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
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter@Table(name = "category")
@Setter
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class CategoryEntity {
    @Id
    @ToString.Include
    private UUID id;

    @Column(name = "name", nullable = false)
    @ToString.Include
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CategoryEntity category = (CategoryEntity) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
