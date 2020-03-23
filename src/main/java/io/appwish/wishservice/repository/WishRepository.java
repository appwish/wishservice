package io.appwish.wishservice.repository;

import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.input.WishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.model.query.WishQuery;
import io.vertx.core.Future;
import java.util.List;
import java.util.Optional;

/**
 * Interface for interaction with wish persistence layer
 */
public interface WishRepository {

  Future<List<Wish>> findAll(final AllWishQuery query);

  Future<Optional<Wish>> findOne(final WishQuery query);

  Future<Wish> addOne(final WishInput input, final String authorId);

  Future<Boolean> deleteOne(final WishQuery query);

  Future<Optional<Wish>> updateOne(final UpdateWishInput input);
}
