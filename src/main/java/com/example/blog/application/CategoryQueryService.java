package com.example.blog.application;

import com.example.blog.application.query.PostDTO;
import com.example.blog.application.query.mapper.CategoryMapper;
import com.example.blog.domain.CategoryRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CategoryQueryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<PostDTO.CategoryDTO> findAll() {
        return categoryRepository.findAll()
                .stream().map(categoryMapper::toDto).toList();
    }

}
