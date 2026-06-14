package com.uit.fooddelivery_api.modules.restaurant.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.restaurant.dtos.RestaurantDistanceView;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class RestaurantSearchController {

    private final RestaurantRepository restaurantRepository;

    // API: Khách hàng truyền Tọa độ GPS hiện tại của điện thoại lên để tìm quán
    // Ví dụ: GET /api/v1/search/nearby?lat=10.7952&lng=106.7218&radius=5
    @GetMapping("/nearby")
    public ApiResponse<List<RestaurantDistanceView>> getNearbyRestaurants(
            @RequestParam("lat") double latitude,
            @RequestParam("lng") double longitude,
            @RequestParam(value = "radius", defaultValue = "5.0") double radiusKm) {

        // Cực kỳ nhanh vì mọi phép toán lượng giác và sắp xếp đều do Engine của MySQL xử lý dưới ổ cứng/RAM
        List<RestaurantDistanceView> nearbyRestaurants = restaurantRepository.findNearbyRestaurants(latitude, longitude, radiusKm);

        return ApiResponse.success(nearbyRestaurants);
    }

    // API: Khách hàng gõ tìm kiếm (VD gõ sai: "cơm tắn", "trà sủa")
    @GetMapping("/keyword")
    public ApiResponse<java.util.List<RestaurantDistanceView>> searchByKeyword(@RequestParam("q") String keyword) {

        // Cắt bỏ khoảng trắng thừa để Ngram Parser làm việc chính xác nhất
        String cleanKeyword = keyword.trim();

        if (cleanKeyword.isEmpty()) {
            return ApiResponse.success(java.util.Collections.emptyList());
        }

        List<RestaurantDistanceView> results = restaurantRepository.searchRestaurantsByKeyword(cleanKeyword);
        return ApiResponse.success(results);
    }
}