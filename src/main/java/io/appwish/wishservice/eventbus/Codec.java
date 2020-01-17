package io.appwish.wishservice.eventbus;

import io.appwish.wishservice.model.Wish;
import io.appwish.wishservice.model.input.UpdateWishInput;
import io.appwish.wishservice.model.input.WishInput;
import io.appwish.wishservice.model.query.AllWishQuery;
import io.appwish.wishservice.model.query.WishQuery;
import io.appwish.wishservice.model.reply.AllWishReply;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.MessageCodec;

/**
 * These codecs can be used to enable passing custom Java objects on the local event bus.
 *
 * To enable T type to be passed via the event bus, just create a new {@link LocalReferenceCodec}.
 *
 * It's not enough to add the codec here - you need to register them on the event bus using {@link
 * EventBus#registerCodec(MessageCodec)}.
 */
public enum Codec {
  UPDATE_WISH_INPUT(new LocalReferenceCodec<>(UpdateWishInput.class)),
  WISH(new LocalReferenceCodec<>(Wish.class)),
  ALL_WISH_REPLY(new LocalReferenceCodec<>(AllWishReply.class)),
  ALL_WISH_QUERY(new LocalReferenceCodec<>(AllWishQuery.class)),
  WISH_QUERY(new LocalReferenceCodec<>(WishQuery.class)),
  WISH_INPUT(new LocalReferenceCodec<>(WishInput.class));

  private final LocalReferenceCodec codec;

  Codec(final LocalReferenceCodec codec) {
    this.codec = codec;
  }

  public <T> LocalReferenceCodec<T> getCodec() {
    return codec;
  }

  public String getCodecName() {
    return codec.name();
  }
}
