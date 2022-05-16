package io.github.rimesc.springbootplayground.util;

import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_NUMBER_GENERATOR;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

/**
 * A source of random unique identifiers for model objects.
 */
public interface IdFactory {

  static IdFactory nanoIdFactory(final int idSize) {
    return () -> NanoIdUtils.randomNanoId(DEFAULT_NUMBER_GENERATOR, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray(), idSize);
  }

  static IdFactory fixed(final String id) {
    return () -> id;
  }

  /**
   * Gets a new, random, (hopefully) unique identifier.
   *
   * @return a new identifier
   */
  String create();
}
