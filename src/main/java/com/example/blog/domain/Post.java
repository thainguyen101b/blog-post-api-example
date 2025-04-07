package com.example.blog.domain;

import com.example.blog.domain.exception.*;
import com.example.blog.domain.valueobject.Author;
import com.example.blog.domain.valueobject.CategoryId;
import com.example.blog.domain.valueobject.CommentId;
import com.example.blog.domain.valueobject.PostId;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Post {
    private final PostId id;
    private String title;
    private String content;
    private final Author author;
    private final List<Category> categories = new ArrayList<>();
    private final List<Comment> comments = new ArrayList<>();
    private final LocalDateTime createdAt;
    private String slug;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private Post(PostId id, String title, String slug, String content, Author author,
          List<Category> cats, List<Comment> comments,
          LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt, LocalDateTime deletedAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.content = content;
        this.author = author;
        this.categories.addAll(Objects.requireNonNullElseGet(cats, ArrayList::new));
        this.comments.addAll(Objects.requireNonNullElseGet(comments, ArrayList::new));
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.publishedAt = publishedAt;
        this.deletedAt = deletedAt;
    }

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
        this.slug = generateSlug(this.title);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.publishedAt = null;
        this.deletedAt = null;
    }

    public static Post reconstitute(PostId id, String title, String slug, String content, Author author,
                                    List<Category> cats, List<Comment> comments,
                                    LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime publishedAt, LocalDateTime deletedAt) {
        return new Post(id, title, slug, content, author, cats, comments, createdAt, updatedAt, publishedAt, deletedAt);
    }

    public Post updatePost(String title, String content) {
        if (isDeleted()) {
            throw new PostAlreadyDeletedException(id);
        }

        if (isPublished()) {
            throw new PostAlreadyPublishedException(id);
        }

        Assert.notNull(title, "title must not be null");
        Assert.notNull(content, "content must not be null");

        this.title = title;
        this.content = content;
        this.slug = generateSlug(this.title);
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public void addCategory(Category category) {
        if (isCategoryUpdatable()) {
            boolean isDuplicate = categories.stream()
                    .anyMatch(c -> c.getName().equals(category.getName()));
            if (isDuplicate) {
                throw new CategoryAlreadyExistsException(category.getName());
            }

            Assert.notNull(category, "category must not be null");
            categories.add(category);
        } else {
            throw new CannotChangeCategoryException(id);
        }
    }

    public void removeCategory(CategoryId categoryId) {
        if (isCategoryUpdatable()) {
            Category category = getCategory(categoryId);
            if (category != null)
                categories.remove(category);
        } else {
            throw new CannotChangeCategoryException(id);
        }
    }

    public void publishPost() {
        if (isDeleted()) {
            throw new PostAlreadyDeletedException(id);
        }
        this.publishedAt = LocalDateTime.now();
    }

    public void unPublishPost() {
        this.publishedAt = null;
    }

    public void softDelete() {
        if (isPublished()) {
            throw new PostAlreadyPublishedException(id);
        }

        deletedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    public void addComment(Comment comment) {
        if (!isPublished()) {
            throw new PostNotPublishedException(id);
        }

        Assert.notNull(comment, "comment must not be null");
        comments.add(comment);
    }

    public void removeComment(CommentId commentId) {
        if (!isPublished()) {
            throw new PostNotPublishedException(id);
        }

        Comment commentToRemove = getComment(commentId);
        if (commentToRemove != null) {
            comments.remove(commentToRemove);
        }
    }

    public void approveComment(CommentId commentId) {
        if (!isPublished()) {
            throw new PostNotPublishedException(id);
        }
        Comment comment = getComment(commentId);
        if (comment != null)
            comment.approve();
    }

    public void cancelApprovalComment(CommentId commentId) {
        if (!isPublished()) {
            throw new PostNotPublishedException(id);
        }

        Comment comment = getComment(commentId);
        if (comment != null)
            comment.cancelApproval();
    }

    /*
    * ==== READ ====
    * */

    public Category getCategory(CategoryId categoryId) {
        Assert.notNull(categoryId, "categoryId must not be null");
        return this.categories.stream()
                .filter(c -> c.getId().equals(categoryId))
                .findFirst()
                .orElse(null);
    }

    public List<CategoryId> getCategoryIds() {
        return categories.stream().map(Category::getId).toList();
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

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public boolean isPublished() {
        return publishedAt != null;
    }

    public boolean isCategoryUpdatable() {
        return !isPublished();
    }

    public static String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
}
