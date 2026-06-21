package com.uit.fooddelivery_api.modules.voucher.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.uit.fooddelivery_api.modules.voucher.services.VoucherService;
import com.uit.fooddelivery_api.modules.user.entities.User;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherRepository voucherRepository;
    private final VoucherService voucherService;

    @GetMapping
    public ApiResponse<List<Voucher>> getActiveVouchers() {
        List<Voucher> activeVouchers = voucherRepository.findActiveVouchers(LocalDateTime.now());
        return ApiResponse.success(activeVouchers);
    }

    @PostMapping("/{id}/exchange")
    public ApiResponse<String> exchangeVoucher(@PathVariable("id") Long voucherId, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            String message = voucherService.exchangeVoucher(user, voucherId);
            return ApiResponse.success(message);
        } catch (Exception e) {
            return ApiResponse.error(400, e.getMessage());
        }
    }

    @GetMapping("/my-vouchers")
    public ApiResponse<List<Voucher>> getMyVouchers(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Voucher> vouchers = voucherService.getMyVouchers(user.getId());
        return ApiResponse.success(vouchers);
    }
}
