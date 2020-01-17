package io.appwish.wishservice.model.reply;

import io.appwish.grpc.WishReplyProto;
import io.appwish.wishservice.model.Wish;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * Represents data to return for single wish query.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(WishReplyProto.class)
public class WishReply {

  @ProtoField
  private Wish wish;

  public WishReply(final Wish wish) {
    this.wish = wish;
  }

  public WishReply() {
  }

  public Wish getWish() {
    return wish;
  }

  public void setWish(final Wish wish) {
    this.wish = wish;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WishReply wishReply = (WishReply) o;
    return Objects.equals(wish, wishReply.wish);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wish);
  }

  @Override
  public String toString() {
    return "WishReply{" +
      "wish=" + wish +
      '}';
  }
}
