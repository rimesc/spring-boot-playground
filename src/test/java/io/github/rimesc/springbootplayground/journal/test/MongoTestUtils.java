package io.github.rimesc.springbootplayground.journal.test;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Utility methods for creating and asserting documents in the database.
 */
public class MongoTestUtils {

  /**
   * Persists a document in the database.
   * @param repository repository to use to persist the document
   * @param document document to persist
   * @return The persisted document.
   * @param <T> The document type
   */
  public static <T> T given(final ReactiveMongoRepository<T, String> repository, final T document) {
    return repository.save(document).block();
  }

  /**
   * Persists several documents in the database.
   * @param repository repository to use to persist the documents
   * @param documents documents to persist
   * @return List of the persisted documents.
   * @param <T> The document type
   */
  @SafeVarargs
  public static <T> List<T> given(final ReactiveMongoRepository<T, String> repository, final T... documents) {
    return Flux.concat(Stream.of(documents).map(repository::save).toList()).toStream().toList();
  }

  /**
   * Asserts that a document with the given ID exists in the database.
   * @param repository repository to use to find the document
   * @param id ID of the document to verify
   * @param <T> The document type
   */
  public static <T> void assertExists(final ReactiveMongoRepository<T, String> repository, final String id) {
    StepVerifier.create(repository.findById(id)).expectNextCount(1).verifyComplete();
  }

  /**
   * Asserts that a document with the given ID exists in the database and satisfies the provided assertions.
   * @param repository repository to use to find the document
   * @param id ID of the document to verify
   * @param assertions additional assertions to run on the document, if it exists
   * @param <T> The document type
   */
  public static <T> void assertExists(final ReactiveMongoRepository<T, String> repository, final String id, final Consumer<T> assertions) {
    StepVerifier.create(repository.findById(id)).assertNext(assertions).verifyComplete();
  }

  /**
   * Asserts that a document exists in the database.
   * @param repository repository to use to find the document
   * @param document expected document
   * @param <T> The document type
   */
  public static <T extends Persistable<String>> void assertExists(final ReactiveMongoRepository<T, String> repository, final T document) {
    StepVerifier.create(repository.findById(requireNonNull(document.getId()))).expectNext(document).verifyComplete();
  }

  /**
   * Deletes all the documents of a certain type from the database.
   * @param repository repository to use to find the document
   */
  public static void clear(final ReactiveMongoRepository<?, String> repository) {
    repository.deleteAll().block();
  }

}
