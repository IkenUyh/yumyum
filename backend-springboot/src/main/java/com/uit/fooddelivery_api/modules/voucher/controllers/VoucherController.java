package com.uit.fooddelivery_api.modules.voucher.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import com.uit.fooddelivery_api.modules.voucher.repositories.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherRepository voucherRepository;

    @GetMapping
    public ApiResponse<List<Voucher>> getActiveVouchers() {
        List<Voucher> activeVouchers = voucherRepository.findActiveVouchers(LocalDateTime.now());
        return ApiResponse.success(activeVouchers);
    }
}
