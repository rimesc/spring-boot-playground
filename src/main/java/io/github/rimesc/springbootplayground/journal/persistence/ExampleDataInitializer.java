package io.github.rimesc.springbootplayground.journal.persistence;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Sets up some example data for local development.
 */
@Component
@Profile("dev")
public class ExampleDataInitializer implements ApplicationRunner {

  private final EntryRepository entryRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  public ExampleDataInitializer(final EntryRepository entryRepository, final ObjectMapper objectMapper) {
    this.entryRepository = entryRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void run(final ApplicationArguments args) throws Exception {
    entryRepository.deleteAll().thenMany(entryRepository.saveAll(loadEntries())).subscribe();
  }

  private List<EntryDocument> loadEntries() throws IOException {
    return objectMapper.readValue(getClass().getResource("examples/entries.json"), new TypeReference<>() {
    });
  }

}
