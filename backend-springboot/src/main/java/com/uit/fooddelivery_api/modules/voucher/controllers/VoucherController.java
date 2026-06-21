package com.uit.fooddelivery_api.modules.voucher.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository;
import com.uit.fooddelivery_api.modules.voucher.dtos.VoucherExchangeRequest;
import com.uit.fooddelivery_api.modules.voucher.services.VoucherService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherRepository voucherRepository;
    private final VoucherService voucherService;

    @GetMapping
    public ApiResponse<List<Voucher>> getActiveVouchers(Authentication authentication) {
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId();
        }
        List<Voucher> activeVouchers = voucherRepository.findActiveVouchersForUser(LocalDateTime.now(), userId);
        return ApiResponse.success(activeVouchers);
    }

    @PostMapping("/exchange")
    public ApiResponse<Voucher> exchangeCoinsForVoucher(
            @RequestBody VoucherExchangeRequest request,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Voucher voucher = voucherService.exchangeCoinsForVoucher(currentUser, request);
        return ApiResponse.success(voucher);
    }
}
