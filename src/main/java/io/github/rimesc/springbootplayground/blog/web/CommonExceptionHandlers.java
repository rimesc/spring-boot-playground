package io.github.rimesc.springbootplayground.blog.web;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import javax.validation.ConstraintViolationException;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

/**
 * Common error handling for all controllers.
 */
@ControllerAdvice
class CommonExceptionHandlers {

  /**
   * Handle violations reported by the validation API by returning a 400 (Bad Request) response..
   *
   * @param ex exception thrown by the validation API
   * @throws ResponseStatusException with a 400 response status
   */
  @ExceptionHandler(ConstraintViolationException.class)
  void constraintViolation(final ConstraintViolationException ex) {
    throw new ResponseStatusException(BAD_REQUEST, ex.getMessage(), ex);
  }

}
