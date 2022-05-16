package io.github.rimesc.springbootplayground.journal.web;

import static io.github.rimesc.springbootplayground.journal.test.MongoTestUtils.given;
import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.rimesc.springbootplayground.journal.model.Entry;
import io.github.rimesc.springbootplayground.journal.persistence.EntryDocument;
import io.github.rimesc.springbootplayground.journal.persistence.EntryRepository;
import io.github.rimesc.springbootplayground.util.IdFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Tests for {@link EntryController}.
 */
@ControllerTest
@DisplayName("Entries API")
public class EntryControllerTest {

  private static final String BASE_PATH = "/api/journal/entries";

  private static final Instant NOW = Instant.now();

  @TestConfiguration
  static public class Config {

    @Bean
    Clock clock() {
      return Clock.fixed(NOW, ZoneId.systemDefault());
    }

    @Bean
    ReactiveAuditorAware<String> auditorAware() {
      return () -> Mono.just("user");
    }

  }

  @Autowired
  private EntryRepository repository;

  @Autowired
  private IdFactory idFactory;

  @Autowired
  private WebTestClient client;

  @AfterEach
  void tearDown() {
    repository.deleteAll().block();
  }

  @Nested
  @WithMockUser
  @DisplayName("when logged in")
  class LoggedInTest {

    @Nested
    @DisplayName("GET /api/journal/entries/")
    class ListEntriesTest {

      @Test
      @DisplayName("returns a list of entries ordered by title")
      void listEntries() {
        final List<String> ids = Stream.generate(idFactory::create).limit(5).toList();
        final Instant entry5Created = Instant.ofEpochSecond(1661500183L).truncatedTo(MILLIS);
        given(repository,
          new EntryDocument(ids.get(0), "First Entry", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."),
          new EntryDocument(ids.get(1), "Second Entry", "Nulla nec finibus nisi."),
          new EntryDocument(ids.get(2), "Third Entry", "Fusce imperdiet, felis et hendrerit lacinia, enim ipsum venenatis quam."),
          new EntryDocument(ids.get(3), "Fourth Entry", "Sed eget lorem est."),
          // Setting 'createdAt' will cause Spring to treat it like an update and set 'lastModifiedBy' and 'lastModifiedAt'.
          new EntryDocument(ids.get(4), "Fifth Entry", "Curabitur consequat nec justo sed varius.", "user", entry5Created, null, null));
        get()
          .expectStatus().isOk()
          .expectBodyList(Entry.class).contains(
            new Entry(ids.get(4), "Fifth Entry", "Curabitur consequat nec justo sed varius.", "user", entry5Created, NOW.truncatedTo(MILLIS)),
            new Entry(ids.get(0), "First Entry", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "user", NOW.truncatedTo(MILLIS), null),
            new Entry(ids.get(3), "Fourth Entry", "Sed eget lorem est.", "user", NOW.truncatedTo(MILLIS), null),
            new Entry(ids.get(1), "Second Entry", "Nulla nec finibus nisi.", "user", NOW.truncatedTo(MILLIS), null),
            new Entry(ids.get(2), "Third Entry", "Fusce imperdiet, felis et hendrerit lacinia, enim ipsum venenatis quam.", "user", NOW.truncatedTo(MILLIS), null)
          );
      }

      @Test
      @DisplayName("returns an empty list if no entries are available")
      void listNoEntries() {
        get()
          .expectStatus().isOk()
          .expectBodyList(Entry.class).hasSize(0);
      }

    }

    @Nested
    @DisplayName("GET /api/journal/entries/{id}")
    class GetEntryTest {

      private final String id = idFactory.create();

