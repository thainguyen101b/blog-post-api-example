package com.example.blog.infrastructure.config;

import com.example.blog.application.PostQueryService;
import com.example.blog.application.PostService;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.PostRepository;
import com.example.blog.utils.mapper.PostMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostServiceBean {

    @Bean
    PostService postService(PostRepository postRepository,
                         CategoryRepository categoryRepository) {
        return new PostService(postRepository, categoryRepository);
    }
    
    @Bean
    PostQueryService postQueryService(PostRepository postRepository, PostMapper postMapper) {
    	return new PostQueryService(postRepository, postMapper);
    }

}
