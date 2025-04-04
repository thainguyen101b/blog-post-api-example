package com.example.blog.domain;

import com.example.blog.domain.exception.CommentAlreadyApprovedException;
import com.example.blog.domain.valueobject.CommentId;
import com.example.blog.domain.valueobject.Commenter;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
public class Comment {
    private final CommentId id;
    private String content;
    private final Commenter commenter;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;

    public Comment(String content, Commenter commenter) {
        Assert.notNull(content, "comment content must not be null");
        Assert.notNull(commenter, "commenter must not be null");

        id = new CommentId();
        this.content = content;
        this.commenter = commenter;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        approvedAt = null;
    }

    public Comment updateComment(String content) {
        Assert.notNull(content, "comment content must not be null");
        this.content = content;
        this.updatedAt = LocalDateTime.now();
        cancelApproval();

        return this;
    }

    protected void cancelApproval() {
        approvedAt = null;
    }

    protected void approve() {
        if (isApproved()) {
            throw new CommentAlreadyApprovedException(id);
        }
        approvedAt = LocalDateTime.now();
    }

    public boolean isApproved() {
        return approvedAt != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