      @Test
      @DisplayName("returns a single entry")
      void getEntry() {
        given(repository, new EntryDocument(id, "My entry", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
        get(id)
          .expectStatus().isOk()
          .expectBody(Entry.class).isEqualTo(new Entry(id, "My entry", "Lorem ipsum dolor sit amet, consectetur adipiscing elit.", "user", NOW.truncatedTo(MILLIS), null));
      }

      @Test
      @DisplayName("fails with 404 status if entry does not exist")
      void getEntryNotFound() {
        get(id)
          .expectStatus().isNotFound();
      }

    }

    @Nested
    @DisplayName("POST /api/journal/entries/")
    class CreateEntryTest {

      @Test
      @DisplayName("creates a new entry")
      void createEntry() {
        post(new Entry(null, "My new entry", "", null, null, null))
          .expectStatus().isCreated()
          .expectBody(Entry.class)
          .value(result -> {
            assertThat(result.getId()).isNotBlank();
            assertThat(result.getTitle()).isEqualTo("My new entry");
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getAuthor()).isEqualTo("user");
            assertThat(result.getCreated()).isEqualTo(NOW.truncatedTo(MILLIS));
            assertThat(result.getEdited()).isNull();
          });
        StepVerifier.create(repository.findAll()).expectNextMatches(entry -> entry.getTitle().equals("My new entry")).verifyComplete();
      }

      @Test
      @DisplayName("fails with 400 status if entry ID is present")
      void createInvalidEntryWithId() {
        final String id = idFactory.create();
        post(new Entry(id, "My new entry", "", null, null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'id': must be null");
      }

      @Test
      @DisplayName("fails with 400 status if entry title is absent")
      void createInvalidEntryWithMissingTitle() {
        post(new Entry(null, null, "", null, null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'title': must not be null");
      }

      @Test
      @DisplayName("fails with 400 status if entry title is empty")
      void createInvalidEntryWithEmptyTitle() {
        post(new Entry(null, "", "", null, null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'title': length must be between 1 and 256");
      }

      @Test
      @DisplayName("fails with 400 status if entry title is too long")
      void createInvalidEntryWithOverLongTitle() {
        post(new Entry(null, StringUtils.repeat("A", 257), null, null, null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'title': length must be between 1 and 256");
      }

      @Test
      @DisplayName("fails with 400 status if entry author is present")
      void createInvalidEntryWithAuthor() {
        post(new Entry(null, "My new entry", "", "user", null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'author': must be null");
      }

      @Test
      @DisplayName("fails with 400 status if entry created at is present")
      void createInvalidEntryWithCreatedAt() {
        post(new Entry(null, "My new entry", "", null, Instant.now(), null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'created': must be null");
      }

      @Test
      @DisplayName("fails with 400 status if entry edited at is present")
      void createInvalidEntryWithEditedAt() {
        post(new Entry(null, "My new entry", "", null, null, Instant.now()))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'edited': must be null");
      }

    }

    @Nested
    @DisplayName("PUT /api/journal/entries/{id}")
    class UpdateEntryTest {

      private final String id = idFactory.create();

      @BeforeEach
      void setUpEntry() {
        given(repository, new EntryDocument(id, "My entry", "Lorem ipsum dolor sit amet, consectetur adipiscing elit."));
      }

      @Test
      @DisplayName("edits title of an existing entry")
      void editEntryTitle() {
        put(id, new Entry(null, "My renamed entry", null, null, null, null))
          .expectStatus().isOk()
          .expectBody(Entry.class)
          .value(result -> {
            assertThat(result.getTitle()).isEqualTo("My renamed entry");
            assertThat(result.getContent()).isEqualTo("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
            assertThat(result.getAuthor()).isEqualTo("user");
            assertThat(result.getCreated()).isEqualTo(NOW.truncatedTo(MILLIS));
            assertThat(result.getEdited()).isEqualTo(NOW.truncatedTo(MILLIS));
          });
        StepVerifier.create(repository.findById(id)).expectNextMatches(entry -> entry.getTitle().equals("My renamed entry")).verifyComplete();
      }

      @Test
      @DisplayName("edits content of an existing entry")
      void editEntryTags() {
        put(id, new Entry(null, null, "Nulla nec finibus nisi.", null, null, null))
          .expectStatus().isOk()
          .expectBody(Entry.class)
          .value(result -> {
            assertThat(result.getAuthor()).isEqualTo("user");
            assertThat(result.getCreated()).isEqualTo(NOW.truncatedTo(MILLIS));
            assertThat(result.getEdited()).isEqualTo(NOW.truncatedTo(MILLIS));
          });
        StepVerifier.create(repository.findById(id)).expectNextMatches(entry -> entry.getContent().equals("Nulla nec finibus nisi.")).verifyComplete();
      }

      @Test
      @DisplayName("fails with 404 status if entry does not exist")
      void editEntryNotFound() {
        put("unknown_entry", new Entry(null, null, null, null, null, null))
          .expectStatus().isNotFound();
      }

      @Test
      @DisplayName("fails with 400 status if entry ID is present")
      void editInvalidEntryWithId() {
        put(id, new Entry(id, null, null, null, null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'id': must be null");
      }

      @Test
      @DisplayName("fails with 400 status if entry title is empty")
      void editInvalidEntryWithEmptyTitle() {
        put(id, new Entry(null, "", null, null, null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'title': length must be between 1 and 256");
      }

      @Test
      @DisplayName("fails with 400 status if entry title is too long")
      void editInvalidEntryWithOverLongTitle() {
        put(id, new Entry(null, StringUtils.repeat("A", 257), null, null, null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'title': length must be between 1 and 256");
      }

      @Test
      @DisplayName("fails with 400 status if entry author is present")
      void editInvalidEntryWithAuthor() {
        put(id, new Entry(null, null, null, "user", null, null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'author': must be null");
      }

      @Test
      @DisplayName("fails with 400 status if entry created at is present")
      void editInvalidEntryWithCreatedAt() {
        put(id, new Entry(null, null, null, null, Instant.now(), null))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'created': must be null");
      }

      @Test
      @DisplayName("fails with 400 status if entry edited at is present")
      void editInvalidEntryWithEditedAt() {
        put(id, new Entry(null, null, null, null, null, Instant.now()))
          .expectStatus().isBadRequest()
          .expectBody().jsonPath("message", "Invalid value for field 'edited': must be null");
      }

    }

  }

  @Nested
  @DisplayName("when logged out")
  class LoggedOutTest {

    @Test
    @DisplayName("GET /api/journal/entries/ fails with 401 status")
    void listUnauthorized() {
      get()
        .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("GET /api/journal/entries/{id} fails with 401 status")
    void getUnauthorized() {
      final String id = idFactory.create();
      get(id)
        .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST /api/journal/entries/ fails with 401 status")
    void createUnauthorized() {
      post(new Entry(null, "My entry", null, null, null, null))
        .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("PUT /api/journal/entries/{id} fails with 401 status")
    void editUnauthorized() {
      final String id = idFactory.create();
      put(id, new Entry(null, null, null, null, null, null))
        .expectStatus().isUnauthorized();
    }

  }

  private WebTestClient.ResponseSpec get() {
    return client.get().uri(BASE_PATH + "/").exchange();
  }

  private WebTestClient.ResponseSpec get(final String id) {
    return client.get().uri(BASE_PATH + "/" + id).exchange();
  }

  private WebTestClient.ResponseSpec post(final Entry entry) {
    return client.mutateWith(csrf()).post().uri(BASE_PATH + "/").bodyValue(entry).exchange();
  }

  private WebTestClient.ResponseSpec put(final String id, final Entry edits) {
    return client.mutateWith(csrf()).put().uri(BASE_PATH + "/" + id).bodyValue(edits).exchange();
  }

}
