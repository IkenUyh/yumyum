package com.uit.fooddelivery_api.modules.food.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.food.dtos.CategoryResponseDTO;
import com.uit.fooddelivery_api.modules.food.dtos.CategoryFoodCountResponseDTO;
import com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.food.services.CategoryService;
import com.uit.fooddelivery_api.modules.food.services.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final FoodService foodService;
    private final FoodRepository foodRepository;

    @GetMapping
    public ApiResponse<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> list = categoryService.getAllCategories()
                .stream()
                .map(CategoryResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    @GetMapping("/food-count")
    public ApiResponse<List<CategoryFoodCountResponseDTO>> getCategoryFoodCounts() {
        return ApiResponse.success(categoryService.getCategoryFoodCounts());
    }

    // API lấy danh mục có món theo restaurant_id (dùng cho màn hình Merchant Menu)
    @GetMapping("/by-restaurant/{restaurantId}")
    public ApiResponse<List<CategoryResponseDTO>> getCategoriesByRestaurant(
            @PathVariable("restaurantId") Long restaurantId) {
        List<CategoryResponseDTO> list = foodRepository
                .findDistinctCategoriesByRestaurantId(restaurantId)
                .stream()
                .map(CategoryResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    @GetMapping("/{id}/foods")
    public ApiResponse<List<FoodResponseDTO>> getFoodsByCategory(@PathVariable("id") Long categoryId) {
        List<FoodResponseDTO> list = foodService.getFoodsByCategory(categoryId)
                .stream()
                .map(FoodResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    @PostMapping
    public ApiResponse<CategoryResponseDTO> createCategory(
            @org.springframework.web.bind.annotation.RequestBody com.uit.fooddelivery_api.modules.food.dtos.CreateCategoryDTO dto) {
        CategoryResponseDTO saved = CategoryResponseDTO.fromEntity(categoryService.createCategory(dto));
        return ApiResponse.success(saved);
    }

    @PutMapping("/{id}")
    public ApiResponse<CategoryResponseDTO> updateCategory(
            @PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.RequestBody com.uit.fooddelivery_api.modules.food.dtos.CreateCategoryDTO dto) {
        CategoryResponseDTO updated = CategoryResponseDTO.fromEntity(categoryService.updateCategory(id, dto));
        return ApiResponse.success(updated);
    }
}
