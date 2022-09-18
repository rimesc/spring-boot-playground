package io.github.rimesc.springbootplayground.journal.service;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static org.springframework.data.domain.Sort.sort;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.rimesc.springbootplayground.journal.model.Entry;
import io.github.rimesc.springbootplayground.journal.persistence.EntryDocument;
import io.github.rimesc.springbootplayground.journal.persistence.EntryRepository;
import io.github.rimesc.springbootplayground.util.IdFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service providing transactional database access to journal entries.
 */
@Transactional
@Service
public class EntryService {

  private final EntryRepository repository;
  private final IdFactory uniqueId;
  private final EntryMapper mapper;

  @Autowired
  public EntryService(final EntryRepository repository, final IdFactory uniqueId, final EntryMapper mapper) {
    this.repository = repository;
    this.uniqueId = uniqueId;
    this.mapper = mapper;
  }

  public Mono<Entry> createEntry(final String title, final String content) {
    try {
      final EntryDocument entry = new EntryDocument(uniqueId.create(), requireNonNull(title), requireNonNull(content));
      return repository.save(entry).map(mapper::entryToResource);
    }
    catch (final RuntimeException e) {
      return Mono.error(e);
    }
  }

  @Transactional(readOnly = true)
  public Mono<Entry> getEntry(final String id) {
    return repository.findById(id).map(mapper::entryToResource);
  }

  @Transactional(readOnly = true)
  public Flux<Entry> listEntries() {
    return listEntries(sort(EntryDocument.class).by(EntryDocument::getTitle).ascending());
  }

  private Flux<Entry> listEntries(final Sort sort) {
    return repository.listEntries(sort).map(mapper::entryToResource);
  }

  public Mono<Entry> editEntry(final String id, @Nullable final String newTitle, @Nullable final String newContent) {
    return repository.findById(id).flatMap(entry -> doEditEntry(entry, newTitle, newContent)).map(mapper::entryToResource);
  }

  private Mono<EntryDocument> doEditEntry(final EntryDocument entry, @Nullable final String newTitle, @Nullable final String newContent) {
    entry.setTitle(requireNonNullElse(newTitle, entry.getTitle()));
    entry.setContent(requireNonNullElse(newContent, entry.getContent()));
    return repository.save(entry);
  }

}
