package io.github.rimesc.springbootplayground;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Security configuration.
 *
 * @see <a href="https://auth0.com/docs/quickstart/backend/java-spring-security5/interactive">Auth0 Spring Boot API SDK Quickstarts: Spring Boot API</a>
 */
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

  private static final String API_PATTERN = "/api/**";
  private static final String AUTH0_PERMISSIONS_CLAIM = "permissions";
  private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

  @Value("${auth0.audience}")
  private String audience;

  @Value("${cors.origin:*}")
  private String allowedOrigin;

  @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
  private String issuer;

  @Bean
  public SecurityWebFilterChain filterChain(final ServerHttpSecurity security) {
    security.authorizeExchange(exchange -> {
        exchange.pathMatchers(API_PATTERN).authenticated();
        exchange.anyExchange().permitAll();
      })
      .cors()
      .and()
      .oauth2ResourceServer().jwt();
    return security.build();
  }

  @Bean
  public static ReactiveJwtAuthenticationConverter authenticationConverter() {
    // Override the default converter, which looks at the 'scope' claim, to instead look at the 'permissions' claim.
    final ReactiveJwtAuthenticationConverter authenticationConverter = new ReactiveJwtAuthenticationConverter();
    authenticationConverter.setJwtGrantedAuthoritiesConverter(new ReactiveJwtGrantedAuthoritiesConverterAdapter(grantedAuthoritiesConverter()));
    return authenticationConverter;
  }

  private static JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter() {
    final JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
    authoritiesConverter.setAuthoritiesClaimName(AUTH0_PERMISSIONS_CLAIM);
    authoritiesConverter.setAuthorityPrefix(DEFAULT_ROLE_PREFIX);
    return authoritiesConverter;
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of(allowedOrigin));
    // Necessary to integrate with Auth0 Angular library.
    configuration.setAllowedHeaders(List.of(AUTHORIZATION));
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration(API_PATTERN, configuration);
    return source;
  }

  @Bean
  JwtDecoder jwtDecoder() {
    final NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);
    jwtDecoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(JwtValidators.createDefaultWithIssuer(issuer), new AudienceValidator(audience)));
    return jwtDecoder;
  }
}
