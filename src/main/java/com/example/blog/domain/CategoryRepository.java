package com.example.blog.domain;

import com.example.blog.domain.valueobject.CategoryId;

import java.util.List;

public interface CategoryRepository {

    List<Category> findCategoryByIds(List<CategoryId> ids);

    void save(Category category);

    boolean existByName(String name);

    Category findById(CategoryId categoryId);

    List<Category> findAll();

    void delete(Category category);
}
