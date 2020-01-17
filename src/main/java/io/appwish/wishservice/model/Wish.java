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
  private String content;

  @ProtoField
  private String coverImageUrl;

  @ProtoField
  private long authorId;

  @ProtoField
  private String url;

  public Wish(final long id, final String title, final String content, final String coverImageUrl, final long authorId, final String url) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.coverImageUrl = coverImageUrl;
    this.authorId = authorId;
    this.url = url;
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

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public long getAuthorId() {
    return authorId;
  }

  public void setAuthorId(long authorId) {
    this.authorId = authorId;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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
      authorId == wish.authorId &&
      title.equals(wish.title) &&
      content.equals(wish.content) &&
      Objects.equals(coverImageUrl, wish.coverImageUrl) &&
      url.equals(wish.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, title, content, coverImageUrl, authorId, url);
  }
}
