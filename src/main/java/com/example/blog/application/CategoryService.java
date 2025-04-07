package com.example.blog.application;

import com.example.blog.application.exception.CannotDeleteCategoryException;
import com.example.blog.domain.Category;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.exception.CategoryAlreadyExistsException;
import com.example.blog.domain.valueobject.CategoryId;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    public UUID createCategory(String name) {
        // Check exist
        boolean isExist = categoryRepository.existByName(name);
        if (isExist) {
            throw new CategoryAlreadyExistsException(name);
        }

        // Create new category
        Category category = new Category(name);
        categoryRepository.save(category);
        return category.getId().id();
    }

    public void editCategory(UUID id, String newName) {
        // Find
        CategoryId categoryId = new CategoryId(id);
        Category category = categoryRepository.findById(categoryId);

        // Check exist
        boolean isExist = categoryRepository.existByName(newName);
        if (isExist) {
            throw new CategoryAlreadyExistsException(newName);
        }

        // Update
        category.updateCategory(newName);
        categoryRepository.save(category);
    }

    public void deleteCategory(UUID id) {
        CategoryId categoryId = CategoryId.fromUUID(id);
        Category category = categoryRepository.findById(categoryId);
        if (postRepository.existsByCategory(categoryId)) {
            throw new CannotDeleteCategoryException(categoryId);
        }

        categoryRepository.delete(category);
    }


}
