package io.appwish.wishservice.repository;

import io.appwish.grpc.Wish;
import io.vertx.core.Promise;

import java.util.List;
import java.util.Optional;

public interface WishRepository {
  Promise<List<Wish>> findAll();
  Promise<Optional<Wish>> findOne(final String id);
  Promise<Boolean> add(final Wish appWish);
  Promise<Boolean> delete(final String id);
}
