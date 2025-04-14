package com.example.blog.helper;

import java.util.List;
import java.util.UUID;

import com.example.blog.web.dto.PostRequestDto;

public class Posts {
	
	public static PostRequestDto postReqValid() {
		return new PostRequestDto("foo 1", "bar 1", UUID.randomUUID().toString(), List.of());
	}

	public static PostRequestDto postReqBlankTitleAndContent() {
		return new PostRequestDto("", "", UUID.randomUUID().toString(), List.of());
	}

	
}
