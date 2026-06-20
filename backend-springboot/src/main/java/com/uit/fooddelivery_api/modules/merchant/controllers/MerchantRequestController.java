package com.uit.fooddelivery_api.modules.merchant.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.merchant.dtos.MerchantRequestResponseDTO;
import com.uit.fooddelivery_api.modules.merchant.dtos.SubmitRequestDTO;
import com.uit.fooddelivery_api.modules.merchant.entities.MerchantRequest;
import com.uit.fooddelivery_api.modules.merchant.services.MerchantRequestService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/merchant-requests")
@RequiredArgsConstructor
public class MerchantRequestController {

    private final MerchantRequestService requestService;

    // 1. API: Khách hàng gửi đơn xin mở quán (Nhận form-data có chứa ảnh)
    @PostMapping("/submit")
    public ApiResponse<MerchantRequestResponseDTO> submitRequest(
            Authentication authentication,
            @ModelAttribute SubmitRequestDTO dto,
            @RequestParam(value = "licenseFile", required = false) MultipartFile licenseFile) {
        try {
            User user = (User) authentication.getPrincipal();
            MerchantRequest req = requestService.submitRequest(dto, licenseFile, user);
            return ApiResponse.success(MerchantRequestResponseDTO.fromEntity(req));
        } catch (Exception e) {
            throw new RuntimeException("Lỗi gửi yêu cầu: " + e.getMessage());
        }
    }

    // 2. API: Admin xem danh sách theo trạng thái
    @GetMapping("")
    public ApiResponse<List<MerchantRequestResponseDTO>> getRequestsByStatus(
            @RequestParam(value = "status", required = false) String status) {
        List<MerchantRequestResponseDTO> list = requestService.getRequestsByStatus(status)
                .stream()
                .map(MerchantRequestResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }

    // 3. API: Admin Duyệt đơn (Sẽ tự động tạo Quán ăn)
    @PutMapping("/{id}/approve")
    public ApiResponse<MerchantRequestResponseDTO> approveRequest(@PathVariable("id") Long id) {
        MerchantRequest req = requestService.approveRequest(id);
        return ApiResponse.success(MerchantRequestResponseDTO.fromEntity(req));
    }

    // 4. API: Admin Từ chối đơn
    @PutMapping("/{id}/reject")
    public ApiResponse<MerchantRequestResponseDTO> rejectRequest(@PathVariable("id") Long id) {
        MerchantRequest req = requestService.rejectRequest(id);
        return ApiResponse.success(MerchantRequestResponseDTO.fromEntity(req));
    }
}