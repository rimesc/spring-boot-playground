package io.github.rimesc.springbootplayground.journal.service;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import io.github.rimesc.springbootplayground.journal.model.Entry;
import io.github.rimesc.springbootplayground.journal.persistence.EntryDocument;

/**
 * Maps {@link EntryDocument journal entries} to corresponding API {@link Entry resources}.
 */
@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR)
public interface EntryMapper {

  /**
   * Maps a journal entry to the corresponding API resource.
   *
   * @param entry entry to map
   * @return an API resource representing the entry
   */
  @Mapping(source = "createdBy", target = "author")
  @Mapping(source = "createdAt", target = "created")
  @Mapping(source = "lastModifiedAt", target = "edited")
  Entry entryToResource(EntryDocument entry);

}
