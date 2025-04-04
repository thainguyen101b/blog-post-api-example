package com.example.blog.domain.exception;

import com.example.blog.domain.valueobject.PostId;

public class PostAlreadyDeletedException extends RuntimeException {

    public PostAlreadyDeletedException(PostId postId) {
        super("Post with ID " + postId + " has already been deleted and cannot be updated.");
    }

}
