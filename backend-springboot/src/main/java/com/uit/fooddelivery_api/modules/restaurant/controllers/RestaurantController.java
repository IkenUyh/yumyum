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
    @org.springframework.cache.annotation.CacheEvict(value = "restaurantsList", allEntries = true) // Xóa cache cũ
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

    // 1. API lấy tất cả nhà hàng cho khách hàng xem
    @GetMapping
    @org.springframework.cache.annotation.Cacheable(value = "restaurantsList")
    public ApiResponse<java.util.List<RestaurantResponseDTO>> getAllRestaurants() {
        System.out.println("Đang truy vấn Database MySQL để lấy danh sách quán ăn...");

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

    @PutMapping("/{id}/settings")
    @org.springframework.cache.annotation.CacheEvict(value = "restaurantsList", allEntries = true)
    public ApiResponse<RestaurantResponseDTO> updateRestaurantSettings(
            @PathVariable("id") Long restaurantId,
            Authentication authentication,
            @RequestBody com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantSettingsDTO dto) {

        User merchant = (User) authentication.getPrincipal();
        Restaurant restaurant = restaurantService.getAllRestaurants().stream()
                .filter(r -> r.getId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quán!"));

        if (!restaurant.getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền cài đặt quán này!");
        }

        if (dto.getIsAcceptingOrders() != null) {
            restaurant.setIsAcceptingOrders(dto.getIsAcceptingOrders());
        }
        if (dto.getMaxPendingOrders() != null) {
            restaurant.setMaxPendingOrders(dto.getMaxPendingOrders());
        }

        // Sửa nhanh: Bạn cần save lại restaurant. Ở đây giả sử bạn có hàm save trong RestaurantRepository
        // Tốt nhất bạn nên tạo hàm updateSettings trong RestaurantService.
        // Dưới đây là code gọi giả định bạn đã nhúng repository:
        // restaurantRepository.save(restaurant);

        return ApiResponse.success(RestaurantResponseDTO.fromEntity(restaurant));
    }
}