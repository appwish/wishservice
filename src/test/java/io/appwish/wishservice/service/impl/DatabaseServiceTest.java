package io.appwish.wishservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.appwish.wishservice.TestData;
import io.appwish.wishservice.eventbus.Address;
import io.appwish.wishservice.eventbus.EventBusConfigurer;
import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.repository.WishRepository;
import io.appwish.wishservice.service.DatabaseService;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class DatabaseServiceTest {

  private io.appwish.wishservice.service.DatabaseService databaseService;
  private WishRepository wishRepository;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) {
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    wishRepository = mock(WishRepository.class);
    databaseService = new DatabaseService(vertx.eventBus(), wishRepository);
    databaseService.registerEventBusEventHandlers();
    util.registerCodecs();
    context.completeNow();
  }

  @Test
  void should_reply_all_wishes(final Vertx vertx, final VertxTestContext context) {
    // given
    when(wishRepository.findAll(TestData.ALL_WISH_QUERY))
      .thenReturn(Future.succeededFuture(TestData.WISHES));

    // when
    vertx.eventBus().<List<Wish>>request(Address.FIND_ALL_WISHES.get(), TestData.ALL_WISH_QUERY,
      event -> {

        // then
        context.verify(() -> {
          assertEquals(TestData.WISHES, event.result().body());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_getting_all_wishes(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(wishRepository.findAll(TestData.ALL_WISH_QUERY))
      .thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<AllWishQuery>request(Address.FIND_ALL_WISHES.get(), TestData.ALL_WISH_QUERY,
      event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_reply_added_wish(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(wishRepository.addOne(TestData.WISH_INPUT_1))
      .thenReturn(Future.succeededFuture(TestData.WISH_4));

    // when
    vertx.eventBus().<Wish>request(Address.CREATE_ONE_WISH.get(), TestData.WISH_INPUT_1, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.WISH_4, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_adding_wish(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(wishRepository.addOne(TestData.WISH_INPUT_1))
      .thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Wish>request(Address.CREATE_ONE_WISH.get(), TestData.WISH_INPUT_1, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.failed());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_true_if_deleted_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    when(wishRepository.deleteOne(TestData.WISH_QUERY))
      .thenReturn(Future.succeededFuture(true));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_WISH.get(), TestData.WISH_QUERY, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertEquals(true, event.result().body());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_false_if_not_deleted_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    when(wishRepository.deleteOne(TestData.WISH_QUERY))
      .thenReturn(Future.succeededFuture(false));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_WISH.get(),
      TestData.WISH_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertEquals(false, event.result().body());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_deleting_wish(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(wishRepository.deleteOne(TestData.WISH_QUERY))
      .thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Boolean>request(Address.DELETE_ONE_WISH.get(),
      TestData.WISH_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_found_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    when(wishRepository.findOne(TestData.WISH_QUERY))
      .thenReturn(Future.succeededFuture(Optional.of(TestData.WISH_1)));

    // when
    vertx.eventBus().<Optional<Wish>>request(Address.FIND_ONE_WISH.get(), TestData.WISH_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertTrue(event.result().body().isPresent());
          assertEquals(TestData.WISH_1, event.result().body().get());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_empty_if_wish_not_found(final Vertx vertx, final VertxTestContext context) {
    // given
    when(wishRepository.findOne(TestData.WISH_QUERY))
      .thenReturn(Future.succeededFuture(Optional.empty()));

    // when
    vertx.eventBus().<Optional<Wish>>request(Address.FIND_ONE_WISH.get(),
      TestData.WISH_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.succeeded());
          assertTrue(event.result().body().isEmpty());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_error_on_error_finding_wish(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(wishRepository.findOne(TestData.WISH_QUERY))
      .thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Optional<Wish>>request(Address.FIND_ONE_WISH.get(),
      TestData.WISH_QUERY, event -> {

        // then
        context.verify(() -> {
          assertTrue(event.failed());
          context.completeNow();
        });
      });
  }

  @Test
  void should_return_updated_wish(final Vertx vertx, final VertxTestContext context) {
    // given
    final UpdateWishInput input = TestData.UPDATE_WISH_INPUT;
    when(wishRepository.updateOne(input))
      .thenReturn(Future.succeededFuture(Optional.of(TestData.WISH_4)));

    // when
    vertx.eventBus().<Optional<Wish>>request(Address.UPDATE_ONE_WISH.get(), input, event -> {

      // then
      context.verify(() -> {
        assertEquals(TestData.WISH_4, event.result().body().get());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_empty_if_not_updated(final Vertx vertx, final VertxTestContext context) {
    // given
    when(wishRepository.updateOne(TestData.UPDATE_WISH_INPUT)).thenReturn(Future.succeededFuture(Optional.empty()));

    // when
    vertx.eventBus().<Optional<Wish>>request(Address.UPDATE_ONE_WISH.get(), TestData.UPDATE_WISH_INPUT, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        assertTrue(event.result().body().isEmpty());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_on_error_updating_wish(final Vertx vertx,
    final VertxTestContext context) {
    // given
    when(wishRepository.updateOne(TestData.UPDATE_WISH_INPUT)).thenReturn(Future.failedFuture(new AssertionError()));

    // when
    vertx.eventBus().<Optional<Wish>>request(Address.UPDATE_ONE_WISH.get(), TestData.UPDATE_WISH_INPUT, event -> {

      // then
      context.verify(() -> {
        assertTrue(event.failed());
        context.completeNow();
      });
    });
  }
}
