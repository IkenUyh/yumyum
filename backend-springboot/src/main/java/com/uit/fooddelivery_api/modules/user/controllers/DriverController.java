package com.uit.fooddelivery_api.modules.user.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.user.services.DriverLocationService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverLocationService driverLocationService;

    // App tài xế gọi API này ngầm mỗi 10 giây để báo cáo GPS
    @PutMapping("/location")
    public ApiResponse<String> updateLocation(
            Authentication authentication,
            @RequestParam double lat,
            @RequestParam double lng) {

        User driver = (User) authentication.getPrincipal();
        driverLocationService.updateDriverLocation(driver.getId(), lat, lng);

        return ApiResponse.success("Đã cập nhật tọa độ trên Redis!");
    }

    // API tắt app nghỉ chạy
    @DeleteMapping("/location")
    public ApiResponse<String> goOffline(Authentication authentication) {
        User driver = (User) authentication.getPrincipal();
        driverLocationService.removeDriverLocation(driver.getId());
        // Gọi thêm lệnh set Status trong MySQL về OFFLINE ở đây...
        return ApiResponse.success("Đã tắt nhận chuyến!");
    }
}