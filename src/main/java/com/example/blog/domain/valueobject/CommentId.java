package com.example.blog.domain.valueobject;

import org.springframework.util.Assert;

import java.util.UUID;

public record CommentId(UUID id) {

    public CommentId {
        Assert.notNull(id, "comment id must not be null");
    }

    public CommentId() {
        this(UUID.randomUUID());
    }

    public static CommentId fromUUID(UUID uuid) {
        return new CommentId(uuid);
    }

}
