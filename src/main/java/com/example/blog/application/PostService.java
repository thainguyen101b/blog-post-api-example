package com.example.blog.application;

import com.example.blog.application.command.PostCreateCommand;
import com.example.blog.application.command.PostEditCommand;
import com.example.blog.application.exception.CategoryNotFoundException;

import com.example.blog.domain.*;
import com.example.blog.domain.valueobject.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public void createPost(PostCreateCommand command) {
        // Extract categories in PostCreateCommand to a list of CategoryId
        List<CategoryId> categoryIds = toCategoryId(command.categoryIds());

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

    public void editPost(UUID id, PostEditCommand command) {
        // Find
        Post post = postRepository.findById(PostId.fromUUID(id));

        List<CategoryId> categoryIdsToUpdate = toCategoryId(command.categoryIds());

        // prepare old categories to delete
        List<CategoryId> categoryIdsToRemove = post.getCategories().stream()
                .map(Category::getId)
                .filter(cId -> !categoryIdsToUpdate.contains(cId))
                .toList();

        // prepare new categories to add
        List<CategoryId> originalCategoryIds = post.getCategoryIds();
        List<CategoryId> categoryIdsToAdd = command.categoryIds().stream()
                .map(CategoryId::fromUUID)
                .filter(cId -> !originalCategoryIds.contains(cId))
                .toList();

        // remove old categories
        categoryIdsToRemove.forEach(post::removeCategory);
        // find category by ids and validate
        List<Category> categoriesToAdd = categoryRepository.findCategoryByIds(categoryIdsToAdd);
        validateCategories(categoriesToAdd, categoryIdsToAdd);
        // add new categories
        categoriesToAdd.forEach(post::addCategory);

        // update info
        post.updatePost(command.title(), command.content());

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

    private List<CategoryId> toCategoryId(List<UUID> uuids) {
        return uuids.stream().map(CategoryId::fromUUID).toList();
    }

}
