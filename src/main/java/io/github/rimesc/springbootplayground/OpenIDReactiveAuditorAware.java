package io.github.rimesc.springbootplayground;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;

import reactor.core.publisher.Mono;

/**
 * Implementation of {@link ReactiveAuditorAware} that calls the OpenID {@code /userinfo} endpoint to find the username.
 */
public class OpenIDReactiveAuditorAware implements ReactiveAuditorAware<String> {

  private final WebClient webClient;

  public OpenIDReactiveAuditorAware(final String issuer) {
    this.webClient = WebClient.create(issuer);
  }

  @Override
  public Mono<String> getCurrentAuditor() {
    return ReactiveSecurityContextHolder.getContext()
      .mapNotNull(SecurityContext::getAuthentication)
      .filter(Authentication::isAuthenticated)
      .flatMap(authentication -> {
        final Jwt jwt = (Jwt) authentication.getPrincipal();
        return webClient.get()
          .uri("/userinfo")
          .accept(APPLICATION_JSON)
          .header(AUTHORIZATION, "Bearer " + jwt.getTokenValue())
          .exchangeToMono(response -> parseResponse(response).map(UserInfo::getName));
      });
  }

  private static Mono<UserInfo> parseResponse(final ClientResponse response) {
    return response.bodyToMono(String.class).flatMap(OpenIDReactiveAuditorAware::parseResponse);
  }

  private static Mono<UserInfo> parseResponse(final String userinfo) {
    try {
      return Mono.just(UserInfo.parse(userinfo));
    }
    catch (final ParseException e) {
      return Mono.error(e);
    }
  }
}
