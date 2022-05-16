package io.github.rimesc.springbootplayground.journal.service;

import static io.github.rimesc.springbootplayground.journal.test.MongoTestUtils.assertExists;
import static io.github.rimesc.springbootplayground.journal.test.MongoTestUtils.clear;
import static io.github.rimesc.springbootplayground.journal.test.MongoTestUtils.given;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.ReactiveAuditorAware;

import io.github.rimesc.springbootplayground.journal.model.Entry;
import io.github.rimesc.springbootplayground.journal.persistence.EntryDocument;
import io.github.rimesc.springbootplayground.journal.persistence.EntryRepository;
import io.github.rimesc.springbootplayground.util.IdFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ServiceTest
@DisplayName("EntryService")
public class EntryServiceTest {

  @TestConfiguration
  static public class Config {

    @Bean
    Clock clock() {
      return Clock.fixed(Instant.now(), ZoneId.systemDefault());
    }

    @Bean
    ReactiveAuditorAware<String> auditorAware() {
      return () -> Mono.just("user");
    }

    @Bean
    IdFactory idFactory() {
      return IdFactory.fixed("my_entry");
    }

  }

  @Autowired
  private Clock clock;

  @Autowired
  private EntryRepository repository;

  @Autowired
  private EntryService service;

  @AfterEach
  void tearDown() {
    clear(repository);
  }

  @Nested
  @DisplayName("listEntries()")
  class ListEntries {

    @Test
    @DisplayName("returns all entries ordered by title")
    void listEntries() {
      given(repository,
        new EntryDocument("entry_1", "First Entry", "Content of the first entry."),
        new EntryDocument("entry_2", "Second Entry", "Content of the second entry."),
        new EntryDocument("entry_3", "Third Entry", "Content of the third entry."),
        new EntryDocument("entry_4", "Fourth Entry", "Content of the fourth entry."),
        new EntryDocument("entry_5", "Fifth Entry", "Content of the fifth entry.")
      );
      StepVerifier.create(service.listEntries())
        .expectNext(new Entry("entry_5", "Fifth Entry", "Content of the fifth entry.", "user", now(), null))
        .expectNext(new Entry("entry_1", "First Entry", "Content of the first entry.", "user", now(), null))
        .expectNext(new Entry("entry_4", "Fourth Entry", "Content of the fourth entry.", "user", now(), null))
        .expectNext(new Entry("entry_2", "Second Entry", "Content of the second entry.", "user", now(), null))
        .expectNext(new Entry("entry_3", "Third Entry", "Content of the third entry.", "user", now(), null))
        .verifyComplete();
    }

    @Test
    @DisplayName("returns an empty list if no entries are available")
    void listNoEntries() {
      StepVerifier.create(service.listEntries()).verifyComplete();
    }

  }

  @Nested
  @DisplayName("getEntry(id)")
  class GetEntry {

    @Test
    @DisplayName("returns a single entry")
    void getEntry() {
      given(repository, new EntryDocument("my_entry", "My Journal Entry", "Content of the entry."));
      StepVerifier.create(service.getEntry("my_entry"))
        .expectNext(new Entry("my_entry", "My Journal Entry", "Content of the entry.", "user", now(), null))
        .verifyComplete();
    }

    @Test
    @DisplayName("returns nothing if requested entry does not exist")
    void getEntryNotFound() {
      StepVerifier.create(service.getEntry("no_such_entry")).verifyComplete();
    }

  }

  @Nested
  @DisplayName("createEntry(title, content)")
  class CreateEntry {

    @Test
    @DisplayName("creates and returns a new entry")
    void createEntry() {
      StepVerifier.create(service.createEntry("My Journal Entry", "Content of the entry."))
        .expectNext(new Entry("my_entry", "My Journal Entry", "Content of the entry.", "user", now(), null))
        .verifyComplete();
        assertExists(repository, new EntryDocument("my_entry", "My Journal Entry", "Content of the entry.", "user", now(), null, null));
    }

    @Test
    @DisplayName("throws if title is null")
    @SuppressWarnings("ConstantConditions")
    void createInvalidEntryWithMissingTitle() {
      StepVerifier.create(service.createEntry(null, "Content of the entry."))
        .expectError(NullPointerException.class)
        .verify();
    }

    @Test
    @DisplayName("throws if content is null")
    @SuppressWarnings("ConstantConditions")
    void createInvalidEntryWithMissingContent() {
      StepVerifier.create(service.createEntry("My Journal Entry", null))
        .expectError(NullPointerException.class)
        .verify();
    }

  }

  @Nested
  @DisplayName("editEntry(id, newTitle, newContent)")
  class EditEntry {

    @Test
    @DisplayName("edits title and content of an existing entry")
    void editTitle() {
      given(repository, new EntryDocument("my_entry", "My Journal Entry", "Content of the entry."));
      StepVerifier.create(service.editEntry("my_entry", "My Renamed Entry", "New content of the entry."))
        .expectNext(new Entry("my_entry", "My Renamed Entry", "New content of the entry.", "user", now(), now()))
        .verifyComplete();
      assertExists(repository, new EntryDocument("my_entry", "My Renamed Entry", "New content of the entry.", "user", now(), "user", now()));
    }

    @Test
    @DisplayName("leaves title unchanged if new title is null")
    void editEntryNoNewTitle() {
      given(repository, new EntryDocument("my_entry", "My Journal Entry", "Content of the entry."));
      StepVerifier.create(service.editEntry("my_entry", null, "New content of the entry."))
        .expectNext(new Entry("my_entry", "My Journal Entry", "New content of the entry.", "user", now(), now()))
        .verifyComplete();
      assertExists(repository, "my_entry", document -> assertThat(document.getTitle()).isEqualTo("My Journal Entry"));
    }

    @Test
    @DisplayName("leaves content unchanged if new content is null")
    void editEntryNoNewContent() {
      given(repository, new EntryDocument("my_entry", "My Journal Entry", "Content of the entry."));
      StepVerifier.create(service.editEntry("my_entry", "My Renamed Entry", null))
        .expectNext(new Entry("my_entry", "My Renamed Entry", "Content of the entry.", "user", now(), now()))
        .verifyComplete();
      assertExists(repository, "my_entry", document -> assertThat(document.getContent()).isEqualTo("Content of the entry."));
    }

    @Test
    @DisplayName("does nothing if entry does not exist")
    void editEntryNotFound() {
      StepVerifier.create(service.editEntry("my_entry", "My Renamed Entry", "New content of the entry.")).verifyComplete();
    }

  }

  private Instant now() {
    return Instant.now(clock).truncatedTo(MILLIS);
  }

}
