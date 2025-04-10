package com.example.blog.infrastructure.config;

import com.example.blog.application.PostService;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostServiceBean {

    @Bean
    public PostService postService(PostRepository postRepository,
                                   CategoryRepository categoryRepository) {
        return new PostService(postRepository, categoryRepository);
    }

}
