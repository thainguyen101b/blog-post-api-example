package com.example.blog.application;

import com.example.blog.application.query.PostDTO;
import com.example.blog.application.query.mapper.PostMapper;
import com.example.blog.utils.Page;
import com.example.blog.domain.Post;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.valueobject.PostId;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class PostQueryService {

    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public PostDTO findById(UUID uuid) {
        PostId postId = PostId.fromUUID(uuid);
        Post post = postRepository.findById(postId);

        return postMapper.toDto(post);
    }

    public Page<PostDTO> findAll(int page, int size) {
        return postRepository.findAll(page, size).map(postMapper::toDto);
    }

}
