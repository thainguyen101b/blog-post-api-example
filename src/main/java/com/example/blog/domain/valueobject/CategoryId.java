package com.example.blog.domain.valueobject;

import org.springframework.util.Assert;

import java.util.UUID;

public record CategoryId(UUID id) {

    public CategoryId {
        Assert.notNull(id, "category id must not be null");
    }

    public CategoryId() {
        this(UUID.randomUUID());
    }

    public static CategoryId fromUUID(UUID id) {
        return new CategoryId(id);
    }

}
