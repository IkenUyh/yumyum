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

    // =====================================================
    // PUBLIC ENDPOINTS - Không cần đăng nhập
    // =====================================================

    // 1. Lấy tất cả nhà hàng cho khách hàng xem (trang Home)
    @GetMapping
    // @org.springframework.cache.annotation.Cacheable(value = "restaurantsList")
    public ApiResponse<java.util.List<RestaurantResponseDTO>> getAllRestaurants() {
        System.out.println("Đang truy vấn Database MySQL để lấy danh sách quán ăn...");

        java.util.List<RestaurantResponseDTO> list = restaurantService.getAllRestaurants()
                .stream()
                .map(RestaurantResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // 2. Lấy chi tiết 1 nhà hàng theo ID (public - không cần đăng nhập)
    @GetMapping("/{id}")
    public ApiResponse<RestaurantResponseDTO> getRestaurantById(@PathVariable("id") Long restaurantId) {
        return ApiResponse.success(RestaurantResponseDTO.fromEntity(restaurantService.getRestaurantById(restaurantId)));
    }

    // 3. Lấy thực đơn (Menu) của một nhà hàng (public - không cần đăng nhập)
    @GetMapping("/{id}/foods")
    public ApiResponse<java.util.List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO>> getRestaurantMenu(
            @PathVariable("id") Long restaurantId) {

        java.util.List<com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO> menu = foodService.getFoodsByRestaurant(restaurantId)
                .stream()
                .map(com.uit.fooddelivery_api.modules.food.dtos.FoodResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(menu);
    }

    // 4. Lấy khoảng cách từ vị trí hiện tại đến nhà hàng
    @GetMapping("/{id}/distance")
    public ApiResponse<Double> getRestaurantDistance(
            @PathVariable("id") Long restaurantId,
            @RequestParam("lat") double userLat,
            @RequestParam("lng") double userLng) {

        Restaurant restaurant = restaurantService.getRestaurantById(restaurantId);
        if (restaurant.getLatitude() == null || restaurant.getLongitude() == null) {
            return ApiResponse.success(-1.0); // Không có toạ độ
        }

        double distanceKm = com.uit.fooddelivery_api.common.utils.DistanceUtil.calculateDistance(
                restaurant.getLatitude().doubleValue(),
                restaurant.getLongitude().doubleValue(),
                userLat,
                userLng
        );
        
        // Làm tròn 1 chữ số thập phân
        distanceKm = Math.round(distanceKm * 10.0) / 10.0;

        return ApiResponse.success(distanceKm);
    }

    // =====================================================
    // PROTECTED ENDPOINTS - Yêu cầu đăng nhập (Chủ quán)
    // =====================================================

    // Tạo nhà hàng mới (chủ quán)
    @PostMapping
    // @org.springframework.cache.annotation.CacheEvict(value = "restaurantsList", allEntries = true)
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

    // Cập nhật cài đặt nhà hàng (chủ quán)
    @PutMapping("/{id}/settings")
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

        restaurantService.save(restaurant);

        return ApiResponse.success(RestaurantResponseDTO.fromEntity(restaurant));
    }

    // Cập nhật thông tin quán: tên, địa chỉ, giờ mở/đóng cửa (chủ quán)
    @PutMapping("/{id}/info")
    public ApiResponse<RestaurantResponseDTO> updateRestaurantInfo(
            @PathVariable("id") Long restaurantId,
            Authentication authentication,
            @RequestBody com.uit.fooddelivery_api.modules.restaurant.dtos.UpdateRestaurantInfoDTO dto) {

        User merchant = (User) authentication.getPrincipal();
        Restaurant restaurant = restaurantService.getAllRestaurants().stream()
                .filter(r -> r.getId().equals(restaurantId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quán!"));

        if (!restaurant.getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa quán này!");
        }

        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            restaurant.setName(dto.getName().trim());
        }
        if (dto.getAddress() != null && !dto.getAddress().trim().isEmpty()) {
            restaurant.setAddress(dto.getAddress().trim());
        }
        if (dto.getOpenTime() != null) {
            restaurant.setOpenTime(java.time.LocalTime.parse(dto.getOpenTime()));
        }
        if (dto.getCloseTime() != null) {
            restaurant.setCloseTime(java.time.LocalTime.parse(dto.getCloseTime()));
        }
        if (dto.getImageUrl() != null) {
            restaurant.setImageUrl(dto.getImageUrl());
        }

        restaurantService.save(restaurant);

        return ApiResponse.success(RestaurantResponseDTO.fromEntity(restaurant));
    }

    // Upload ảnh nhà hàng (chủ quán)
    @PostMapping(value = "/{id}/upload-image", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> uploadRestaurantImage(
            @PathVariable("id") Long restaurantId,
            Authentication authentication,
            @RequestParam("restaurantFile") org.springframework.web.multipart.MultipartFile file) {
        try {
            User merchant = (User) authentication.getPrincipal();
            String imageUrl = restaurantService.updateRestaurantImage(restaurantId, file, merchant);
            return ApiResponse.success(imageUrl);
        } catch (Exception e) {
            throw new RuntimeException("Tải ảnh nhà hàng thất bại: " + e.getMessage());
        }
    }
}