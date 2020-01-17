package io.appwish.wishservice.model;

import io.appwish.grpc.WishProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(WishProto.class)
public class Wish {

  @ProtoField
  private long id;

  @ProtoField
  private String title;

  @ProtoField
  private String description;

  @ProtoField
  private String coverImageUrl;

  public Wish(
    final long id,
    final String title,
    final String description,
    final String coverImageUrl
  ) {
    this.id = id;
    this.title = title;
    this.description = description;
    this.coverImageUrl = coverImageUrl;
  }

  public Wish() {
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Wish wish = (Wish) o;
    return id == wish.id &&
      title.equals(wish.title) &&
      description.equals(wish.description) &&
      Objects.equals(coverImageUrl, wish.coverImageUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, description, coverImageUrl);
  }

  @Override
  public String toString() {
    return "Wish{" +
      "id=" + id +
      ", title='" + title + '\'' +
      ", description='" + description + '\'' +
      ", coverImageUrl='" + coverImageUrl + '\'' +
      '}';
  }
}
