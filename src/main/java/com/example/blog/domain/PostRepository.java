package com.example.blog.domain;

import com.example.blog.utils.Page;
import com.example.blog.domain.valueobject.PostId;

public interface PostRepository {

    Post findById(PostId id);

    void save(Post post);

    void delete(Post post);

    Page<Post> search(String keyword, int page, int size);

    Page<Post> findAll(int page, int size);
}
