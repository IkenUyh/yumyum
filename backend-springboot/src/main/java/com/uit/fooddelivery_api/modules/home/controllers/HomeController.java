package com.uit.fooddelivery_api.modules.home.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.home.dtos.*;
import com.uit.fooddelivery_api.modules.home.services.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/core")
    public ApiResponse<HomeCoreResponseDTO> getHomeCore(@RequestParam(value = "addressId", required = false) String addressId) {
        return ApiResponse.success(homeService.getHomeCore(addressId));
    }

    @GetMapping("/brands")
    public ApiResponse<BrandResponseDTO> getPopularBrands(@RequestParam(value = "addressId", required = false) String addressId) {
        return ApiResponse.success(homeService.getPopularBrands(addressId));
    }

    @GetMapping("/deals")
    public ApiResponse<DealResponseDTO> getRecommendedDeals(
            @RequestParam(value = "addressId", required = false) String addressId,
            @RequestParam(value = "tabId", defaultValue = "0") int tabId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "lat", required = false) Double lat,
            @RequestParam(value = "lng", required = false) Double lng) {
        return ApiResponse.success(homeService.getRecommendedDeals(addressId, tabId, page, size, lat, lng));
    }
}
