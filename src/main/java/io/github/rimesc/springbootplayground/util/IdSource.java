package io.github.rimesc.springbootplayground.util;

import static com.aventrix.jnanoid.jnanoid.NanoIdUtils.DEFAULT_NUMBER_GENERATOR;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

@Component
public class IdSource implements Supplier<String> {

  public static final char[] ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

  private final int idSize;

  IdSource(@Value("${nanoid.size:12}") final int idSize) {
    this.idSize = idSize;
  }

  @Override
  public String get() {
    return NanoIdUtils.randomNanoId(DEFAULT_NUMBER_GENERATOR, ALPHABET, idSize);
  }
}
