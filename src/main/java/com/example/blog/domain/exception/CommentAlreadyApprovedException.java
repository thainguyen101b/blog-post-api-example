package com.example.blog.domain.exception;

import com.example.blog.domain.valueobject.CommentId;

public class CommentAlreadyApprovedException extends RuntimeException {

    public CommentAlreadyApprovedException(CommentId commentId) {
        super("Comment with ID " + commentId + " has already been approved.");
    }

}
