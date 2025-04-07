package com.example.blog.application.query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PostDTO(
        UUID id,
        String title,
        String content,
        String authorId,
        List<CategoryDTO> categories,
        List<CommentDTO> comments,
        String slug,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isPublished,
        Boolean isDeleted,
        LocalDateTime deletedAt
) {

    public record CategoryDTO(
            UUID id,
            String name
    ){ }

    public record CommentDTO(
            UUID id,
            String content,
            String commenter,
            Boolean isApproved,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime approvedAt
    ){}

}
