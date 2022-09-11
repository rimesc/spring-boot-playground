package io.github.rimesc.springbootplayground.journal.persistence;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Flux;

/**
 * Reactive MongoDB repository for journal entries.
 */
@Repository
public interface EntryRepository extends ReactiveMongoRepository<EntryDocument, String> {

  @Override
  @Query(fields = "{ 'content' : 0 }")
  Flux<EntryDocument> findAll();

}
