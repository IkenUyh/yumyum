package com.uit.fooddelivery_api.modules.restaurant.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.restaurant.dtos.CreateRestaurantDTO;
import com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantResponseDTO;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.services.RestaurantService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final com.uit.fooddelivery_api.modules.food.services.FoodService foodService;

    @PostMapping
    public ApiResponse<RestaurantResponseDTO> createRestaurant(
            Authentication authentication,
            @RequestBody CreateRestaurantDTO dto) {

        // Lay thong tin user dang dang nhap tu Token
        User merchant = (User) authentication.getPrincipal();

        // Goi service tao nha hang
        Restaurant savedRestaurant = restaurantService.createRestaurant(dto, merchant);

        // Chuyen sang DTO va tra ve ket qua
        return ApiResponse.success(RestaurantResponseDTO.fromEntity(savedRestaurant));
    }

    // 1. API lay tat ca nha hang cho khach hang xem
    @GetMapping
    public ApiResponse<java.util.List<RestaurantResponseDTO>> getAllRestaurants() {
        java.util.List<RestaurantResponseDTO> list = restaurantService.getAllRestaurants()
                .stream()
                .map(RestaurantResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // 2. API lay thuc don (Menu) cua mot nha hang cu the
    @GetMapping("/{id}/foods")
    public ApiResponse<java.util.List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO>> getRestaurantMenu(
            @PathVariable("id") Long restaurantId) {

        java.util.List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO> menu = foodService.getFoodsByRestaurant(restaurantId)
                .stream()
                .map(com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(menu);
    }
}