package io.github.rimesc.springbootplayground;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories
@Profile("remoteMongo")
class MongoConfiguration extends AbstractReactiveMongoConfiguration {

  @Override
  protected String getDatabaseName() {
    return "spring_boot_playground";
  }

}
