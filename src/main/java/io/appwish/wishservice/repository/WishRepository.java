package io.appwish.wishservice.repository;

import io.appwish.grpc.Wish;
import io.vertx.core.Promise;

import java.util.List;
import java.util.Optional;

public interface WishRepository {
  Promise<List<Wish>> findAll();
  Promise<Optional<Wish>> findOne(final String id);
  Promise<Optional<String>> addOne(final Wish wish);
  Promise<Boolean> deleteOne(final String id);
  Promise<Boolean> updateOne(final Wish wish);
}
