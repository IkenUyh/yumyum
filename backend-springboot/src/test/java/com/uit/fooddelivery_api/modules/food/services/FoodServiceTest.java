package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.food.entities.Category;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.food.repositories.CategoryRepository;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.services.CloudinaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CloudinaryService cloudinaryService;

    @InjectMocks
    private FoodService foodService;

    private Category category;
    private Food food1;
    private Food food2;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Cơm")
                .build();

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .name("Cơm Tấm")
                .build();

        food1 = Food.builder()
                .id(10L)
                .name("Cơm Tấm Sườn")
                .price(BigDecimal.valueOf(35000))
                .isAvailable(true)
                .category(category)
                .restaurant(restaurant)
                .build();

        food2 = Food.builder()
                .id(11L)
                .name("Cơm Tấm Chả")
                .price(BigDecimal.valueOf(30000))
                .isAvailable(true)
                .category(category)
                .restaurant(restaurant)
                .build();
    }

    @Test
    void getFoodsByCategory_shouldReturnAvailableFoods_whenCategoryExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(foodRepository.findByCategoryIdAndIsAvailableTrue(1L)).thenReturn(List.of(food1, food2));

        List<Food> result = foodService.getFoodsByCategory(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Cơm Tấm Sườn", result.get(0).getName());
        assertEquals("Cơm Tấm Chả", result.get(1).getName());

        verify(categoryRepository, times(1)).findById(1L);
        verify(foodRepository, times(1)).findByCategoryIdAndIsAvailableTrue(1L);
    }

    @Test
    void getFoodsByCategory_shouldThrowException_whenCategoryDoesNotExist() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            foodService.getFoodsByCategory(99L);
        });

        assertEquals("Không tìm thấy danh mục món ăn!", exception.getMessage());
        verify(categoryRepository, times(1)).findById(99L);
        verify(foodRepository, never()).findByCategoryIdAndIsAvailableTrue(anyLong());
    }
}
