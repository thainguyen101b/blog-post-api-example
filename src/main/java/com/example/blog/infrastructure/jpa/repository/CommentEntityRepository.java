package com.example.blog.infrastructure.jpa.repository;

import com.example.blog.infrastructure.jpa.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentEntityRepository extends JpaRepository<CommentEntity, UUID> {
}
