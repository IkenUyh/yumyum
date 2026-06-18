package com.uit.fooddelivery_api.modules.food.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.food.dtos.CategoryResponseDTO;
import com.uit.fooddelivery_api.modules.food.dtos.CategoryFoodCountResponseDTO;
import com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO;
import com.uit.fooddelivery_api.modules.food.services.CategoryService;
import com.uit.fooddelivery_api.modules.food.services.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final FoodService foodService;

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

    @GetMapping("/{id}/foods")
    public ApiResponse<List<FoodResponseDTO>> getFoodsByCategory(@PathVariable("id") Long categoryId) {
        List<FoodResponseDTO> list = foodService.getFoodsByCategory(categoryId)
                .stream()
                .map(FoodResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }
}
