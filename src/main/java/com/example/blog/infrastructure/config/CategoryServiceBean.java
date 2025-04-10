package com.example.blog.infrastructure.config;

import com.example.blog.application.CategoryService;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.PostRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CategoryServiceBean {

    @Bean
    CategoryService categoryService(
            CategoryRepository categoryRepository,
            PostRepository postRepository
    ) {
        return new CategoryService(categoryRepository, postRepository);
    }

}
