package com.example.blog.domain;

import com.example.blog.domain.valueobject.CategoryId;
import com.example.blog.utils.Page;
import com.example.blog.domain.valueobject.PostId;

public interface PostRepository {

    Post findById(PostId id);

    void save(Post post);

    Page<Post> search(String keyword, int page, int size);

    Page<Post> findAll(int page, int size);

    boolean existsByCategory(CategoryId categoryId);
    
    Page<Post> searchDeletedPosts(String keyword, int page, int size);
    
    Page<Post> findAllDeletedPosts(int page, int size);
}
