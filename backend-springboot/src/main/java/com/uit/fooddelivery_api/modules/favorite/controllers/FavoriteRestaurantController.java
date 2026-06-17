package com.uit.fooddelivery_api.modules.favorite.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.favorite.dtos.FavoriteRestaurantResponseDTO;
import com.uit.fooddelivery_api.modules.favorite.dtos.ToggleFavoriteResponseDTO;
import com.uit.fooddelivery_api.modules.favorite.services.FavoriteRestaurantService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteRestaurantController {

    private final FavoriteRestaurantService favoriteRestaurantService;

    /**
     * POST /api/v1/favorites/restaurants/{restaurantId}/toggle
     *
     * Bấm tim: Nếu chưa thích → thêm vào yêu thích.
     *          Nếu đã thích rồi → bỏ ra khỏi danh sách.
     * Response sẽ cho Android biết trạng thái mới để cập nhật icon tim.
     */
    @PostMapping("/restaurants/{restaurantId}/toggle")
    public ApiResponse<ToggleFavoriteResponseDTO> toggleFavorite(
            @PathVariable Long restaurantId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        ToggleFavoriteResponseDTO result = favoriteRestaurantService.toggleFavorite(restaurantId, user);
        return ApiResponse.success(result);
    }

    /**
     * GET /api/v1/favorites/restaurants
     *
     * Lấy danh sách tất cả nhà hàng yêu thích của user đang đăng nhập.
     */
    @GetMapping("/restaurants")
    public ApiResponse<List<FavoriteRestaurantResponseDTO>> getMyFavorites(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ApiResponse.success(favoriteRestaurantService.getMyFavorites(user));
    }

    /**
     * GET /api/v1/favorites/restaurants/{restaurantId}/status
     *
     * Kiểm tra xem user đã thích nhà hàng này chưa.
     * Dùng khi mở màn hình chi tiết quán để biết icon tim cần filled hay outline.
     */
    @GetMapping("/restaurants/{restaurantId}/status")
    public ApiResponse<Map<String, Object>> checkFavoriteStatus(
            @PathVariable Long restaurantId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        boolean favorited = favoriteRestaurantService.isFavorited(restaurantId, user);
        return ApiResponse.success(Map.of(
                "restaurantId", restaurantId,
                "isFavorited", favorited
        ));
    }
}
