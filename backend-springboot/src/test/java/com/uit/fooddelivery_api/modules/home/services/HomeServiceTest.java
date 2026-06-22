package com.uit.fooddelivery_api.modules.home.services;

import com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem;
import com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleItemRepository;
import com.uit.fooddelivery_api.modules.food.entities.Category;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.repositories.CategoryRepository;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.home.dtos.*;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HomeServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private FoodRepository foodRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FlashSaleItemRepository flashSaleItemRepository;

    @InjectMocks
    private HomeService homeService;

    private Restaurant restaurant;
    private Food food;
    private Category category;

    @BeforeEach
    void setUp() {
        restaurant = Restaurant.builder()
                .id(1L)
                .name("Restaurant ABC")
                .address("123 Street")
                .ratingAverage(BigDecimal.valueOf(4.8))
                .reviewCount(120)
                .build();

        category = Category.builder()
                .id(1L)
                .name("Burgers")
                .build();

        food = Food.builder()
                .id(1L)
                .name("Beef Burger")
                .price(BigDecimal.valueOf(50000))
                .isAvailable(true)
                .restaurant(restaurant)
                .category(category)
                .build();
    }

    @Test
    void getHomeCore_shouldReturnHomeCoreResponse() {
        when(categoryRepository.findAll()).thenReturn(List.of(category));
        when(flashSaleItemRepository.findActiveFlashSaleItems(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(foodRepository.findAllWithRestaurant()).thenReturn(List.of(food));

        HomeCoreResponseDTO response = homeService.getHomeCore("addr1");

        assertNotNull(response);
        assertEquals(3, response.getBanners().size());
        assertEquals(2, response.getCategories().size()); // Burgers + Danh mục
        assertEquals("Burgers", response.getCategories().get(0).getName());
        assertEquals("Danh mục", response.getCategories().get(1).getName());
        assertTrue(response.getCategories().get(1).isSelectAll());
        assertFalse(response.getFlashSales().isEmpty()); // Fallback to food list
    }

    @Test
    void getPopularBrands_shouldReturnBrandResponse() {
        when(foodRepository.findAllWithRestaurant()).thenReturn(List.of(food));

        BrandResponseDTO response = homeService.getPopularBrands("addr1");

        assertNotNull(response);
        assertEquals(1, response.getBrands().size());
        RestaurantHomeDTO brand = response.getBrands().get(0);
        assertEquals("Restaurant ABC", brand.getName());
        assertEquals("Restaurant", brand.getShortName());
        assertEquals(1, brand.getMenu().size());
    }

    @Test
    void getRecommendedDeals_shouldReturnDealResponse() {
        when(foodRepository.findAllWithRestaurant()).thenReturn(List.of(food));

        DealResponseDTO response = homeService.getRecommendedDeals("addr1", 0, 1, 10);

        assertNotNull(response);
        assertEquals(1, response.getDeals().size());
        RecommendedDealDTO deal = response.getDeals().get(0);
        assertEquals("Beef Burger", deal.getFoodTitle());
        assertEquals("Restaurant ABC", deal.getStoreName());
    }
}
