package com.example.blog.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record PostRequestDto(
		@JsonProperty("title")
		@NotBlank(message = "post title must not be blank")
		@Size(min = 1, max = 255, message = "post title length must be between 1 and 255 characters")
		String title,
		
		@JsonProperty("content")
		@NotBlank(message = "post content must not be blank")
		@Size(min = 1, max = 255, message = "post content length must be between 1 and 255 characters")
		String content,
		
		@JsonProperty("author_id")
		String authorId,
		
		@JsonProperty("category_ids")
		List<UUID> categoryIds
		
) {

}
