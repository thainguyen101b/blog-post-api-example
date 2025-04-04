package com.example.blog.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Category Domain Model Tests")
class CategoryTests {

    @Test
    @DisplayName("Should create category successfully")
    void testCreateCategory_Success() {
        // Arrange
        String name = "Technology";

        // Act
        Category category = new Category(name);

        // Assert
        assertNotNull(category.getId());
        assertEquals(name, category.getName());
    }

    @Test
    @DisplayName("Should throw exception when creating category with null name")
    void testCreateCategory_NullName_ShouldThrowException() {
        // Arrange
        String name = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> new Category(name));
    }

    @Test
    @DisplayName("Should update category name successfully")
    void testUpdateCategory_Success() {
        // Arrange
        Category category = new Category("Old Name");
        String newName = "New Name";

        // Act
        Category updatedCategory = category.updateCategory(newName);

        // Assert
        assertSame(category, updatedCategory); // Should return the same instance
        assertEquals(newName, category.getName());
    }

    @Test
    @DisplayName("Should throw exception when updating category with null name")
    void testUpdateCategory_NullName_ShouldThrowException() {
        // Arrange
        Category category = new Category("Valid Name");
        String newName = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> category.updateCategory(newName));
    }

}
