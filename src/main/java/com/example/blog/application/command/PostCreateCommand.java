package com.example.blog.application.command;

import java.util.List;
import java.util.UUID;

public record PostCreateCommand(
        String title,
        String content,
        String authorId,
        List<UUID> categoryIds
) {

}
