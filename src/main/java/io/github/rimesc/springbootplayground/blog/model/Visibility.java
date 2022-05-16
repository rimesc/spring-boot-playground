package io.github.rimesc.springbootplayground.blog.model;

/**
 * Visibility level of an article.
 */
public enum Visibility {

  /**
   * Article is visible to all logged-in users.
   */
  PUBLIC,

  /**
   * Article is visible only to its author.
   */
  PRIVATE

}
