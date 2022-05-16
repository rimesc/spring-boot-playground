package io.github.rimesc.springbootplayground.blog.web;

import static java.time.temporal.ChronoUnit.MILLIS;

import java.time.Instant;

/**
 * Static utility methods useful in controller classes.
 */
final class ControllerUtils {

  /**
   * Gets the current time, to the nearest millisecond (which is the maximum precision preserved when round-tripping via
   * the API).
   *
   * @return the current time
   */
  static Instant now() {
    return Instant.now().truncatedTo(MILLIS);
  }

}
