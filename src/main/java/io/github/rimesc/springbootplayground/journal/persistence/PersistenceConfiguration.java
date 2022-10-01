package io.github.rimesc.springbootplayground.journal.persistence;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import io.github.rimesc.springbootplayground.OpenIDReactiveAuditorAware;

@Configuration
@EnableReactiveMongoRepositories
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider", modifyOnCreate = false)
class PersistenceConfiguration {

  @Bean
  ReactiveAuditorAware<String> auditorAware(@Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}") final String issuer) {
    return new OpenIDReactiveAuditorAware(issuer);
  }

  @Bean
  DateTimeProvider dateTimeProvider(final Clock clock) {
    return () -> Optional.of(Instant.now(clock).truncatedTo(MILLIS));
  }

}
