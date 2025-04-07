package com.example.blog.domain.exception;

import com.example.blog.domain.valueobject.PostId;

public class PostAlreadyPublishedException extends RuntimeException {

    public PostAlreadyPublishedException(PostId postId) {
        super("Post with ID " + postId + " has already been published and cannot be updated or deleted..");
    }

}
