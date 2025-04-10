package com.example.blog.infrastructure.jpa.repository;

import com.example.blog.infrastructure.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryEntityRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findByIdIn(Collection<UUID> ids);

    boolean existsByName(String name);
}
