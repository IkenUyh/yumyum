package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.food.entities.Category;
import com.uit.fooddelivery_api.modules.food.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category1;
    private Category category2;

    @BeforeEach
    void setUp() {
        category1 = Category.builder()
                .id(1L)
                .name("Cơm")
                .imageUrl("https://res.cloudinary.com/demo/com.jpg")
                .build();

        category2 = Category.builder()
                .id(2L)
                .name("Bánh Mì")
                .imageUrl("https://res.cloudinary.com/demo/banhmi.jpg")
                .build();
    }

    @Test
    void getAllCategories_shouldReturnAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(category1, category2));

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cơm", result.get(0).getName());
        assertEquals("Bánh Mì", result.get(1).getName());
    }
}
