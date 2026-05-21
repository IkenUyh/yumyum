package com.uit.fooddelivery_api.modules.food.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.food.dtos.CreateFoodDTO;
import com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.services.FoodService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    public ApiResponse<FoodResponseDTO> createFood(
            Authentication authentication,
            @RequestBody CreateFoodDTO dto) {

        // Lấy thông tin chủ quán đang đăng nhập từ Token gác cổng
        User merchant = (User) authentication.getPrincipal();

        // Tiến hành thêm món ăn
        Food savedFood = foodService.createFood(dto, merchant);

        return ApiResponse.success(FoodResponseDTO.fromEntity(savedFood));
    }
}