package io.github.rimesc.springbootplayground;

import java.time.Clock;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.reactive.ResourceHandlerRegistrationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.reactive.resource.PathResourceResolver;
import org.springframework.web.reactive.resource.ResourceResolverChain;
import org.springframework.web.server.ServerWebExchange;

import io.github.rimesc.springbootplayground.util.IdFactory;
import reactor.core.publisher.Mono;

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

  // Based on https://stackoverflow.com/a/64588051/2945996
  @Bean
  ResourceHandlerRegistrationCustomizer angularResourceHandlerRegistrationCustomizer(@Value("classpath:/static/index.html") final Resource indexHtml) {
    return registration -> registration.resourceChain(true).addResolver(new AngularResourceResolver(indexHtml));
  }

  private static class AngularResourceResolver extends PathResourceResolver {

    // Matches files with extensions.
    private static final String STATIC_FILE_PATTERN = "**/*.*";

    private final Resource indexHtml;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public AngularResourceResolver(final Resource indexHtml) {
      this.indexHtml = indexHtml;
    }

    @Override
    public Mono<Resource> resolveResource(final ServerWebExchange exchange, final String requestPath, final List<? extends Resource> locations, final ResourceResolverChain chain) {
      return pathMatcher.match(STATIC_FILE_PATTERN, requestPath) ? super.resolveResource(exchange, requestPath, locations, chain) : Mono.just(indexHtml);
    }

  }

}
