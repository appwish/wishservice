package io.appwish.wishservice.model.reply;

import io.appwish.grpc.AllWishReplyProto;
import io.appwish.wishservice.model.Wish;
import java.util.List;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * Represents data to return for multiple wishes query. Right now it contains just a list of wishes,
 * but later we'll add pagination etc.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(AllWishReplyProto.class)
public class AllWishReply {

  @ProtoField
  private List<Wish> wishes;

  public AllWishReply(final List<Wish> wishes) {
    this.wishes = wishes;
  }

  public AllWishReply() {
  }

  public List<Wish> getWishes() {
    return wishes;
  }

  public void setWishes(final List<Wish> wishes) {
    this.wishes = wishes;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AllWishReply that = (AllWishReply) o;
    return wishes.equals(that.wishes);
  }

  @Override
  public int hashCode() {
    return Objects.hash(wishes);
  }

  @Override
  public String toString() {
    return "AllWishReply{" +
      "wishes=" + wishes +
      '}';
  }
}
