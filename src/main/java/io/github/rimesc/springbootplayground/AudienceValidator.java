package io.github.rimesc.springbootplayground;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Token audience validator.
 *
 * @see <a href="https://auth0.com/docs/quickstart/backend/java-spring-security5/interactive">Auth0 Spring Boot API SDK Quickstarts: Spring Boot API</a>
 */
class AudienceValidator implements OAuth2TokenValidator<Jwt> {

  private final String audience;

  AudienceValidator(final String audience) {
    this.audience = audience;
  }

  public OAuth2TokenValidatorResult validate(final Jwt jwt) {
    if (jwt.getAudience().contains(audience)) {
      return OAuth2TokenValidatorResult.success();
    }
    return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "The required audience is missing", null));
  }
}
