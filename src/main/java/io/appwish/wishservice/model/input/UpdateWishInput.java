package io.appwish.wishservice.model.input;

import io.appwish.grpc.UpdateWishInputProto;
import java.util.Objects;
import net.badata.protobuf.converter.annotation.ProtoClass;
import net.badata.protobuf.converter.annotation.ProtoField;

/**
 * This type should be used as input for updates of wishes in the database.
 *
 * {@link ProtoClass} and {@link ProtoField} annotations are used by {@link
 * net.badata.protobuf.converter.Converter} to convert back/forth between protobuf data transfer
 * objects and model objects.
 *
 * The converter requires a POJO with getters, setters and a default constructor.
 */
@ProtoClass(UpdateWishInputProto.class)
public class UpdateWishInput {

  @ProtoField
  private String title;

  @ProtoField
  private String description;

  @ProtoField
  private String coverImageUrl;

  @ProtoField
  private long id;

  public UpdateWishInput(
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

  public UpdateWishInput() {
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
    UpdateWishInput that = (UpdateWishInput) o;
    return id == that.id &&
      title.equals(that.title) &&
      description.equals(that.description) &&
      Objects.equals(coverImageUrl, that.coverImageUrl);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, coverImageUrl, id);
  }

  @Override
  public String toString() {
    return "UpdateWishInput{" +
      "title='" + title + '\'' +
      ", description='" + description + '\'' +
      ", coverImageUrl='" + coverImageUrl + '\'' +
      ", id=" + id +
      '}';
  }
}
