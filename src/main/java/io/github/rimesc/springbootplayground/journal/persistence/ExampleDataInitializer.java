package io.github.rimesc.springbootplayground.journal.persistence;

import static java.util.Arrays.asList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import io.github.rimesc.springbootplayground.util.IdFactory;

/**
 * Sets up some example data for local development.
 */
@Component
@Profile("dev")
public class ExampleDataInitializer implements ApplicationRunner {

  private final EntryRepository entryRepository;
  private final IdFactory idFactory;

  @Autowired
  public ExampleDataInitializer(final EntryRepository entryRepository, final IdFactory idFactory) {
    this.entryRepository = entryRepository;
    this.idFactory = idFactory;
  }

  @Override
  public void run(final ApplicationArguments args) throws Exception {
    entryRepository.deleteAll().thenMany(entryRepository.saveAll(asList(
      new EntryDocument(idFactory.create(), "My first entry", "", "alice", null, null, null),
      new EntryDocument(idFactory.create(), "My second entry", "", "bob", null, null, null),
      new EntryDocument(idFactory.create(), "My third entry", "", "carol", null, null, null))
    )).subscribe();
  }
}
