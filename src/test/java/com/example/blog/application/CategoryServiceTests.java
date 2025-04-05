package com.example.blog.application;

import com.example.blog.domain.Category;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.exception.CategoryAlreadyExistsException;
import com.example.blog.domain.valueobject.CategoryId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Category Service Tests")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService service;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;
    @Captor
    private ArgumentCaptor<String> nameCaptor;
    @Captor
    private ArgumentCaptor<CategoryId> categoryIdCaptor;

    @Test
    @DisplayName("Should create category successfully when no category with the specified name already exists")
    void createCategory_shouldCreateCategory_Success() {
        // Arrange
        String name = "Tech";
        when(categoryRepository.existByName(name)).thenReturn(false);

        // Act
        UUID id = service.createCategory(name);

        // Assert
        assertThat(id).isNotNull();
        verify(categoryRepository, times(1)).save(categoryCaptor.capture());
        Category category = categoryCaptor.getValue();
        assertThat(category.getName()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should throw CategoryAlreadyExistsException when create Category with the specified name already exists")
    void createCategory_NameAlreadyExists_ShouldThrowCategoryAlreadyExistsException() {
        // Arrange
        String name = "Tech";
        when(categoryRepository.existByName(name)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> service.createCategory(name))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        verify(categoryRepository, never()).save(any(Category.class));
        verify(categoryRepository, times(1)).existByName(nameCaptor.capture());
        assertThat(nameCaptor.getValue()).isEqualTo(name);
    }

    @Test
    @DisplayName("Should update Category successfully when no category with the specified name already exists")
    void updateCategory_shouldUpdateCategory_Success() {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryId categoryId = new CategoryId(id);
        String name = "Tech";
        Category category = new Category(name);
        String newName = "New Name";
        when(categoryRepository.findById(categoryId)).thenReturn(category);
        when(categoryRepository.existByName(newName)).thenReturn(false);

        // Act
        service.editCategory(id, newName);

        // Assert
        verify(categoryRepository, times(1)).findById(categoryIdCaptor.capture());
        verify(categoryRepository, times(1)).existByName(newName);
        verify(categoryRepository, times(1)).save(categoryCaptor.capture());
        assertThat(newName).isEqualTo(categoryCaptor.getValue().getName());
        assertThat(categoryIdCaptor.getValue()).isEqualTo(categoryId);
    }

    @Test
    @DisplayName("Should throw CategoryAlreadyExistsException when update Category with the specified name already exists")
    void updateCategory_NameAlreadyExists_ShouldThrowCategoryAlreadyExistsException() {
        // Arrange
        UUID id = UUID.randomUUID();
        CategoryId categoryId = new CategoryId(id);
        String name = "Tech";
        Category category = new Category(name);
        String newName = "New Name";
        when(categoryRepository.findById(categoryId)).thenReturn(category);
        when(categoryRepository.existByName(newName)).thenReturn(true);

        // Act
        assertThatThrownBy(() -> service.editCategory(id, newName))
                .isInstanceOf(CategoryAlreadyExistsException.class);

        // Assert
        verify(categoryRepository, times(1)).findById(categoryIdCaptor.capture());
        verify(categoryRepository, times(1)).existByName(newName);
        verify(categoryRepository, never()).save(any(Category.class));
        assertThat(categoryIdCaptor.getValue()).isEqualTo(categoryId);
    }


}
