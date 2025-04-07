package com.example.blog.application;

import com.example.blog.application.command.PostCreateCommand;
import com.example.blog.application.command.PostEditCommand;
import com.example.blog.application.exception.CategoryNotFoundException;
import com.example.blog.domain.Category;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.Post;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.exception.PostAlreadyPublishedException;
import com.example.blog.domain.valueobject.Author;
import com.example.blog.domain.valueobject.CategoryId;
import com.example.blog.domain.valueobject.PostId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Post Service Tests")
class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PostService service;

    @Captor
    private ArgumentCaptor<Post> postCaptor;
    @Captor
    private ArgumentCaptor<List<CategoryId>> categoryIdListCaptor;

    private Author author;
    private String authorIdString;

    // common for test update
    private UUID postIdUUID;
    private PostId postId;
    private Post existingPost;
    private Category category3;
    private Category category4;
    private CategoryId categoryId1, categoryId2, categoryId3, categoryId4;
    private UUID categoryUUID2;
    private UUID categoryUUID3;
    private UUID categoryUUID4;

    @BeforeEach
    void setUp() {
        UUID authorUUID = UUID.randomUUID();
        authorIdString = authorUUID.toString();
        author = new Author(authorIdString);

        // common for update test
        postIdUUID = UUID.randomUUID();
        postId = PostId.fromUUID(postIdUUID);
        UUID categoryUUID1 = UUID.randomUUID();
        categoryUUID2 = UUID.randomUUID();
        categoryUUID3 = UUID.randomUUID();
        categoryUUID4 = UUID.randomUUID();

        categoryId1 = CategoryId.fromUUID(categoryUUID1);
        categoryId2 = CategoryId.fromUUID(categoryUUID2);
        categoryId3 = CategoryId.fromUUID(categoryUUID3);
        categoryId4 = CategoryId.fromUUID(categoryUUID4);

        Category category1 = Category.reconstitute(categoryId1, "Tech");
        Category category2 = Category.reconstitute(categoryId2, "Java");
        category3 = Category.reconstitute(categoryId3, "Spring");
        category4 = Category.reconstitute(categoryId4, "New Category");

        LocalDateTime createdAt = LocalDateTime.of(2025, 4, 15, 11, 30, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2025, 4, 15, 11, 45, 0);

        existingPost = Post.reconstitute(postId, "Initial Title", Post.generateSlug("Initial Title"), "Initial Content", author,
                List.of(category1, category2), null,
                createdAt, updatedAt, null, null);
    }

    @Test
    @DisplayName("Should create post successfully when valid categoryIds provided")
    void createPost_validCategoryIds_Success() {
        // Arrange
        UUID category1Uuid = UUID.randomUUID();
        UUID category2Uuid = UUID.randomUUID();
        List<UUID> categoryUuids = List.of(category1Uuid, category2Uuid);

        // Create the command object that will be passed to the use case
        PostCreateCommand command = new PostCreateCommand(
                "Valid Post Title",
                "Some content here",
                authorIdString, // Use the String UUID
                categoryUuids   // Use the List<UUID>
        );

        // Prepare expected data and mock behavior
        CategoryId categoryId1 = CategoryId.fromUUID(category1Uuid);
        CategoryId categoryId2 = CategoryId.fromUUID(category2Uuid);
        List<CategoryId> expectedCategoryIdsToSearch = List.of(categoryId1, categoryId2);

        // Create mock Category objects that the repository should "find"
        Category foundCategory1 = Category.reconstitute(categoryId1, "Tech");
        Category foundCategory2 = Category.reconstitute(categoryId2, "Java");
        List<Category> foundCategories = List.of(foundCategory1, foundCategory2);

        when(categoryRepository.findCategoryByIds(expectedCategoryIdsToSearch)).thenReturn(foundCategories);

        // Act
        service.createPost(command);

        // Assert
        verify(categoryRepository, times(1)).findCategoryByIds(categoryIdListCaptor.capture());
        // Check that the argument passed to findCategoryByIds was the list we expected.
        assertThat(categoryIdListCaptor.getValue())
                .containsExactlyInAnyOrderElementsOf(expectedCategoryIdsToSearch);

        verify(postRepository, times(1)).save(postCaptor.capture());
        // Assert the state of the captured Post object
        Post savedPost = postCaptor.getValue();
        assertThat(savedPost.getTitle()).isEqualTo("Valid Post Title");
        assertThat(savedPost.getContent()).isEqualTo("Some content here");
        assertThat(savedPost.getAuthor()).isEqualTo(author);
        assertThat(savedPost.getCategories())
                .isNotNull()
                .hasSize(2)
                .containsExactlyInAnyOrder(foundCategory1, foundCategory2);
    }

    @Test
    @DisplayName("Should throw CategoryNotFoundException when an invalid Category ID is provided")
    void createPost_invalidCategoryIds_ShouldThrowCategoryNotFoundException() {
        // Arrange
        UUID existingUuid = UUID.randomUUID();
        UUID nonExistingUuid = UUID.randomUUID();
        List<UUID> categoryUuids = List.of(existingUuid, nonExistingUuid);

        PostCreateCommand command = new PostCreateCommand(
                "Invalid Categories Post",
                "Content...",
                authorIdString,
                categoryUuids
        );

        // Prepare expected data and mock behavior
        CategoryId existingId = CategoryId.fromUUID(existingUuid);
        CategoryId nonExistingId = CategoryId.fromUUID(nonExistingUuid);
        List<CategoryId> expectedCategoryIdsToSearch = List.of(existingId, nonExistingId);

        // categoryIds to find has size is 2, but repo return only one, should throw by method PostService.validateCategories
        Category foundCategory = Category.reconstitute(existingId, "Tech");
        List<Category> foundCategories = List.of(foundCategory);

        when(categoryRepository.findCategoryByIds(expectedCategoryIdsToSearch)).thenReturn(foundCategories);

        // Act & Assert
        assertThatThrownBy(() -> service.createPost(command))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(postRepository, never()).save(any(Post.class));
        verify(categoryRepository, times(1)).findCategoryByIds(categoryIdListCaptor.capture());
        // Check that the argument passed to findCategoryByIds was the list we expected.
        assertThat(categoryIdListCaptor.getValue())
                .containsExactlyInAnyOrderElementsOf(expectedCategoryIdsToSearch);
    }

    @Test
    @DisplayName("Should edit post successfully when valid categoryIds provided")
    void editPost_validCategoryIds_Success() {
        // Arrange
        String newTitle = "Updated Title";
        String newContent = "Updated Content";
        PostEditCommand command = new PostEditCommand(
                newTitle,
                newContent,
                List.of(categoryUUID2, categoryUUID3, categoryUUID4)
        );

        when(postRepository.findById(postId)).thenReturn(existingPost);

        // Create mock Category objects that the repository should "find"
        List<CategoryId> expectedIdsToAdd = List.of(categoryId3, categoryId4);
        List<Category> categoriesFoundForAdding = List.of(category3, category4);

        when(categoryRepository.findCategoryByIds(expectedIdsToAdd)).thenReturn(categoriesFoundForAdding);

        // Act
        service.editPost(postIdUUID, command);

        // Assert
        verify(postRepository, times(1)).findById(postId);

        verify(categoryRepository, times(1)).findCategoryByIds(categoryIdListCaptor.capture());
        assertThat(categoryIdListCaptor.getValue())
                .containsExactlyInAnyOrderElementsOf(expectedIdsToAdd);

        verify(postRepository, times(1)).save(postCaptor.capture());
        Post savedPost = postCaptor.getValue();

        assertThat(savedPost.getTitle()).isEqualTo(newTitle);
        assertThat(savedPost.getContent()).isEqualTo(newContent);
        List<CategoryId> finalCategoryIds = savedPost.getCategoryIds();
        assertThat(finalCategoryIds).containsExactlyInAnyOrder(categoryId2, categoryId3, categoryId4);
        assertThat(finalCategoryIds).doesNotContain(categoryId1);
    }

    @Test
    @DisplayName("Should throw ShouldThrowCategoryNotFoundException when an invalid Category ID is provided")
    void editPost_invalidCategoryIds_ShouldThrowCategoryNotFoundException() {
        // Arrange
        PostEditCommand command = new PostEditCommand(
                "Updated title",
                "Updated Content",
                List.of(categoryUUID2, categoryUUID3, categoryUUID4)
        );

        when(postRepository.findById(postId)).thenReturn(existingPost);

        // categoryIds to find has size is 2, but repo return only one, should throw by method PostService.validateCategories
        List<CategoryId> expectedIdsToAdd = List.of(categoryId3, categoryId4);
        List<Category> categoriesFoundForAdding = List.of(category4);
        when(categoryRepository.findCategoryByIds(expectedIdsToAdd)).thenReturn(categoriesFoundForAdding);

        // Act & Assert
        assertThatThrownBy(() -> service.editPost(postIdUUID, command))
                .isInstanceOf(CategoryNotFoundException.class);

        verify(postRepository, times(1)).findById(postId);
        verify(categoryRepository, times(1)).findCategoryByIds(categoryIdListCaptor.capture());
        assertThat(categoryIdListCaptor.getValue())
                .containsExactlyInAnyOrderElementsOf(expectedIdsToAdd);
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    @DisplayName("Should soft delete Post successfully")
    void softDeletePost_Success() {
        // Arrange
        when(postRepository.findById(postId)).thenReturn(existingPost);

        // Act
        service.deletePost(postIdUUID);

        // Assert
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).save(postCaptor.capture());
        Post deletedPost = postCaptor.getValue();
        assertThat(deletedPost.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("Should throw PostAlreadyPublishedException when soft delete a published Post")
    void softDeletePost_PublishedPost_ThrowException() {
        // Arrange
        existingPost.publishPost();
        when(postRepository.findById(postId)).thenReturn(existingPost);

        // Act & Assert
        assertThatThrownBy(() -> service.deletePost(postIdUUID))
                .isInstanceOf(PostAlreadyPublishedException.class);
    }
}
