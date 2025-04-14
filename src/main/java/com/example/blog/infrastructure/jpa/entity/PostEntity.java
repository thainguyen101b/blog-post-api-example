package com.example.blog.infrastructure.jpa.entity;

import com.example.blog.domain.Category;
import com.example.blog.domain.Comment;
import com.example.blog.domain.Post;
import com.example.blog.domain.valueobject.Author;
import com.example.blog.domain.valueobject.PostId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "post")
@Getter
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class PostEntity {
    @Id
    @ToString.Include
    private UUID id;

    @Column(name = "title", nullable = false)
    @ToString.Include
    private String title;

    @Lob
    @Column(name = "content", columnDefinition = "TEXT")
    @ToString.Include
    private String content;

    @Column(name = "author_id", nullable = false)
    @ToString.Include
    private String authorId;

    @ManyToMany
    @JoinTable(
            name = "post_category",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryEntity> categories = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "post_id")
    private List<CommentEntity> comments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    @ToString.Include
    private LocalDateTime createdAt;

    @Column(name = "slug", unique = true)
    @ToString.Include
    private String slug;

    @Column(name = "published_at")
    @ToString.Include
    private LocalDateTime publishedAt;

    @Column(name = "updated_at")
    @ToString.Include
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    @ToString.Include
    private LocalDateTime deletedAt;

    public static PostEntity fromDomain(Post post) {
        PostEntity entity = new PostEntity();
        entity.id = post.getId().id();
        entity.title = post.getTitle();
        entity.content = post.getContent();
        entity.authorId = post.getAuthor().id();
        entity.categories = post.getCategories().stream().map(CategoryEntity::fromDomain).toList();
        entity.comments = post.getComments().stream().map(CommentEntity::fromDomain).toList();
        entity.createdAt = post.getCreatedAt();
        entity.slug = post.getSlug();
        entity.publishedAt = post.getPublishedAt();
        entity.updatedAt = post.getUpdatedAt();
        entity.deletedAt = post.getDeletedAt();

        return entity;
    }

    public Post toDomain() {
        PostId postId = PostId.fromUUID(id);
        Author author = new Author(authorId);
        List<Category> categories = this.categories.stream()
                .map(CategoryEntity::toDomain).toList();
        List<Comment> comments = this.comments.stream()
                .map(CommentEntity::toDomain).toList();
        return Post.reconstitute(postId, title, slug, content, author, categories, comments,
                createdAt, updatedAt, publishedAt, deletedAt);
    }

}
