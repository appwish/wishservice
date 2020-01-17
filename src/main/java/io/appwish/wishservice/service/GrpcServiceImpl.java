package io.appwish.wishservice.service;

import io.appwish.grpc.AllWishQueryProto;
import io.appwish.grpc.AllWishReplyProto;
import io.appwish.grpc.UpdateWishInputProto;
import io.appwish.grpc.WishDeleteReplyProto;
import io.appwish.grpc.WishInputProto;
import io.appwish.grpc.WishProto;
import io.appwish.grpc.WishQueryProto;
import io.appwish.grpc.WishReplyProto;
import io.appwish.grpc.WishServiceGrpc;
import io.appwish.wishservice.eventbus.Address;
import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.input.WishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.model.query.WishQuery;
import io.appwish.wishservice.model.reply.WishDeleteReply;
import io.appwish.wishservice.model.reply.WishReply;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.badata.protobuf.converter.Converter;

/**
 * Handles gRPC server request calls. Sends request on event bus to interact with wish data in the
 * database.
 */
public class GrpcServiceImpl extends WishServiceGrpc.WishServiceVertxImplBase {

  private final EventBus eventBus;
  private final Converter converter;

  public GrpcServiceImpl(final EventBus eventBus) {
    this.eventBus = eventBus;
    this.converter = Converter.create();
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.getWish(...)
   */
  @Override
  public void getWish(final WishQueryProto request, final Promise<WishReplyProto> response) {

    eventBus.<Optional<Wish>>request(
      Address.FIND_ONE_WISH.get(), converter.toDomain(WishQuery.class, request),
      event -> {
        if (event.succeeded() && event.result().body().isPresent()) {
          response.complete(
            converter.toProtobuf(WishReplyProto.class, new WishReply(event.result().body().get())));
        } else if (event.succeeded()) {
          response.complete();
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.getAllWish(...)
   */
  @Override
  public void getAllWish(final AllWishQueryProto request,
    final Promise<AllWishReplyProto> response) {

    eventBus.<List<Wish>>request(
      Address.FIND_ALL_WISHES.get(), converter.toDomain(AllWishQuery.class, request),
      event -> {
        if (event.succeeded()) {
          final List<WishProto> collect = event.result().body().stream()
            .map(it -> converter.toProtobuf(WishProto.class, it))
            .collect(Collectors.toList());
          response.complete(AllWishReplyProto.newBuilder().addAllWishes(collect).build());
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.addWish(...)
   */
  @Override
  public void createWish(final WishInputProto request, final Promise<WishReplyProto> response) {

    eventBus.<Wish>request(
      Address.CREATE_ONE_WISH.get(), converter.toDomain(WishInput.class, request),
      event -> {
        if (event.succeeded()) {
          response.complete(
            converter.toProtobuf(WishReplyProto.class, new WishReply(event.result().body())));
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.updateWish(...)
   */
  @Override
  public void updateWish(final UpdateWishInputProto request,
    final Promise<WishReplyProto> response) {

    eventBus.<Optional<Wish>>request(
      Address.UPDATE_ONE_WISH.get(), converter.toDomain(UpdateWishInput.class, request),
      event -> {
        if (event.succeeded() && event.result().body().isPresent()) {
          response.complete(converter.
            toProtobuf(WishReplyProto.class, new WishReply(event.result().body().get())));
        } else if (event.succeeded()) {
          response.complete();
        } else {
          response.fail(event.cause());
        }
      });
  }

  /**
   * This method gets invoked when other service (app, microservice) invokes stub.deleteWish(...)
   */
  @Override
  public void deleteWish(final WishQueryProto request,
    final Promise<WishDeleteReplyProto> response) {

    eventBus.<Boolean>request(
      Address.DELETE_ONE_WISH.get(), converter.toDomain(WishQuery.class, request),
      event -> {
        if (event.succeeded()) {
          response.complete(converter.toProtobuf(
            WishDeleteReplyProto.class, new WishDeleteReply(event.result().body())));
        } else {
          response.fail(event.cause());
        }
      });
  }
}
