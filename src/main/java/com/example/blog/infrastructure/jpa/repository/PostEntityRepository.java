package com.example.blog.infrastructure.jpa.repository;

import com.example.blog.infrastructure.jpa.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, UUID> {

	Optional<PostEntity> findByIdAndDeletedAtIsNull(UUID id);
	
    Page<PostEntity> findByContentContainingAndDeletedAtIsNull(String content, Pageable pageable);
    Page<PostEntity> findByContentContainingAndDeletedAtIsNotNull(String content, Pageable pageable);
    
    Page<PostEntity> findByDeletedAtIsNull(Pageable pageable);
    Page<PostEntity> findByDeletedAtIsNotNull(Pageable pageable);
    
    boolean existsByCategories_IdAndDeletedAtIsNull(UUID categoryId);
}
