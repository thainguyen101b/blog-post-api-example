package com.example.blog.application.command;

import java.util.List;
import java.util.UUID;

public record PostEditCommand(
        String title,
        String content,
        List<UUID> categoryIds
) {

}
