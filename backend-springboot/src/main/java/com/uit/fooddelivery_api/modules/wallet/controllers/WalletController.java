package com.uit.fooddelivery_api.modules.wallet.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.wallet.dtos.TopUpRequestDTO;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.services.WalletService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/topup")
    public ApiResponse<Wallet> topUp(
            Authentication authentication,
            @RequestBody TopUpRequestDTO dto) {

        // Lay thong tin User dang nhap tu Token gac cong
        User currentUser = (User) authentication.getPrincipal();

        // Goi service nap tien
        Wallet updatedWallet = walletService.topUp(dto, currentUser);

        return ApiResponse.success(updatedWallet);
    }
}