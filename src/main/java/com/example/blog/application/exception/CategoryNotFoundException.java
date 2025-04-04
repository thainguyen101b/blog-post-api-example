package com.example.blog.application.exception;

import com.example.blog.domain.valueobject.CategoryId;

import java.util.List;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(CategoryId categoryId) {
        super(String.format("Category with ID '%s' not found.", categoryId));
    }

    public CategoryNotFoundException(List<CategoryId> categoryIds) {
        super(String.format("No categories found for the provided IDs: %s.", categoryIds));
    }

}
