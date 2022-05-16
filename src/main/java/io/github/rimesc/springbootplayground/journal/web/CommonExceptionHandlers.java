package io.github.rimesc.springbootplayground.journal.web;

import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
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
  @ExceptionHandler(WebExchangeBindException.class)
  void constraintViolation(final WebExchangeBindException ex) {
    final List<FieldError> violations = ex.getBindingResult().getFieldErrors();
    final String message = violations.stream().map(this::getMessage).collect(joining(";"));
    throw new ResponseStatusException(BAD_REQUEST, message, ex);
  }

  private String getMessage(final FieldError fieldError) {
    return String.format("Invalid value for field '%s': %s", fieldError.getField(), fieldError.getDefaultMessage());
  }

}
