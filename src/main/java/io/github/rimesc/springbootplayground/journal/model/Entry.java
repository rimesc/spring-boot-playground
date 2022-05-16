package io.github.rimesc.springbootplayground.journal.model;

import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.validator.constraints.Length;

import io.github.rimesc.springbootplayground.journal.web.validation.ValidationGroups.Create;
import io.github.rimesc.springbootplayground.journal.web.validation.ValidationGroups.Update;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * API resource representing a journal entry.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Entry {

  /**
   * Unique identifier of this entry.
   */
  @Null(groups = {Create.class, Update.class})
  private String id;

  /**
   * Title of this entry.
   */
  @NotNull(groups = Create.class)
  @Length(min = 1, max = 256)
  private String title;

  /**
   * Text content of the entry.
   */
  private String content;

  /**
   * Author of this entry.
   */
  @Null(groups = {Create.class, Update.class})
  private String author;

  /**
   * Time when this entry was created.
   */
  @Null(groups = {Create.class, Update.class})
  private Instant created;

  /**
   * Optionally, time when the entry was last edited.
   */
  @Null(groups = {Create.class, Update.class})
  private Instant edited;

}
