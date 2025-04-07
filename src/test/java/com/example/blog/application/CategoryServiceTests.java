package com.example.blog.application;

import com.example.blog.application.exception.CannotDeleteCategoryException;
import com.example.blog.domain.Category;
import com.example.blog.domain.CategoryRepository;
import com.example.blog.domain.PostRepository;
import com.example.blog.domain.exception.CategoryAlreadyExistsException;
import com.example.blog.domain.valueobject.CategoryId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Category Service Tests")
@ExtendWith(MockitoExtension.class)
class CategoryServiceTests {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CategoryService service;

    @Captor
    private ArgumentCaptor<Category> categoryCaptor;
    @Captor
    private ArgumentCaptor<String> nameCaptor;
    @Captor
    private ArgumentCaptor<CategoryId> categoryIdCaptor;

    private UUID categoryUuid;
    private CategoryId categoryId;
    private Category existingCategory;

    @BeforeEach
    void setUp() {
        categoryUuid = UUID.randomUUID();
        categoryId = new CategoryId(categoryUuid);
        existingCategory = Mockito.mock(Category.class);
    }

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

    @Test
    @DisplayName("Should delete Category successfully")
    void deleteCategory_Success() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(existingCategory);
        when(postRepository.existsByCategory(categoryId)).thenReturn(false);

        // Act
        service.deleteCategory(categoryUuid);

        // Assert
        verify(categoryRepository, times(1)).delete(categoryCaptor.capture());
        Category category = categoryCaptor.getValue();
        assertEquals(existingCategory, category);
        verify(postRepository, times(1)).existsByCategory(categoryId);
    }

    @Test
    @DisplayName("Should throw CannotDeleteCategoryException when delete category but it is in use")
    void deleteCategory_InUse_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(categoryId)).thenReturn(existingCategory);
        when(postRepository.existsByCategory(categoryId)).thenReturn(true);

        // Act
        assertThatThrownBy(() -> service.deleteCategory(categoryUuid))
                .isInstanceOf(CannotDeleteCategoryException.class);

        // Assert
        verify(postRepository, times(1)).existsByCategory(categoryId);
        verify(categoryRepository, never()).delete(any(Category.class));
    }


}
