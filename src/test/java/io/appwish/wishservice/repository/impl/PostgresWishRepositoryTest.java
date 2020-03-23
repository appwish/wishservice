package io.appwish.wishservice.repository.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.wishservice.TestData;
import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.input.WishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.model.query.WishQuery;
import io.appwish.wishservice.repository.WishRepository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class PostgresWishRepositoryTest {

  private static final String DATABASE_HOST = "localhost";
  private static final String DEFAULT_POSTGRES = "postgres";


  private EmbeddedPostgres postgres;
  private WishRepository repository;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) throws Exception {
    postgres = EmbeddedPostgres.start();

    final PgConnectOptions connectOptions = new PgConnectOptions()
      .setPort(postgres.getPort())
      .setHost(DATABASE_HOST)
      .setDatabase(DEFAULT_POSTGRES)
      .setUser(DEFAULT_POSTGRES)
      .setPassword(DEFAULT_POSTGRES);
    final PoolOptions poolOptions = new PoolOptions().setMaxSize(5);
    final PgPool client = PgPool.pool(connectOptions, poolOptions);

    client.query(Query.CREATE_WISH_TABLE.sql(), context.completing());

    repository = new PostgresWishRepository(client);
  }

  @AfterEach
  void tearDown() throws Exception {
    postgres.close();
  }

  @Test
  void should_be_able_to_store_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    final WishInput wishInput = new WishInput(
      TestData.SOME_TITLE,
      TestData.SOME_MARKDOWN,
      TestData.SOME_COVER_IMAGE_URL);

    // when
    repository.addOne(wishInput, TestData.SOME_AUTHOR_ID)
      .setHandler(event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertEquals(TestData.SOME_AUTHOR_ID, event.result().getAuthorId());
          assertEquals(TestData.SOME_TITLE, event.result().getTitle());
          assertEquals(TestData.SOME_MARKDOWN, event.result().getMarkdown());
          assertEquals(TestData.SOME_COVER_IMAGE_URL, event.result().getCoverImageUrl());
          context.completeNow();
        });
      });
  }

  @Test
  void should_be_able_to_read_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    final WishInput wishInput = new WishInput(
      TestData.SOME_TITLE,
      TestData.SOME_MARKDOWN,
      TestData.SOME_COVER_IMAGE_URL);
    context.assertComplete(repository.addOne(wishInput, TestData.SOME_AUTHOR_ID)).setHandler(event -> {

      // when
      repository.findOne(new WishQuery(event.result().getId())).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertTrue(query.result().isPresent());
          assertEquals(TestData.SOME_AUTHOR_ID, query.result().get().getAuthorId());
          assertEquals(TestData.SOME_TITLE, query.result().get().getTitle());
          assertEquals(TestData.SOME_MARKDOWN, query.result().get().getMarkdown());
          assertEquals(TestData.SOME_COVER_IMAGE_URL, query.result().get().getCoverImageUrl());
          context.completeNow();
        });
      });
    });
  }

  @Test
  void should_be_able_to_read_multiple_wishes(final Vertx vertx, final VertxTestContext context) {
    // given
    // TODO authorIds
    final Future<Wish> addWish1 = repository.addOne(TestData.WISH_INPUT_1, TestData.SOME_AUTHOR_ID);
    final Future<Wish> addWish2 = repository.addOne(TestData.WISH_INPUT_2, "");
    final Future<Wish> addWish3 = repository.addOne(TestData.WISH_INPUT_3, "");
    final Future<Wish> addWish4 = repository.addOne(TestData.WISH_INPUT_4, "");
    context.assertComplete(CompositeFuture.all(addWish1, addWish2, addWish3, addWish4))
      .setHandler(event -> {

        // when
        repository.findAll(new AllWishQuery()).setHandler(query -> context.verify(() -> {

          // then
          assertTrue(query.succeeded());
          assertEquals(4, query.result().size());
          query.result().forEach(wish -> assertTrue(isInList(wish, TestData.WISHES)));
          context.completeNow();
        }));
      });
  }

  @Test
  void should_not_delete_non_existent_wish(final Vertx vertx, final VertxTestContext context) {
    // when
    repository.deleteOne(new WishQuery(TestData.NON_EXISTING_ID))
      .setHandler(event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertFalse(event.result());
          context.completeNow();
        });
      });
  }

  @Test
  void should_be_able_to_delete_existing_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    context.assertComplete(repository.addOne(TestData.WISH_INPUT_1, TestData.SOME_AUTHOR_ID)).setHandler(event -> {
      final long id = event.result().getId();

      // when
      repository.deleteOne(new WishQuery(id)).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertTrue(query.result());
          context.completeNow();
        });
      });
    });
  }

  @Test
  void should_not_update_non_existent_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    final UpdateWishInput updated = new UpdateWishInput(
      TestData.NON_EXISTING_ID,
      TestData.WISH_2.getTitle(), TestData.WISH_2.getMarkdown(),
      TestData.WISH_2.getCoverImageUrl());

    // when
    repository.updateOne(updated).setHandler(query -> {

      // then
      context.verify(() -> {
        assertTrue(query.succeeded());
        assertTrue(query.result().isEmpty());
        context.completeNow();
      });
    });
  }

  @Test
  void  should_update_existing_wish(final Vertx vertx, final VertxTestContext context)
    throws Exception {
    // given
    context.assertComplete(repository.addOne(TestData.WISH_INPUT_1, TestData.SOME_AUTHOR_ID)).setHandler(event -> {
      final long id = event.result().getId();
      final UpdateWishInput updated = new UpdateWishInput(id, TestData.WISH_2.getTitle(),
        TestData.WISH_1.getMarkdown(), TestData.WISH_1.getCoverImageUrl());

      // when
      repository.updateOne(updated).setHandler(query -> {

        // then
        context.verify(() -> {
          assertTrue(query.succeeded());
          assertTrue(query.result().isPresent());
          assertEquals(TestData.WISH_2.getTitle(), query.result().get().getTitle());
          assertEquals(TestData.WISH_1.getMarkdown(), query.result().get().getMarkdown());
          assertEquals(TestData.WISH_1.getCoverImageUrl(), query.result().get().getCoverImageUrl());
          assertEquals(id, query.result().get().getId());
          context.completeNow();
        });
      });
    });
  }

  @Test
  @Timeout(value = 5, timeUnit = TimeUnit.SECONDS)
  void should_fail_fast_on_postgres_connection_error(final Vertx vertx, final VertxTestContext context) throws Exception {
    // given
    final UpdateWishInput updateWishInput = new UpdateWishInput(
      TestData.SOME_ID,
      TestData.SOME_TITLE,
      TestData.SOME_MARKDOWN,
      TestData.SOME_COVER_IMAGE_URL);

    // database down
    postgres.close();

    // when
    final Future<Wish> addWish = repository.addOne(TestData.WISH_INPUT_1, TestData.SOME_AUTHOR_ID);
    final Future<List<Wish>> findAllWishes = repository.findAll(new AllWishQuery());
    final Future<Optional<Wish>> findOneWish = repository.findOne(new WishQuery(TestData.SOME_ID));
    final Future<Optional<Wish>> updateWish = repository.updateOne(updateWishInput);

    // then
    CompositeFuture.any(addWish, findAllWishes, findOneWish, updateWish).setHandler(event -> {
      if (event.succeeded()) {
        context.failNow(new AssertionError("All queries should fail!"));
      } else {
        context.completeNow();
      }
    });
  }

  private static boolean isInList(final Wish wish, final List<Wish> list) {
    return list.stream().anyMatch(wishFromList ->
        wish.getAuthorId().equals(wishFromList.getAuthorId()) &&
        wish.getMarkdown().equals(wishFromList.getMarkdown()) &&
        wish.getTitle().equals(wishFromList.getTitle()) &&
        wish.getUpdatedAt().equals(wishFromList.getUpdatedAt()) &&
        wish.getCreatedAt().equals(wishFromList.getCreatedAt()) &&
        wish.getHtml().equals(wishFromList.getHtml()) &&
        wish.getId() == wishFromList.getId() &&
        wish.getSlug() == wishFromList.getSlug() &&
        wish.getCoverImageUrl().equals(wishFromList.getCoverImageUrl()));
  }
}
