package io.github.rimesc.springbootplayground.journal.web;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.rimesc.springbootplayground.journal.model.Entry;
import io.github.rimesc.springbootplayground.journal.service.EntryService;
import io.github.rimesc.springbootplayground.journal.web.validation.ValidationGroups.Create;
import io.github.rimesc.springbootplayground.journal.web.validation.ValidationGroups.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller for the Journal Entries API endpoints.
 */
@RestController
@RequestMapping("/api/journal/entries")
@Validated
class EntryController {

  private final EntryService entryService;

  @Autowired
  EntryController(final EntryService entryService) {
    this.entryService = entryService;
  }

  /**
   * Lists all the available journal entries.
   *
   * @return a list of journal entries
   */
  @GetMapping("/")
  Flux<Entry> listEntries() {
    return entryService.listEntries();
  }

  /**
   * Gets a specific journal entry.
   *
   * @param id unique identifier of the entry to get
   * @return the entry, or nothing if there is no entry with the specified ID
   */
  @GetMapping("/{id}")
  Mono<Entry> getEntry(@PathVariable final String id) {
    return getEntryOrNotFound(id);
  }

  private Mono<Entry> getEntryOrNotFound(final String id) {
    return entryService.getEntry(id).switchIfEmpty(entryNotFound(id));
  }

  private Mono<Entry> entryNotFound(final String id) {
    return Mono.error(() -> new ResponseStatusException(NOT_FOUND, "No entry found with ID: " + id));
  }

  /**
   * Creates a new journal entry.
   * <p>
   *   The request body may contain the following fields (those marked * are required):
   * </p>
   * <dl>
   *   <dt>title*</dt>
   *   <dd>title of the entry</dd>
   *   <dt>content</dt>
   *   <dd>text content of the entry</dd>
   * </dl>
   *
   * @param entry resource describing the entry to create
   * @return the created entry
   */
  @PostMapping("/")
  @ResponseStatus(CREATED)
  Mono<Entry> createEntry(@RequestBody @Validated(Create.class) final Entry entry) {
    return entryService.createEntry(entry.getTitle(), entry.getContent());
  }

  /**
   * Edits an existing journal entry.
   * <p>
   *   The request body may contain the following fields:
   * </p>
   * <dl>
   *   <dt>title</dt>
   *   <dd>title of the entry</dd>
   *   <dt>content</dt>
   *   <dd>text content of the entry</dd>
   * </dl>
   * <p>
   *   Fields that are absent from the request body are not modified.
   * </p>
   *
   * @param id unique identifier of the entry to update
   * @param edits resource describing the edits to apply to the entry
   * @return the edited entry
   */
  @PutMapping("/{id}")
  Mono<Entry> editEntry(@PathVariable final String id, @RequestBody @Validated(Update.class) final Entry edits) {
    return entryService.editEntry(id, edits.getTitle(), edits.getContent()).switchIfEmpty(entryNotFound(id));
  }

}
