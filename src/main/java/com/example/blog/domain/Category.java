package com.example.blog.domain;

import com.example.blog.domain.valueobject.CategoryId;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Objects;

@Getter
public class Category {
    private final CategoryId id;
    private String name;

    public Category(String name) {
        Assert.notNull(name, "category name must not be null");
        this.id = new CategoryId();
        this.name = name;
    }

    public Category updateCategory(String newName) {
        Assert.notNull(newName, "category name must not be null");
        this.name = newName;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
