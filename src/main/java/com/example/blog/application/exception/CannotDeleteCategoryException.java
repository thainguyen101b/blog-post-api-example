package com.example.blog.application.exception;

import com.example.blog.domain.valueobject.CategoryId;

public class CannotDeleteCategoryException extends RuntimeException {

    public CannotDeleteCategoryException(CategoryId categoryId) {
        super(String.format("Cannot delete category %s, because it is in use", categoryId));
    }

}
