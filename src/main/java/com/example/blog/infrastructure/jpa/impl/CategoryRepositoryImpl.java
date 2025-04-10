package com.example.blog.infrastructure.jpa.impl;

import com.example.blog.domain.Category;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.valueobject.CategoryId;
import com.example.blog.infrastructure.jpa.entity.CategoryEntity;
import com.example.blog.infrastructure.jpa.exception.ResourceNotFoundException;
import com.example.blog.infrastructure.jpa.repository.CategoryEntityRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryEntityRepository repository;

    public CategoryRepositoryImpl(CategoryEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Category> findCategoryByIds(List<CategoryId> ids) {
        return repository.findByIdIn(ids.stream().map(CategoryId::id).toList())
                .stream()
                .map(CategoryEntity::toDomain)
                .toList();
    }

    @Override
    public void save(Category category) {
        CategoryEntity entity = CategoryEntity.fromDomain(category);
        repository.save(entity);
    }

    @Override
    public boolean existByName(String name) {
        return repository.existsByName(name);
    }

    @Override
    public Category findById(CategoryId categoryId) {
        return repository.findById(categoryId.id())
                .map(CategoryEntity::toDomain)
                .orElseThrow(() -> new ResourceNotFoundException(categoryId.id()));
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll().stream().map(CategoryEntity::toDomain).toList();
    }

    @Override
    public void delete(Category category) {
        CategoryEntity entity = CategoryEntity.fromDomain(category);
        if (entity.getId() == null)
            return;
        repository.delete(entity);
    }

}
