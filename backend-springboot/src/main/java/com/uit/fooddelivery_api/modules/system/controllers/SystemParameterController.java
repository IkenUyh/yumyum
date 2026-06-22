package com.uit.fooddelivery_api.modules.system.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.system.entities.SystemParameter;
import com.uit.fooddelivery_api.modules.system.repositories.SystemParameterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system-parameters")
@RequiredArgsConstructor
public class SystemParameterController {

    private final SystemParameterRepository systemParameterRepository;

    // Lấy toàn bộ danh sách cấu hình hệ thống
    @GetMapping
    public ApiResponse<List<SystemParameter>> getAllParameters() {
        return ApiResponse.success(systemParameterRepository.findAll());
    }

    // Cập nhật một cấu hình bất kỳ (Ví dụ đổi điểm thưởng từ 100 Xu lên 200 Xu)
    // Cần phân quyền @PreAuthorize("hasRole('ADMIN')") nếu dự án có Role Admin
    @PutMapping("/{paramKey}")
    public ApiResponse<SystemParameter> updateParameter(
            @PathVariable String paramKey,
            @RequestParam String newValue) {

        SystemParameter param = systemParameterRepository.findByParamKey(paramKey)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tham số cấu hình này!"));

        param.setParamValue(newValue);
        SystemParameter updatedParam = systemParameterRepository.save(param);

        return ApiResponse.success(updatedParam);
    }
}