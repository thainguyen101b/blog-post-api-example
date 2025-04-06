package com.example.blog.domain.exception;

import com.example.blog.domain.valueobject.PostId;

public class CannotChangeCategoryException extends RuntimeException {

    public CannotChangeCategoryException(PostId postId) {
        super("Post with ID " + postId + " already published and category cannot be changed.");
    }

}
