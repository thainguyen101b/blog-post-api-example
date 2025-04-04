package com.example.blog.domain;

import com.example.blog.domain.exception.CommentAlreadyApprovedException;
import com.example.blog.domain.valueobject.Commenter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Comment Domain Model Tests")
class CommentTests {

    private Commenter commenter;

    @BeforeEach
    void setUp() {
        commenter = new Commenter("Test Commenter");
    }

    @Test
    @DisplayName("Should create comment successfully")
    void testCreateComment_Success() {
        // Arrange
        String content = "This is a comment";

        // Act
        Comment comment = new Comment(content, commenter);

        // Assert
        assertNotNull(comment.getId());
        assertEquals(content, comment.getContent());
        assertEquals(commenter, comment.getCommenter());
        assertNotNull(comment.getCreatedAt());
        assertNotNull(comment.getUpdatedAt());
        assertNull(comment.getApprovedAt());
        assertFalse(comment.isApproved());
    }

    @Test
    @DisplayName("Should throw exception when creating comment with null content")
    void testCreateComment_NullContent_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Comment(null, commenter));
    }

    @Test
    @DisplayName("Should throw exception when creating comment with null commenter")
    void testCreateComment_NullCommenter_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Comment("Valid content", null));
    }

    @Test
    @DisplayName("Should update comment content successfully and reset approval")
    void testUpdateComment_Success_ShouldResetApproval() {
        // Arrange
        Comment comment = new Comment("Old content", commenter);
        comment.approve(); // Approve it first
        assertTrue(comment.isApproved());
        LocalDateTime initialUpdatedAt = comment.getUpdatedAt();
        String newContent = "Updated content";

        // Act
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Comment updatedComment = comment.updateComment(newContent);

        // Assert
        assertSame(comment, updatedComment);
        assertEquals(newContent, comment.getContent());
        assertNotEquals(initialUpdatedAt, comment.getUpdatedAt()); // UpdatedAt should change
        assertNull(comment.getApprovedAt()); // Approval should be reset
        assertFalse(comment.isApproved());
    }

    @Test
    @DisplayName("Should throw exception when updating comment with null content")
    void testUpdateComment_NullContent_ShouldThrowException() {
        // Arrange
        Comment comment = new Comment("Valid content", commenter);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> comment.updateComment(null));
    }

    @Test
    @DisplayName("Should approve comment successfully")
    void testApproveComment_Success() {
        // Arrange
        Comment comment = new Comment("Comment to approve", commenter);

        // Act
        comment.approve();

        // Assert
        assertNotNull(comment.getApprovedAt());
        assertTrue(comment.isApproved());
    }

    @Test
    @DisplayName("Should throw exception when approving an already approved comment")
    void testApproveComment_AlreadyApproved_ShouldThrowException() {
        // Arrange
        Comment comment = new Comment("Comment", commenter);
        comment.approve(); // First approval

        // Act & Assert
        assertThrows(CommentAlreadyApprovedException.class, comment::approve); // Second approval
    }

    @Test
    @DisplayName("Should cancel approval successfully")
    void testCancelApproval_Success() {
        // Arrange
        Comment comment = new Comment("Comment", commenter);
        comment.approve();
        assertTrue(comment.isApproved());

        // Act
        comment.cancelApproval();

        // Assert
        assertNull(comment.getApprovedAt());
        assertFalse(comment.isApproved());
    }

    @Test
    @DisplayName("Should do nothing when cancelling approval of a non-approved comment")
    void testCancelApproval_WhenNotApproved_ShouldDoNothing() {
        // Arrange
        Comment comment = new Comment("Comment", commenter);
        assertFalse(comment.isApproved());

        // Act
        comment.cancelApproval(); // Should not throw exception

        // Assert
        assertNull(comment.getApprovedAt());
        assertFalse(comment.isApproved());
    }

}
