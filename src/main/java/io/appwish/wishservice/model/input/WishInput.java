package io.appwish.wishservice.model.input;

import io.appwish.grpc.WishInputProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used for inserting new wishes to the database.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(WishInputProto.class)
public class WishInput {

  @ProtoField
  private String title;

  @ProtoField
  private String content;

  @ProtoField
  private String coverImageUrl;

  public WishInput(final String title, final String content, final String coverImageUrl) {
    this.title = title;
    this.content = content;
    this.coverImageUrl = coverImageUrl;
  }

  public WishInput() {
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WishInput wishInput = (WishInput) o;
    return title.equals(wishInput.title) &&
      content.equals(wishInput.content) &&
      Objects.equals(coverImageUrl, wishInput.coverImageUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, content, coverImageUrl);
  }

  @Override
  public String toString() {
    return "WishInput{" +
      "title='" + title + '\'' +
      ", content='" + content + '\'' +
      ", coverImageUrl='" + coverImageUrl + '\'' +
      '}';
  }
}
