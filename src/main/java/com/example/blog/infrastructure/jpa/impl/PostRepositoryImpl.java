package com.example.blog.infrastructure.jpa.impl;

import com.example.blog.domain.Post;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.valueobject.CategoryId;
import com.example.blog.domain.valueobject.PostId;
import com.example.blog.infrastructure.jpa.entity.PostEntity;
import com.example.blog.infrastructure.jpa.exception.ResourceNotFoundException;
import com.example.blog.infrastructure.jpa.repository.PostEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {

    private final PostEntityRepository repository;

    @Override
    public Post findById(PostId id) {
        PostEntity post = repository.findByIdAndDeletedAtIsNull(id.id())
                .orElseThrow(() -> new ResourceNotFoundException(id.id()));
        return post.toDomain();
    }

    @Override
    public void save(Post post) {
        PostEntity postEntity = PostEntity.fromDomain(post);
        repository.save(postEntity);
    }

    @Override
    public com.example.blog.utils.Page<Post> search(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPageJpa =
                repository.findByContentContainingAndDeletedAtIsNull(keyword, pageable);
        com.example.blog.utils.Page<PostEntity> postEntityPage = com.example.blog.utils.Page.of(
                postPageJpa.getContent(),
                page,
                size,
                postPageJpa.getTotalElements()
        );

        return postEntityPage.map(PostEntity::toDomain);
    }

    @Override
    public com.example.blog.utils.Page<Post> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPageJpa =
                repository.findByDeletedAtIsNull(pageable);

        com.example.blog.utils.Page<PostEntity> postEntityPage = com.example.blog.utils.Page.of(
                postPageJpa.getContent(),
                page,
                size,
                postPageJpa.getTotalElements()
        );
        return postEntityPage.map(PostEntity::toDomain);
    }

    @Override
    public boolean existsByCategory(CategoryId categoryId) {
        return repository.existsByCategories_IdAndDeletedAtIsNull(categoryId.id());
    }

	@Override
	public com.example.blog.utils.Page<Post> searchDeletedPosts(String keyword, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPageJpa =
                repository.findByContentContainingAndDeletedAtIsNotNull(keyword, pageable);
        com.example.blog.utils.Page<PostEntity> postEntityPage = com.example.blog.utils.Page.of(
                postPageJpa.getContent(),
                page,
                size,
                postPageJpa.getTotalElements()
        );

        return postEntityPage.map(PostEntity::toDomain);
	}

	@Override
	public com.example.blog.utils.Page<Post> findAllDeletedPosts(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
        Page<PostEntity> postPageJpa =
                repository.findByDeletedAtIsNotNull(pageable);

        com.example.blog.utils.Page<PostEntity> postEntityPage = com.example.blog.utils.Page.of(
                postPageJpa.getContent(),
                page,
                size,
                postPageJpa.getTotalElements()
        );
        return postEntityPage.map(PostEntity::toDomain);
	}

}
