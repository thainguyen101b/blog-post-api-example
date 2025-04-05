package com.example.blog.domain.valueobject;

import org.springframework.util.Assert;

import java.util.UUID;

public record PostId(UUID id) {

    public PostId {
        Assert.notNull(id, "post id must not be null");
    }

    public PostId() {
        this(UUID.randomUUID());
    }

    public static PostId fromUUID(UUID uuid) {
        return new PostId(uuid);
    }

}
