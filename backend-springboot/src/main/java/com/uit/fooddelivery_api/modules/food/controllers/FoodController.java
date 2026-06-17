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

    // =====================================================
    // PUBLIC ENDPOINTS - Không cần đăng nhập
    // =====================================================

    // Lấy tất cả món ăn đang bán (hiển thị trang Home cho khách chưa đăng nhập)
    @GetMapping
    public ApiResponse<java.util.List<FoodResponseDTO>> getAllAvailableFoods() {
        java.util.List<FoodResponseDTO> list = foodService.getAllAvailableFoods()
                .stream()
                .map(FoodResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // Lấy chi tiết 1 món ăn (public - không cần đăng nhập)
    @GetMapping("/{id}")
    public ApiResponse<FoodResponseDTO> getFoodById(@PathVariable("id") Long foodId) {
        return ApiResponse.success(FoodResponseDTO.fromEntity(foodService.getFoodById(foodId)));
    }

    // =====================================================
    // PROTECTED ENDPOINTS - Yêu cầu đăng nhập (Chủ quán)
    // =====================================================

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

    // Thêm API upload ảnh món ăn
    @PostMapping("/{id}/upload-image")
    public ApiResponse<String> uploadFoodImage(
            @PathVariable("id") Long foodId,
            Authentication authentication,
            @RequestParam("foodFile") org.springframework.web.multipart.MultipartFile file) {
        try {
            // Lấy thông tin chủ quán từ Token
            User merchant = (User) authentication.getPrincipal();

            // Gọi service xử lý cập nhật ảnh
            String imageUrl = foodService.updateFoodImage(foodId, file, merchant);

            return ApiResponse.success(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Tải ảnh món ăn thất bại: " + e.getMessage());
        }
    }

    // Endpoint Sửa món ăn
    @PutMapping("/{id}")
    public ApiResponse<FoodResponseDTO> updateFood(
            @PathVariable("id") Long foodId,
            Authentication authentication,
            @RequestBody CreateFoodDTO dto) {

        User merchant = (User) authentication.getPrincipal();
        Food updatedFood = foodService.updateFood(foodId, dto, merchant);

        return ApiResponse.success(FoodResponseDTO.fromEntity(updatedFood));
    }

    // Endpoint Xóa món ăn (Ngưng bán)
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteFood(
            @PathVariable("id") Long foodId,
            Authentication authentication) {

        User merchant = (User) authentication.getPrincipal();
        foodService.deleteFood(foodId, merchant);

        return ApiResponse.success("Đã ngưng bán món ăn thành công!");
    }
}