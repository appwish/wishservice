package io.appwish.wishservice.service;

import io.appwish.grpc.*;
import io.appwish.wishservice.repository.WishRepository;
import io.vertx.core.Promise;

public class WishService extends WishServiceGrpc.WishServiceVertxImplBase {

  private final WishRepository wishRepository;

  public WishService(final WishRepository wishRepository) {
    this.wishRepository = wishRepository;
  }

  @Override
  public void getWish(final WishQuery request, final Promise<WishReply> response) {
    wishRepository.findOne(request.getId()).future().setHandler(event -> {
      if (event.succeeded()) {
        response.complete(WishReply.newBuilder().setWish(event.result().orElse(null)).build());
      } else {
        response.fail(event.cause());
      }
    });
  }

  @Override
  public void getAllWish(final AllWishQuery request, final Promise<AllWishReply> response) {
    wishRepository.findAll().future().setHandler(event -> {
      if (event.succeeded()) {
        response.complete(AllWishReply.newBuilder().addAllWishes(event.result()).build());
      } else {
        response.fail(event.cause());
      }
    });
  }
}
