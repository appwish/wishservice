package io.appwish.wishservice.verticle;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.grpc.AllWishQueryProto;
import io.appwish.grpc.WishServiceGrpc;
import io.appwish.wishservice.TestData;
import io.appwish.wishservice.eventbus.Address;
import io.appwish.wishservice.eventbus.Codec;
import io.appwish.wishservice.eventbus.EventBusConfigurer;
import io.grpc.ManagedChannel;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.grpc.VertxChannelBuilder;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class GrpcVerticleTest {

  @Test
  void should_expose_grpc_server(final Vertx vertx, final VertxTestContext context) {
    // given
    final EventBusConfigurer util = new EventBusConfigurer(vertx.eventBus());
    final ManagedChannel channel = VertxChannelBuilder.forAddress(vertx, TestData.APP_HOST, TestData.APP_PORT).usePlaintext(true).build();
    final WishServiceGrpc.WishServiceVertxStub serviceStub = new WishServiceGrpc.WishServiceVertxStub(channel);
    vertx.deployVerticle(new GrpcVerticle(), new DeploymentOptions(), context.completing());
    vertx.eventBus().consumer(Address.FIND_ALL_WISHES.get(),
      event -> event.reply(TestData.WISHES, new DeliveryOptions().setCodecName(Codec.WISH.getCodecName())));

    util.registerCodecs();

    // when
    serviceStub.getAllWish(AllWishQueryProto.newBuilder().build(), event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        context.completeNow();
      });
    });
  }
}
