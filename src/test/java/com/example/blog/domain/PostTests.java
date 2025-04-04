package com.example.blog.domain;

import com.example.blog.domain.exception.CategoryAlreadyExistsException;
import com.example.blog.domain.exception.PostAlreadyDeletedException;
import com.example.blog.domain.valueobject.Author;
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
    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        author = new Author("Test Author");
        category1 = new Category("Tech");
        category2 = new Category("News");
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
    @DisplayName("Should add comment successfully")
    void testAddComment_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("My comment", new Commenter("User1"));

        // Act
        post.addComment(comment);

        // Assert
        assertEquals(1, post.getComments().size());
        assertEquals(comment.getId(), post.getComments().get(0).getId());
    }

    @Test
    @DisplayName("Should throw exception when adding null comment")
    void testAddComment_NullComment_ShouldThrowException() {
        Post post = new Post("Title", "Content", author);
        assertThrows(IllegalArgumentException.class, () -> post.addComment(null));
    }

    @Test
    @DisplayName("Should throw exception when adding comment to a deleted post")
    void testAddComment_WhenDeleted_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        post.softDelete();
        Comment comment = new Comment("My comment", new Commenter("User1"));

        // Act & Assert
        assertThrows(PostAlreadyDeletedException.class, () -> post.addComment(comment));
    }

    @Test
    @DisplayName("Should remove comment successfully")
    void testRemoveComment_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("My comment", new Commenter("User1"));
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
        Post post = new Post("Title", "Content", author);
        assertThrows(IllegalArgumentException.class, () -> post.removeComment(null));
    }

    @Test
    @DisplayName("Should throw exception when removing comment from a deleted post")
    void testRemoveComment_WhenDeleted_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("My comment", new Commenter("User1"));
        post.addComment(comment);
        post.softDelete();


        // Act & Assert
        assertThrows(PostAlreadyDeletedException.class, () -> post.removeComment(comment.getId()));
    }

    @Test
    @DisplayName("Should approve comment successfully")
    void testApproveComment_Success() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("Comment to approve", new Commenter("User1"));
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
    @DisplayName("Should throw exception when approving comment on a deleted post")
    void testApproveComment_WhenDeleted_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("Comment", new Commenter("User1"));
        post.addComment(comment);
        CommentId commentId = comment.getId();
        post.softDelete();

        // Act & Assert
        assertThrows(PostAlreadyDeletedException.class, () -> post.approveComment(commentId));
    }

    @Test
    @DisplayName("Should handle approving non-existent comment gracefully (no-op)")
    void testApproveComment_NotFound() {
        // Arrange
        Post post = new Post("Title", "Content", author);
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
    @DisplayName("Should throw exception when cancelling approval on a deleted post")
    void testCancelApprovalComment_WhenDeleted_ShouldThrowException() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        Comment comment = new Comment("Comment", new Commenter("User1"));
        post.addComment(comment);
        CommentId commentId = comment.getId();
        post.approveComment(commentId);
        post.softDelete();

        // Act & Assert
        assertThrows(PostAlreadyDeletedException.class, () -> post.cancelApprovalComment(commentId));
    }

    @Test
    @DisplayName("Should handle cancelling approval of non-existent comment gracefully (no-op)")
    void testCancelApprovalComment_NotFound() {
        // Arrange
        Post post = new Post("Title", "Content", author);
        CommentId nonExistentCommentId = new CommentId();

        // Act
        Executable action = () -> post.cancelApprovalComment(nonExistentCommentId);

        // Assert
        assertDoesNotThrow(action);
    }

}
