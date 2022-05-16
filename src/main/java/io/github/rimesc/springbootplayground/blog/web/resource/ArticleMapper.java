package io.github.rimesc.springbootplayground.blog.web.resource;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;

import org.mapstruct.Mapper;

import io.github.rimesc.springbootplayground.blog.model.Article;

/**
 * Maps {@link Article articles} to corresponding API {@link ArticleResource resources}.
 */
@Mapper(componentModel = "spring", injectionStrategy = CONSTRUCTOR)
public interface ArticleMapper {

  ArticleResource articleToResource(Article article);

}
