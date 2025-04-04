package com.example.blog.application;

import com.example.blog.application.command.PostCreateCommand;
import com.example.blog.application.exception.CategoryNotFoundException;

import com.example.blog.domain.*;
import com.example.blog.domain.valueobject.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CreatePostUseCase {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public void execute(PostCreateCommand command) {
        // Extract categories in PostCreateCommand to a list of CategoryId
        List<CategoryId> categoryIds = command.categoryIds().stream()
                .map(CategoryId::fromUUID)
                .toList();

        // Find category by ids and validate
        List<Category> existingCategories = categoryRepository.findCategoryByIds(categoryIds);
        validateCategories(existingCategories, categoryIds);

        Post post = new Post(
                command.title(),
                command.content(),
                new Author(command.authorId()),
                existingCategories.toArray(new Category[0])
        );

        postRepository.save(post);
    }

    private void validateCategories(List<Category> existingCategories, List<CategoryId> categoryIds) {
        // transform list of Category to set of CategoryId
        Set<CategoryId> existingSet = existingCategories.stream()
                .map(Category::getId)
                .collect(Collectors.toSet());

        // find category that not contains in existingSet
        List<CategoryId> invalidCategories = categoryIds.stream()
                .filter(id -> !existingSet.contains(id))
                .toList();

        if (!invalidCategories.isEmpty()) {
            throw new CategoryNotFoundException(invalidCategories);
        }
    }

}
