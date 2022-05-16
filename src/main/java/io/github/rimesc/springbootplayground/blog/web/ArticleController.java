package io.github.rimesc.springbootplayground.blog.web;

import static io.github.rimesc.springbootplayground.blog.model.Article.INITIAL_VERSION;
import static io.github.rimesc.springbootplayground.blog.model.Article.UNMODIFIED;
import static io.github.rimesc.springbootplayground.blog.model.Visibility.PUBLIC;
import static io.github.rimesc.springbootplayground.blog.web.ControllerUtils.now;
import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.github.rimesc.springbootplayground.blog.model.Article;
import io.github.rimesc.springbootplayground.blog.model.Visibility;
import io.github.rimesc.springbootplayground.blog.repository.ArticleRepository;
import io.github.rimesc.springbootplayground.blog.web.resource.ArticleMapper;
import io.github.rimesc.springbootplayground.blog.web.resource.ArticleResource;
import io.github.rimesc.springbootplayground.blog.web.validation.ValidationGroups;
import io.github.rimesc.springbootplayground.util.IdSource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * Controller for the Articles API endpoints.
 */
@RestController
@RequestMapping("/api/articles")
@Validated
class ArticleController {

  private final ArticleRepository repository;
  private final ArticleMapper mapper;
  private final IdSource idSource;

  @Autowired
  ArticleController(final ArticleRepository repository, final ArticleMapper mapper, final IdSource idSource) {
    this.repository = repository;
    this.mapper = mapper;
    this.idSource = idSource;
  }

  /**
   * Lists all the available articles.
   *
   * @return a page of articles
   */
  @GetMapping("/")
  Flux<ArticleResource> listArticles() {
    return repository.findAll().map(mapper::articleToResource);
  }

  /**
   * Gets a specific article.
   *
   * @param id unique ID of the article to get
   * @return the article, or nothing if there is no article with the specified ID
   */
  @GetMapping("/{id}")
  Mono<ArticleResource> getArticle(@PathVariable final String id) {
    return repository.findById(id).map(mapper::articleToResource).switchIfEmpty(Mono.error(() -> new ResponseStatusException(NOT_FOUND, "No article found with ID: " + id)));
  }

  /**
   * Creates a new article.
   * <p>
   *   The request body may contain the following fields (those marked * are required):
   * </p>
   * <dl>
   *   <dt>title*</dt>
   *   <dd>title of the article</dd>
   *   <dt>tags</dt>
   *   <dd>list of tags to apply to the article</dd>
   *   <dt>visibility</dt>
   *   <dd>visibility level of the article</dd>
   * </dl>
   *
   * @param articleResource resource describing the article to create
   * @return the created article
   */
  @PostMapping("/")
  @Validated(ValidationGroups.Create.class)
  @ResponseStatus(CREATED)
  Mono<ArticleResource> createArticle(@RequestBody @Valid final ArticleResource articleResource, final Principal principal) {
    final String author = principal.getName();
    final List<String> tags = requireNonNullElseGet(articleResource.tags(), ArrayList::new);
    final Visibility visibility = requireNonNullElse(articleResource.visibility(), PUBLIC);
    final Article article = new Article(idSource.get(), articleResource.title(), author, now(), UNMODIFIED, tags, visibility, INITIAL_VERSION);
    return repository.save(article).map(mapper::articleToResource);
  }

}
