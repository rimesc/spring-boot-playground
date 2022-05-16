package io.github.rimesc.springbootplayground.journal.persistence;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Reactive MongoDB repository for journal entries.
 */
@Repository
public interface EntryRepository extends ReactiveMongoRepository<EntryDocument, String> {

}
