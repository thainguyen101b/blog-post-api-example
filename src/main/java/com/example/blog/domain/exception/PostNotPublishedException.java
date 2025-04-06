package com.example.blog.domain.exception;

import com.example.blog.domain.valueobject.PostId;

public class PostNotPublishedException extends RuntimeException {

    public PostNotPublishedException(PostId postId) {
        super("Post " + postId + " is not published");
    }

}
