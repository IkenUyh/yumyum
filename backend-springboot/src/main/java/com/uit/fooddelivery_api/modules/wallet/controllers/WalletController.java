package com.uit.fooddelivery_api.modules.wallet.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.wallet.dtos.TransactionResponseDTO;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.services.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    // Lấy số dư ví hiện tại
    @GetMapping("/balance")
    public ApiResponse<Map<String, BigDecimal>> getBalance(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Wallet wallet = walletService.getMyWallet(currentUser);

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("balance", wallet.getBalance());
        return ApiResponse.success(response);
    }

    // Lấy lịch sử biến động số dư (Sao kê)
    @GetMapping("/transactions")
    public ApiResponse<List<TransactionResponseDTO>> getTransactions(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<TransactionResponseDTO> history = walletService.getTransactionHistory(currentUser).stream()
                .map(TransactionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success(history);
    }

    // Nạp tiền vào ví
    @org.springframework.web.bind.annotation.PostMapping("/topup")
    public ApiResponse<String> topUp(Authentication authentication, @org.springframework.web.bind.annotation.RequestBody com.uit.fooddelivery_api.modules.wallet.dtos.TopUpRequestDTO dto) {
        User currentUser = (User) authentication.getPrincipal();
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền nạp phải lớn hơn 0");
        }
        walletService.processTransaction(currentUser.getId(), dto.getAmount(), "TOPUP", "TOPUP_" + System.currentTimeMillis(), "Nạp tiền vào ví (giả lập)");
        return ApiResponse.success("Nạp tiền thành công " + dto.getAmount() + "đ");
    }
}