package io.github.rimesc.springbootplayground.blog.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import io.github.rimesc.springbootplayground.blog.model.Article;

/**
 * Reactive MongoDB repository for articles.
 */
@Repository
public interface ArticleRepository extends ReactiveMongoRepository<Article, String> {

}
