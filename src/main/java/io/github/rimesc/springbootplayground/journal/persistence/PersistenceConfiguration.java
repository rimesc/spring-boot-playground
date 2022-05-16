package io.github.rimesc.springbootplayground.journal.persistence;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import reactor.core.publisher.Mono;

@Configuration
@EnableReactiveMongoRepositories
@EnableReactiveMongoAuditing(dateTimeProviderRef = "dateTimeProvider", modifyOnCreate = false)
class PersistenceConfiguration {

  @Bean
  ReactiveAuditorAware<String> auditorAware() {
    return () -> Mono.justOrEmpty(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .filter(Authentication::isAuthenticated)
      .map(Authentication::getName);
  }

  @Bean
  DateTimeProvider dateTimeProvider(final Clock clock) {
    return () -> Optional.of(Instant.now(clock).truncatedTo(MILLIS));
  }

}
