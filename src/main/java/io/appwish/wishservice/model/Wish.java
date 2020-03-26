package io.appwish.wishservice.model;

import io.appwish.grpc.WishProto;

import java.util.Objects;

import com.google.protobuf.Timestamp;

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
  private String markdown;

  @ProtoField
  private String html;

  @ProtoField
  private String coverImageUrl;

  // TODO decide on ID type
  @ProtoField
  private String authorId;

  @ProtoField
  private String slug;

  @ProtoField
  private Timestamp createdAt;

  @ProtoField
  private Timestamp updatedAt;

  public Wish(final long id, final String title, final String markdown, final String coverImageUrl, final String authorId, final String slug, final String html, final Timestamp createdAt, final Timestamp updatedAt) {
    this.id = id;
    this.title = title;
    this.markdown = markdown;
    this.coverImageUrl = coverImageUrl;
    this.authorId = authorId;
    this.slug = slug;
    this.html = html;
    this.updatedAt = updatedAt;
    this.createdAt = createdAt;
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

  public String getMarkdown() {
    return markdown;
  }

  public void setMarkdown(String markdown) {
    this.markdown = markdown;
  }

  public void setCoverImageUrl(String coverImageUrl) {
    this.coverImageUrl = coverImageUrl;
  }

  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  public String  getAuthorId() {
    return authorId;
  }

  public void setAuthorId(String authorId) {
    this.authorId = authorId;
  }

  public String getSlug() {
    return slug;
  }

  public void setSlug(String slug) {
    this.slug = slug;
  }

  public String getHtml() {
    return html;
  }

  public void setHtml(String html) {
    this.html = html;
  }

  public Timestamp getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Timestamp createdAt) {
    this.createdAt = createdAt;
  }

  public Timestamp getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Timestamp updatedAt) {
    this.updatedAt = updatedAt;
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
      markdown.equals(wish.markdown) &&
      Objects.equals(html, wish.html) &&
      Objects.equals(coverImageUrl, wish.coverImageUrl) &&
      slug.equals(wish.slug) &&
      Objects.equals(createdAt, wish.createdAt) &&
      Objects.equals(updatedAt, wish.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects
      .hash(id, title, markdown, html, coverImageUrl, authorId, slug, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "Wish{" +
      "id=" + id +
      ", title='" + title + '\'' +
      ", markdown='" + markdown + '\'' +
      ", html='" + html + '\'' +
      ", coverImageUrl='" + coverImageUrl + '\'' +
      ", authorId=" + authorId +
      ", slug='" + slug + '\'' +
      ", createdAt=" + createdAt +
      ", updatedAt=" + updatedAt +
      '}';
  }
}
