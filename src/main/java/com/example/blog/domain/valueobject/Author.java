package com.example.blog.domain.valueobject;

import org.springframework.util.Assert;

public record Author(String id) {

    public Author {
        Assert.notNull(id, "author id must not be null");
    }

}
