package io.appwish.wishservice.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.appwish.grpc.AllWishQueryProto;
import io.appwish.grpc.AllWishReplyProto;
import io.appwish.grpc.UpdateWishInputProto;
import io.appwish.grpc.WishDeleteReplyProto;
import io.appwish.grpc.WishInputProto;
import io.appwish.grpc.WishQueryProto;
import io.appwish.grpc.WishReplyProto;
import io.appwish.wishservice.TestData;
import io.appwish.wishservice.eventbus.Address;
import io.appwish.wishservice.eventbus.Codec;
import io.appwish.wishservice.eventbus.EventBusConfigurer;
import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.service.GrpcServiceImpl;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import java.util.Optional;
import java.util.stream.Collectors;
import net.badata.protobuf.converter.Converter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
class GrpcServiceTest {

  private GrpcServiceImpl grpcService;

  @BeforeEach
  void setUp(final Vertx vertx, final VertxTestContext context) {
    final EventBusConfigurer eventBusConfigurer = new EventBusConfigurer(vertx.eventBus());
    grpcService = new GrpcServiceImpl(vertx.eventBus());
    eventBusConfigurer.registerCodecs();
    context.completeNow();
  }

  @Test
  void should_return_all_wishes(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<AllWishReplyProto> promise = Promise.promise();
    final AllWishQueryProto query = AllWishQueryProto.newBuilder().build();
    vertx.eventBus().consumer(Address.FIND_ALL_WISHES.get(), event -> {
      event.reply(TestData.WISHES, new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
    });

    // when
    grpcService.getAllWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.WISHES, promise.future().result().getWishesList().stream()
          .map(it -> Converter.create().toDomain(Wish.class, it)).collect(Collectors.toList()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_when_exception_occured_while_getting_all_wishes(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<AllWishReplyProto> promise = Promise.promise();
    final AllWishQueryProto query = AllWishQueryProto.newBuilder().build();
    vertx.eventBus().consumer(Address.FIND_ALL_WISHES.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.getAllWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_one_wish(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final WishQueryProto query = WishQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ONE_WISH.get(), event -> {
      event.reply(Optional.of(TestData.WISH_1),
        new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
    });

    // when
    grpcService.getWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.WISH_1,
          Converter.create().toDomain(Wish.class, promise.future().result().getWish()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_empty_wish_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final WishQueryProto query = WishQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ONE_WISH.get(), event -> {
      event.reply(Optional.empty(), new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
    });

    // when
    grpcService.getWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertNull(promise.future().result());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_getting_one_wish(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final WishQueryProto query = WishQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ONE_WISH.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.getWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_add_and_return_back_wish(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final WishInputProto inputProto = WishInputProto.newBuilder()
      .setTitle(TestData.SOME_TITLE)
      .setDescription(TestData.SOME_DESCRIPTION)
      .setCoverImageUrl(TestData.SOME_URL)
      .build();
    vertx.eventBus().consumer(Address.CREATE_ONE_WISH.get(), event -> {
      event.reply(TestData.WISH_1);
    });

    // when
    grpcService.createWish(inputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(TestData.WISH_1,
          Converter.create().toDomain(Wish.class, promise.future().result().getWish()));
        context.completeNow();
      });
    });
  }

  @Test
  void should_report_error_while_error_creating_wish(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final WishInputProto inputProto = WishInputProto.newBuilder()
      .setTitle(TestData.SOME_TITLE)
      .setDescription(TestData.SOME_DESCRIPTION)
      .setCoverImageUrl(TestData.SOME_URL)
      .build();
    vertx.eventBus().consumer(Address.CREATE_ONE_WISH.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.createWish(inputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }

  @Test
  void should_update_and_return_updated_wish(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final UpdateWishInputProto updateWishInputProto = UpdateWishInputProto.newBuilder()
      .setId(TestData.WISH_3.getId())
      .setCoverImageUrl(TestData.WISH_3.getCoverImageUrl())
      .setTitle(TestData.WISH_3.getTitle())
      .setDescription(TestData.WISH_3.getDescription())
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_WISH.get(), event -> {
      event.reply(Optional.of(TestData.WISH_3),
        new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
    });

    // when
    grpcService.updateWish(updateWishInputProto, promise);
    promise.future().setHandler(event -> {

      // then
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertEquals(Converter.create().toDomain(Wish.class, promise.future().result().getWish()),
          TestData.WISH_3);
        context.completeNow();
      });

    });
  }

  @Test
  void should_not_update_and_return_empty_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final UpdateWishInputProto updateWishInputProto = UpdateWishInputProto.newBuilder()
      .setId(TestData.WISH_3.getId())
      .setCoverImageUrl(TestData.WISH_3.getCoverImageUrl())
      .setTitle(TestData.WISH_3.getTitle())
      .setDescription(TestData.WISH_3.getDescription())
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_WISH.get(), event -> {
      event.reply(Optional.empty(), new DeliveryOptions().setCodecName(Codec.WISH.getCodecName()));
    });

    // when
    grpcService.updateWish(updateWishInputProto, promise);
    promise.future().setHandler(event -> {

      // then
      context.verify(() -> {
        assertTrue(event.succeeded());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_updating_wish(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishReplyProto> promise = Promise.promise();
    final UpdateWishInputProto updateWishInputProto = UpdateWishInputProto.newBuilder()
      .setId(TestData.WISH_3.getId())
      .setCoverImageUrl(TestData.WISH_3.getCoverImageUrl())
      .setTitle(TestData.WISH_3.getTitle())
      .setDescription(TestData.WISH_3.getDescription())
      .build();
    vertx.eventBus().consumer(Address.UPDATE_ONE_WISH.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.updateWish(updateWishInputProto, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });

    });
  }

  @Test
  void should_delete_wish_and_return_true(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishDeleteReplyProto> promise = Promise.promise();
    final WishQueryProto query = WishQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.DELETE_ONE_WISH.get(), event -> {
      event.reply(true);
    });

    // when
    grpcService.deleteWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertTrue(promise.future().result().getDeleted());
        context.completeNow();
      });
    });
  }

  @Test
  void should_not_delete_and_return_false_if_not_found(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishDeleteReplyProto> promise = Promise.promise();
    final WishQueryProto query = WishQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.DELETE_ONE_WISH.get(), event -> {
      event.reply(false);
    });

    // when
    grpcService.deleteWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().succeeded());
        assertFalse(promise.future().result().getDeleted());
        context.completeNow();
      });
    });
  }

  @Test
  void should_return_error_while_error_deleting_wish(final Vertx vertx, VertxTestContext context) {
    // given
    final Promise<WishDeleteReplyProto> promise = Promise.promise();
    final WishQueryProto query = WishQueryProto.newBuilder().setId(TestData.SOME_ID).build();
    vertx.eventBus().consumer(Address.FIND_ALL_WISHES.get(), event -> {
      event.fail(0, TestData.ERROR_MESSAGE);
    });

    // when
    grpcService.deleteWish(query, promise);

    // then
    promise.future().setHandler(event -> {
      context.verify(() -> {
        assertTrue(promise.future().failed());
        assertTrue(promise.future().cause() instanceof ReplyException);
        context.completeNow();
      });
    });
  }
}
