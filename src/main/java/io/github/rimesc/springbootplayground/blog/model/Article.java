package io.github.rimesc.springbootplayground.blog.model;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.Nullable;

/**
 * MongoDB document representing an article.
 *
 * @param id         a unique identifier
 * @param title      title of the article
 * @param author     identifies the author of the article
 * @param createdAt  time when the article was created
 * @param editedAt   (optional) time when the article was last edited
 * @param tags       list of tags
 * @param visibility whether the article is public or private
 * @param version    incrementing version number
 */
@Document
public record Article(
  @Id String id,
  String title,
  String author,
  Instant createdAt,
  @Nullable Instant editedAt,
  List<String> tags,
  Visibility visibility,
  int version
) {

  /**
   * Initial value for the {@link #version() version} property, representing the unedited version of an article.
   */
  public static int INITIAL_VERSION = 0;

  /**
   * Initial value for the {@link #editedAt() editedAt} property, representing an unedited article.
   */
  @Nullable
  public static Instant UNMODIFIED = null;

}
