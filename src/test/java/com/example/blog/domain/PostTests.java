package com.example.blog.domain;

// NOTE: Only import from domain package
import com.example.blog.domain.exception.*;
import com.example.blog.domain.valueobject.Author;
import com.example.blog.domain.valueobject.CategoryId;
import com.example.blog.domain.valueobject.CommentId;
import com.example.blog.domain.valueobject.Commenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Post Domain Model Tests")
class PostTests {

    private Author author;
    private CategoryId categoryId1;
    private CategoryId categoryId2;
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        author = new Author("Test Author");
        categoryId1 = new CategoryId();
        categoryId2 = new CategoryId();
        category1 = Category.reconstitute(categoryId1, "Tech");
        category2 = Category.reconstitute(categoryId2, "News");
    }

    @Test
    @DisplayName("Should create post successfully with categories")
    void testCreatePost_Success() {
        // Arrange
        String title = "My First Post";
        String content = "This is the content.";

        // Act
        Post post = new Post(title, content, author, category1, category2);

        // Assert
        assertNotNull(post.getId());
        assertEquals(title, post.getTitle());
        assertEquals(content, post.getContent());
        assertEquals(author, post.getAuthor());
        assertNotNull(post.getCreatedAt());
        assertNull(post.getDeletedAt());
        assertFalse(post.isDeleted());
        assertThat(post.getCategories()).containsExactlyInAnyOrder(category1, category2);
        assertTrue(post.getComments().isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when creating post with null title")
    void testCreatePost_NullTitle_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Post(null, "Content", author));
    }

    @Test
    @DisplayName("Should throw exception when creating post with null content")
    void testCreatePost_NullContent_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Post("Title", null, author));
    }

    @Test
    @DisplayName("Should throw exception when creating post with null author")
    void testCreatePost_NullAuthor_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Post("Title", "Content", null));
    }

    @Test
    @DisplayName("Should throw exception when adding duplicate category name during creation")
    void testCreatePost_DuplicateCategoryName_ShouldThrowException() {
        // Arrange
        Category cat1 = new Category("Duplicate");
        Category cat2 = new Category("Duplicate"); // Same name

        // Act & Assert
        // Exception xảy ra trong constructor khi gọi addCategory
        assertThrows(CategoryAlreadyExistsException.class,
                () -> new Post("Title", "Content", author, cat1, cat2));
    }

    @Test
    @DisplayName("Should update post successfully")
    void testUpdatePost_Success() {
        // Arrange
        Post post = new Post("Old Title", "Old Content", author, category1);
        LocalDateTime initialUpdatedAt = post.getUpdatedAt();
        String newTitle = "Updated Title";
        String newContent = "Updated Content";

        // Act
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Post updatedPost = post.updatePost(newTitle, newContent);

        // Assert
        assertSame(post, updatedPost);
        assertEquals(newTitle, post.getTitle());
        assertEquals(newContent, post.getContent());
        assertNotEquals(initialUpdatedAt, post.getUpdatedAt());
        assertFalse(post.isDeleted());
    }

    @Test
    @DisplayName("Should throw exception when updating post with null title")
    void testUpdatePost_NullTitle_ShouldThrowException() {
        Post post = new Post("Title", "Content", author);
        assertThrows(IllegalArgumentException.class, () -> post.updatePost(null, "New Content"));
    }

    @Test
    @DisplayName("Should throw exception when updating post with null content")
    void testUpdatePost_NullContent_ShouldThrowException() {
        Post post = new Post("Title", "Content", author);
        assertThrows(IllegalArgumentException.class, () -> post.updatePost("New Title", null));
    }

    @Test
    @DisplayName("Should throw exception when updating a deleted post")
    void testUpdatePost_WhenDeleted_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.softDelete();

        // Act & Assert
        assertThrows(PostAlreadyDeletedException.class,
                () -> post.updatePost("New Title", "New Content"));
    }

    @Test
    @DisplayName("Should throw exception when updating a published post")
    void testUpdatePost_WhenPublished_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.publishPost();

        // Act & Assert
        assertThrows(PostAlreadyPublishedException.class,
                () -> post.updatePost("New Title", "New Content"));
    }

    @Test
    @DisplayName("Should publish post successfully")
    void testPublishPost_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);

        // Act
        post.publishPost();

        // Assert
        assertTrue(post.isPublished());
        assertFalse(post.isDeleted());
        assertFalse(post.isCategoryUpdatable());
    }

    @Test
    @DisplayName("Should throw exception when publish a deleted post")
    void testPublishPost_WhenDeleted_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.softDelete();

        assertTrue(post.isDeleted());
        assertFalse(post.isPublished());

        // Act
        assertThrows(PostAlreadyDeletedException.class, post::publishPost);
    }

    // add category
    @Test
    @DisplayName("Should add Category successfully")
    void testAddCategory_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author, category1);
        assertEquals(1, post.getCategories().size());

        Category category = new Category("New Category");

        // Act
        post.addCategory(category);

        assertEquals(2, post.getCategories().size());
        assertThat(post.getCategories()).containsExactlyInAnyOrder(category1, category);
    }

    @Test
    @DisplayName("Should throw CategoryAlreadyExistsException when the specified category's name already exists")
    void testAddCategory_ValidCategoryName_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author, category1);
        assertEquals(1, post.getCategories().size());

        Category category = new Category(category1.getName());

        // Act & Assert
        assertThrows(CategoryAlreadyExistsException.class, () -> post.addCategory(category));
    }

    @Test
    @DisplayName("Should throw CannotChangeCategoryException when add a Category to a published post")
    void testAddCategory_PublishedPost_ThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author, category1);
        post.publishPost();

        // Act & Assert
        Category category = new Category(category1.getName());
        assertThrows(CannotChangeCategoryException.class, () -> post.addCategory(category));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when the new category to add is null")
    void testAddCategory_NullCategory_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> post.addCategory(null));
    }

    @Test
    @DisplayName("Should remove Category successfully")
    void testRemoveCategory_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author, category1, category2);

        assertEquals(2, post.getCategories().size());

        // Act
        post.removeCategory(categoryId1);

        // Assert
        assertEquals(1, post.getCategories().size());
        assertThat(post.getCategories()).containsExactlyInAnyOrder(category2);
    }

    @Test
    void testRemoveCategory_PublishedPost_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author, category1, category2);
        post.publishPost();

        // Act & Assert
        assertThrows(CannotChangeCategoryException.class, () -> post.removeCategory(categoryId1));
    }

    @Test
    @DisplayName("Should soft delete post successfully")
    void testSoftDelete_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        LocalDateTime initialUpdatedAt = post.getUpdatedAt();

        // Act
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        post.softDelete();

        // Assert
        assertNotNull(post.getDeletedAt());
        assertTrue(post.isDeleted());
        assertNotEquals(initialUpdatedAt, post.getUpdatedAt()); // UpdatedAt should be updated on delete
    }

    @Test
    @DisplayName("Should throw exception when soft delete a published post")
    void testSoftDelete_WhenPostAlreadyPublished_ThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.publishPost();

        // Act & Assert
        assertThrows(PostAlreadyPublishedException.class, post::softDelete);
    }

    @Test
    @DisplayName("Should add comment successfully")
    void testAddComment_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("My comment", new Commenter("User1"));

        post.publishPost();

        // Act
        post.addComment(comment);

        // Assert
        assertEquals(1, post.getComments().size());
        assertEquals(comment.getId(), post.getComments().get(0).getId());
    }

    @Test
    @DisplayName("Should throw CannotAddCommentException when add a comment to unpublish post")
    void testAddComment_UnpublishedPost_ShouldThrowException() {
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("My comment", new Commenter("User1"));

        // Act & Assert
        assertThrows(PostNotPublishedException.class, () -> post.addComment(comment));
    }

    @Test
    @DisplayName("Should throw exception when adding null comment")
    void testAddComment_NullComment_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.publishPost();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> post.addComment(null));
    }

    @Test
    @DisplayName("Should remove comment successfully")
    void testRemoveComment_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("My comment", new Commenter("User1"));

        post.publishPost();

        post.addComment(comment);
        assertEquals(1, post.getComments().size());

        // Act
        post.removeComment(comment.getId());

        // Assert
        assertTrue(post.getComments().isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when removing null comment id")
    void testRemoveComment_NullComment_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.publishPost();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> post.removeComment(null));
    }

    @Test
    @DisplayName("Should throw exception when removing comment from a un published post")
    void testRemoveComment_WhenUnpublished_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("My comment", new Commenter("User1"));

        post.publishPost();

        post.addComment(comment);

        post.unPublishPost();

        // Act & Assert
        assertThrows(PostNotPublishedException.class, () -> post.removeComment(comment.getId()));
    }

    @Test
    @DisplayName("Should approve comment successfully when post already published")
    void testApproveComment_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("Comment to approve", new Commenter("User1"));

        post.publishPost();

        post.addComment(comment);
        CommentId commentId = comment.getId();

        // Act
        post.approveComment(commentId);

        // Assert
        Comment fetchedComment = post.getComment(commentId);
        assertNotNull(fetchedComment);
        assertTrue(fetchedComment.isApproved());
    }

    @Test
    @DisplayName("Should throw exception when approving comment on a un publish post")
    void testApproveComment_WhenUnPublished_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("Comment", new Commenter("User1"));

        post.publishPost();

        post.addComment(comment);
        CommentId commentId = comment.getId();

        post.unPublishPost();

        // Act & Assert
        assertThrows(PostNotPublishedException.class, () -> post.approveComment(commentId));
    }

    @Test
    @DisplayName("Should handle approving non-existent comment gracefully (no-op)")
    void testApproveComment_NotFound() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.publishPost();

        CommentId nonExistentCommentId = new CommentId(); // ID non exist

        // Act
        Executable action = () -> post.approveComment(nonExistentCommentId);

        // Assert
        assertDoesNotThrow(action);
    }

    @Test
    @DisplayName("Should cancel comment approval successfully")
    void testCancelApprovalComment_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("Comment", new Commenter("User1"));

        post.publishPost();

        post.addComment(comment);
        CommentId commentId = comment.getId();

        post.approveComment(commentId); // Approve first
        assertTrue(post.getComment(commentId).isApproved());

        // Act
        post.cancelApprovalComment(commentId);

        // Assert
        Comment fetchedComment = post.getComment(commentId);
        assertNotNull(fetchedComment);
        assertFalse(fetchedComment.isApproved());
    }

    @Test
    @DisplayName("Should throw exception when cancelling approval on a un published post")
    void testCancelApprovalComment_WhenUnpublished_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("Comment", new Commenter("User1"));

        post.publishPost();

        post.addComment(comment);

        CommentId commentId = comment.getId();
        post.approveComment(commentId);

        post.unPublishPost();

        // Act & Assert
        assertThrows(PostNotPublishedException.class, () -> post.cancelApprovalComment(commentId));
    }

    @Test
    @DisplayName("Should handle cancelling approval of non-existent comment gracefully (no-op)")
    void testCancelApprovalComment_NotFound() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.publishPost();
        CommentId nonExistentCommentId = new CommentId();

        // Act
        Executable action = () -> post.cancelApprovalComment(nonExistentCommentId);

        // Assert
        assertDoesNotThrow(action);
    }

}
