package com.example.blog.domain;

import com.example.blog.domain.exception.CategoryAlreadyExistsException;
import com.example.blog.domain.exception.PostAlreadyDeletedException;
import com.example.blog.domain.valueobject.Author;
import com.example.blog.domain.valueobject.CommentId;
import com.example.blog.domain.valueobject.PostId;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Post {
    private final PostId id;
    private String title;
    private String content;
    private final Author author;
    private final List<Category> categories = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Post(String title, String content, Author author, Category ... cats) {
        Assert.notNull(title, "title must not be null");
        Assert.notNull(content, "content must not be null");
        Assert.notNull(author, "author must not be null");

        this.id = new PostId();
        this.title = title;
        this.content = content;
        this.author = author;
        for (Category cat : cats) {
            addCategory(cat);
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deletedAt = null;
    }

    private void addCategory(Category cat) {
        boolean isDuplicate = categories.stream()
                .anyMatch(c -> c.getName().equals(cat.getName()));
        if (isDuplicate) {
            throw new CategoryAlreadyExistsException(cat.getName());
        }
        categories.add(cat);
    }

    public Post updatePost(String title, String content) {
        if (isDeleted()) {
            throw new PostAlreadyDeletedException(id);
        }

        Assert.notNull(title, "title must not be null");
        Assert.notNull(content, "content must not be null");

        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public void softDelete() {
        deletedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void addComment(Comment comment) {
        if (isDeleted()) {
            throw new PostAlreadyDeletedException(id);
        }

        Assert.notNull(comment, "comment must not be null");
        comments.add(comment);
    }

    public void removeComment(CommentId commentId) {
        if (isDeleted()) {
            throw new PostAlreadyDeletedException(id);
        }
        Assert.notNull(commentId, "commentId must not be null");
        Comment commentToRemove = getComment(commentId);
        if (commentToRemove != null) {
            comments.remove(commentToRemove);
        }
    }

    public void approveComment(CommentId commentId) {
        if (isDeleted()) {
            throw new PostAlreadyDeletedException(id);
        }
        Comment comment = getComment(commentId);
        if (comment != null)
            comment.approve();
    }

    public void cancelApprovalComment(CommentId commentId) {
        if (isDeleted()) {
            throw new PostAlreadyDeletedException(id);
        }

        Comment comment = getComment(commentId);
        if (comment != null)
            comment.cancelApproval();
    }

    public Comment getComment(CommentId commentId) {
        Assert.notNull(commentId, "commentId must not be null");
        return this.comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElse(null);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    public List<Category> getCategories() {
        return Collections.unmodifiableList(categories);
    }

}
