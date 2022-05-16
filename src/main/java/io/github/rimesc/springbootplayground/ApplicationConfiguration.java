package io.github.rimesc.springbootplayground;

import java.time.Clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.github.rimesc.springbootplayground.util.IdFactory;

@Configuration
class ApplicationConfiguration {

  @Bean
  Clock clock() {
    return Clock.systemDefaultZone();
  }

  @Bean
  IdFactory idFactory(@Value("${nanoid.size:12}") final int idSize) {
    return IdFactory.nanoIdFactory(idSize);

  }

}
