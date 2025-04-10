package com.example.blog.infrastructure.jpa.entity;

import com.example.blog.domain.Comment;
import com.example.blog.domain.valueobject.CommentId;
import com.example.blog.domain.valueobject.Commenter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
public class CommentEntity {
    @Id
    private UUID id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "commenter_id", nullable = false)
    private String commenterId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public Comment toDomain() {
        CommentId commentId = CommentId.fromUUID(id);
        Commenter commenter = new Commenter(commenterId);
        return Comment.reconstitute(commentId, content, commenter, createdAt, updatedAt, approvedAt);
    }

    public static CommentEntity fromDomain(Comment comment) {
        CommentEntity entity = new CommentEntity();
        entity.id = comment.getId().id();
        entity.content = comment.getContent();
        entity.commenterId = comment.getCommenter().id();
        entity.createdAt = comment.getCreatedAt();
        entity.updatedAt = comment.getUpdatedAt();
        entity.approvedAt = comment.getApprovedAt();
        return entity;
    }
}
