package io.github.rimesc.springbootplayground.blog.web;

import static io.github.rimesc.springbootplayground.blog.model.Article.INITIAL_VERSION;
import static io.github.rimesc.springbootplayground.blog.model.Article.UNMODIFIED;
import static io.github.rimesc.springbootplayground.blog.model.Visibility.PRIVATE;
import static io.github.rimesc.springbootplayground.blog.model.Visibility.PUBLIC;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.time.Instant;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import io.github.rimesc.springbootplayground.blog.model.Article;
import io.github.rimesc.springbootplayground.blog.repository.ArticleRepository;
import io.github.rimesc.springbootplayground.blog.web.resource.ArticleMapper;
import io.github.rimesc.springbootplayground.blog.web.resource.ArticleResource;
import io.github.rimesc.springbootplayground.util.IdSource;
import reactor.test.StepVerifier;


/**
 * Tests for {@link ArticleController}.
 */
@ControllerTest
@DisplayName("Articles API")
public class ArticleControllerTest {

  public static final String BASE_PATH = "/api/articles";

  @Autowired
  private ArticleRepository repository;

  @Autowired
  private WebTestClient client;

  @Autowired
  private IdSource idSource;

  @Autowired
  private ArticleMapper mapper;

  @AfterEach
  void tearDown() {
    repository.deleteAll().block();
  }

  @Nested
  @WithMockUser
  @DisplayName("when logged in")
  class LoggedInTest {

    @Test
    @DisplayName("GET /api/articles/ returns a list of articles")
    void listArticles() {
      final List<Article> articles = IntStream.rangeClosed(1, 7).mapToObj(i -> givenArticle(new Article(idSource.get(), "Article " + i, idSource.get(), now().truncatedTo(SECONDS), UNMODIFIED, emptyList(), PUBLIC, INITIAL_VERSION))).toList();
      final List<ArticleResource> expected = articles.stream().map(mapper::articleToResource).collect(toList());
      get()
        .expectStatus().isOk()
        .expectBodyList(ArticleResource.class).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /api/articles/ returns an empty list if no articles are available")
    void listNoArticles() {
      get()
        .expectStatus().isOk()
        .expectBodyList(ArticleResource.class).hasSize(0);
    }

    @Test
    @DisplayName("GET /api/articles/{id} returns a single article")
    void getArticle() {
      final Article article = givenArticle(new Article(idSource.get(), "My Article", idSource.get(), now().truncatedTo(SECONDS), UNMODIFIED, emptyList(), PUBLIC, INITIAL_VERSION));
      final ArticleResource expected = mapper.articleToResource(article);
      get(article.id())
        .expectStatus().isOk()
        .expectBody(ArticleResource.class).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /api/articles/{id} fails with 404 status if article does not exist")
    void getArticleNotFound() {
      get(idSource.get())
        .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("POST /api/articles/ creates a new article")
    void createArticle() {
      final Instant start = now();
      post(new ArticleResource(null, "My new article", null, null, null, List.of("foo", "bar"), PRIVATE, null))
        .expectStatus().isCreated()
        .expectBody(ArticleResource.class)
        .value(result -> {
          assertThat(result.id()).isNotBlank();
          assertThat(result.title()).isEqualTo("My new article");
          assertThat(result.author()).isEqualTo("user");
          assertThat(result.createdAt()).isBetween(start, now());
          assertThat(result.editedAt()).isNull();
          assertThat(result.tags()).containsExactly("foo", "bar");
          assertThat(result.visibility()).isEqualTo(PRIVATE);
          assertThat(result.version()).isEqualTo(INITIAL_VERSION);
        });
      StepVerifier.create(repository.findAll()).expectNextMatches(article -> article.title().equals("My new article")).verifyComplete();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article ID is present")
    void createInvalidArticleWithId() {
      post(new ArticleResource(idSource.get(), "My new article", null, null, null, emptyList(), PUBLIC, null))
        .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article title is absent")
    void createInvalidArticleWithMissingTitle() {
      post(new ArticleResource(null, null, null, null, null, emptyList(), PUBLIC, null))
        .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article title is empty")
    void createInvalidArticleWithEmptyTitle() {
      post(new ArticleResource(null, "", null, null, null, emptyList(), PUBLIC, null))
        .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article title is too long")
    void createInvalidArticleWithOverLongTitle() {
      post(new ArticleResource(null, Strings.repeat("A", 257), null, null, null, emptyList(), PUBLIC, null))
        .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article author is present")
    void createInvalidArticleWithAuthor() {
      post(new ArticleResource(null, "My new article", idSource.get(), null, null, emptyList(), PUBLIC, null))
        .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article created at is present")
    void createInvalidArticleWithCreatedAt() {
      post(new ArticleResource(null, "My new article", null, Instant.now(), null, emptyList(), PUBLIC, null))
        .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article edited at is present")
    void createInvalidArticleWithEditedAt() {
      post(new ArticleResource(null, "My new article", null, null, Instant.now(), emptyList(), PUBLIC, null))
        .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 400 status if article version is present")
    void createInvalidArticleWithVersion() {
      post(new ArticleResource(null, "My new article", null, null, null, emptyList(), PUBLIC, 3))
        .expectStatus().isBadRequest();
    }

  }

  @Nested
  @DisplayName("when logged out")
  class LoggedOutTest {

    @Test
    @DisplayName("GET /api/articles/ fails with 401 status")
    void listUnauthorized() {
      get()
        .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("GET /api/articles/{id} fails with 401 status")
    void getUnauthorized() {
      get(idSource.get())
        .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("POST /api/articles/ fails with 401 status")
    void createUnauthorized() {
      post(new ArticleResource(null, "My new article", null, null, null, emptyList(), PUBLIC, null))
        .expectStatus().isUnauthorized();
    }

  }

  private Article givenArticle(final Article article) {
    return repository.save(article).block();
  }

  private WebTestClient.ResponseSpec get() {
    return client.get().uri(BASE_PATH + "/").exchange();
  }

  private WebTestClient.ResponseSpec get(final String id) {
    return client.get().uri(BASE_PATH + "/" + id).exchange();
  }

  private WebTestClient.ResponseSpec post(final ArticleResource article) {
    return client.mutateWith(csrf()).post().uri(BASE_PATH + "/").bodyValue(article).exchange();
  }

}
