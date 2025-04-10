package com.example.blog.infrastructure.jpa.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Object id) {
        super(String.format("Resource with id: %s is not found", id));
    }
}
