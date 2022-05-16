package io.github.rimesc.springbootplayground.journal.persistence;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * MongoDB document representing a journal entry.
 */
@Document
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class EntryDocument implements Persistable<String> {

  /** Unique identifier of this entry. */
  @Id
  @NonNull
  private String id;

  /** Title of this entry. */
  @Setter
  @NonNull
  private String title;

  /** Text content of the entry. */
  @Setter
  @NonNull
  private String content;

  /** User who created the entry. */
  @CreatedBy
  private String createdBy;

  /** Time when the entry was created. */
  @CreatedDate
  private Instant createdAt;

  /** User who created the last modified. */
  @LastModifiedBy
  private String lastModifiedBy;

  /** Time when the entry was last modified. */
  @LastModifiedDate
  private Instant lastModifiedAt;

  @Override
  public boolean isNew() {
    return this.createdAt == null;
  }

}
