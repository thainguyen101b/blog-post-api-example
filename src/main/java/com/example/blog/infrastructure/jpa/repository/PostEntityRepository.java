package com.example.blog.infrastructure.jpa.repository;

import com.example.blog.infrastructure.jpa.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, UUID> {

    Page<PostEntity> findByContentContaining(String content, Pageable pageable);

    boolean existsByCategories_Id(UUID categoryId);
}
