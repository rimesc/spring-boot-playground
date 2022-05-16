package io.github.rimesc.springbootplayground.util;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.Clock;
import java.time.Instant;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides access to the current system time.
 */
@Component
public class CurrentTime implements Supplier<Instant> {

  private final Clock clock;

  @Autowired
  public CurrentTime(final Clock clock) {
    this.clock = clock;
  }

  /**
   * Gets the current time, to the nearest millisecond (which is the maximum precision preserved when round-tripping via
   * the API).
   *
   * @return the current time
   */
  public Instant get() {
    return Instant.now(clock).truncatedTo(MILLIS);
  }

}
