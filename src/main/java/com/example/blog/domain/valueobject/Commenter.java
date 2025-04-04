package com.example.blog.domain.valueobject;

import org.springframework.util.Assert;

public record Commenter(String id) {

    public Commenter {
        Assert.notNull(id, "commenter id must not be null");
    }
}
