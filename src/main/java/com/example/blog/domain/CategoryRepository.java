package com.example.blog.domain;

import com.example.blog.domain.valueobject.CategoryId;

import java.util.List;

public interface CategoryRepository {

    List<Category> findCategoryByIds(List<CategoryId> ids);

}
