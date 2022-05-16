package io.github.rimesc.springbootplayground.journal.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@Profile("remoteMongo")
class RemotePersistenceConfiguration extends AbstractReactiveMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "spring_boot_playground";
  }

}
