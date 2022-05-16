package io.github.rimesc.springbootplayground.blog.web.resource;

import java.time.Instant;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.annotation.Id;

import io.github.rimesc.springbootplayground.blog.model.Visibility;
import io.github.rimesc.springbootplayground.blog.web.validation.ValidationGroups;

/**
 * API resource representing an article.
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
public record ArticleResource(
  @Id
  @NotNull(groups = ValidationGroups.Read.class)
  @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
  String id,
  @NotNull(groups = {ValidationGroups.Read.class, ValidationGroups.Create.class})
  @Length(min = 1, max = 256)
  String title,
  @NotNull(groups = ValidationGroups.Read.class)
  @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
  String author,
  @NotNull(groups = ValidationGroups.Read.class)
  @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
  Instant createdAt,
  @Null(groups = {ValidationGroups.Create.class, ValidationGroups.Update.class})
  Instant editedAt,
  List<String> tags,
  @NotNull(groups = ValidationGroups.Read.class)
  Visibility visibility,
  @NotNull(groups = {ValidationGroups.Read.class, ValidationGroups.Update.class})
  @Null(groups = ValidationGroups.Create.class)
  Integer version
) {
}
