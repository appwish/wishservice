package io.appwish.wishservice.repository.impl;

import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.input.WishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.model.query.WishQuery;
import io.appwish.wishservice.repository.WishRepository;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Enables storing wishes in PostgreSQL
 */
public class PostgresWishRepository implements WishRepository {

  private static final String ID_COLUMN = "id";
  private static final String TITLE_COLUMN = "title";
  private static final String CONTENT_COLUMN = "content";
  private static final String COVER_IMAGE_URL_COLUMN = "cover_image_url";
  private static final String AUTHOR_ID_COLUMN = "author_id";
  private static final String URL_COLUMN = "url";

  private final PgPool client;

  public PostgresWishRepository(final PgPool client) {
    this.client = client;
  }

  @Override
  public Future<List<Wish>> findAll(final AllWishQuery query) {
    final Promise<List<Wish>> promise = Promise.promise();

    client.preparedQuery(Query.FIND_ALL_WISH.sql(), event -> {
      if (event.succeeded()) {
        final List<Wish> wishes = StreamSupport
          .stream(event.result().spliterator(), false)
          .map(this::wishFromRow)
          .collect(Collectors.toList());
        promise.complete(wishes);
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Optional<Wish>> findOne(final WishQuery query) {
    final Promise<Optional<Wish>> promise = Promise.promise();

    client.preparedQuery(Query.FIND_ONE_WISH.sql(), Tuple.of(query.getId()), event -> {
      if (event.succeeded()) {
        if (event.result().iterator().hasNext()) {
          final Row firstRow = event.result().iterator().next();
          final Wish wish = wishFromRow(firstRow);
          promise.complete(Optional.of(wish));
        } else {
          promise.complete(Optional.empty());
        }
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Wish> addOne(final WishInput wish) {
    final Promise<Wish> promise = Promise.promise();

    client.preparedQuery(Query.INSERT_WISH_QUERY.sql(),
      Tuple.of(wish.getTitle(), wish.getContent(), wish.getCoverImageUrl(), wish.getAuthorId(), "hardcoded-for-now"),
      event -> {
        if (event.succeeded()) {
          if (event.result().iterator().hasNext()) {
            final Row row = event.result().iterator().next();
            promise.complete(wishFromRow(row));
          } else {
            promise.fail(new AssertionError("Adding a wish should always succeed"));
          }
        } else {
          promise.fail(event.cause());
        }
      });

    return promise.future();
  }

  @Override
  public Future<Boolean> deleteOne(final WishQuery query) {
    final Promise<Boolean> promise = Promise.promise();

    client.preparedQuery(Query.DELETE_WISH_QUERY.sql(), Tuple.of(query.getId()), event -> {
      if (event.succeeded()) {
        if (event.result().rowCount() == 1) {
          promise.complete(true);
        } else {
          promise.complete(false);
        }
      } else {
        promise.fail(event.cause());
      }
    });

    return promise.future();
  }

  @Override
  public Future<Optional<Wish>> updateOne(final UpdateWishInput wish) {
    final Promise<Optional<Wish>> promise = Promise.promise();

    client.preparedQuery(Query.UPDATE_WISH_QUERY.sql(),
      Tuple.of(wish.getTitle(), wish.getContent(), wish.getCoverImageUrl(), wish.getId()),
      event -> {
        if (event.succeeded() && event.result().rowCount() == 1) {
          final Row row = event.result().iterator().next();
          promise.complete(Optional.of(wishFromRow(row)));
        } else if (event.succeeded() && event.result().rowCount() == 0) {
          promise.complete(Optional.empty());
        } else if (event.failed()) {
          promise.fail(event.cause());
        }
      });

    return promise.future();
  }

  private Wish wishFromRow(final Row row) {
    return new Wish(
      row.getLong(ID_COLUMN),
      row.getString(TITLE_COLUMN),
      row.getString(CONTENT_COLUMN),
      row.getString(COVER_IMAGE_URL_COLUMN),
      row.getLong(AUTHOR_ID_COLUMN),
      row.getString(URL_COLUMN));
  }
}
