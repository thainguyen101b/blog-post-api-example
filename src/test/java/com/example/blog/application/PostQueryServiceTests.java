package com.example.blog.application;

import com.example.blog.application.query.PostDTO;
import com.example.blog.application.query.mapper.PostMapper;
import com.example.blog.domain.Post;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.valueobject.PostId;
import com.example.blog.utils.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostQueryServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostQueryService postQueryService;

    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private UUID postUuid;
    private PostId postId;
    private Post existingPost;
    private PostDTO existingPostDto;

    @BeforeEach
    void setUp() {
        postUuid = UUID.randomUUID();
        postId = PostId.fromUUID(postUuid);

        existingPost = Mockito.mock(Post.class);
        existingPostDto = Mockito.mock(PostDTO.class);
    }

    @Test
    @DisplayName("Should find Post By Id Successfully")
    void findPostById_Success() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(existingPost);
        when(postMapper.toDto(existingPost)).thenReturn(existingPostDto);

        // Act
        PostDTO result = postQueryService.findById(postUuid);

        // Assert
        assertEquals(existingPostDto, result);
        verify(postRepository, times(1)).findById(postId);
        verify(postMapper, times(1)).toDto(postCaptor.capture());
        assertEquals(existingPost, postCaptor.getValue());
    }

    @Test
    @DisplayName("Should find all Posts Successfully")
    void findAllPosts_Success() {
        // Arrange
        int page = 0;
        int size = 10;

        Page<Post> postPage = Page.of(List.of(existingPost), page, size, 1);
        when(postRepository.findAll(page, size)).thenReturn(postPage);
        when(postMapper.toDto(existingPost)).thenReturn(existingPostDto);

        // Act
        Page<PostDTO> result = postQueryService.findAll(page, size);

        // Assert
        verify(postRepository, times(1)).findAll(page, size);
        verify(postMapper, times(1)).toDto(postCaptor.capture());
        assertEquals(existingPost, postCaptor.getValue());
        assertEquals(1, result.content().size());
    }

}
