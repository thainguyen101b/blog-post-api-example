package com.example.blog.application;

import com.example.blog.application.command.PostCreateCommand;
import com.example.blog.application.exception.CategoryNotFoundException;
import com.example.blog.domain.Category;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.Post;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.valueobject.Author;
import com.example.blog.domain.valueobject.CategoryId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @BeforeEach
    void setUp() {
        UUID authorUUID = UUID.randomUUID();
        authorIdString = authorUUID.toString();
        author = new Author(authorIdString);
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

        // Create mock Category objects that the repository should "find"
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

}
