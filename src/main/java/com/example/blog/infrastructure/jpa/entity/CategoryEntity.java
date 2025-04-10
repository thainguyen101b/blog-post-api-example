package com.example.blog.infrastructure.jpa.entity;

import com.example.blog.domain.Category;
import com.example.blog.domain.valueobject.CategoryId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter@Table(name = "category")
@Setter
@NoArgsConstructor
public class CategoryEntity {
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    public Category toDomain () {
        return Category.reconstitute(CategoryId.fromUUID(id), name);
    }

    public static CategoryEntity fromDomain(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.id = category.getId().id();
        entity.name = category.getName();
        return entity;
    }

}
