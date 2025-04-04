package com.example.blog.domain.exception;

public class CategoryAlreadyExistsException extends RuntimeException {

    public CategoryAlreadyExistsException(String name) {
        super("Category with name: " + name + " has already been existed and cannot be duplicated.");
    }

}
