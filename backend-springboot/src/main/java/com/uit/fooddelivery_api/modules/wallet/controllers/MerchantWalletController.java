package com.uit.fooddelivery_api.modules.wallet.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.wallet.services.WalletService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/merchant/wallets")
@RequiredArgsConstructor
public class MerchantWalletController {

    private final RestaurantRepository restaurantRepository;
    private final WalletService walletService;
    private final com.uit.fooddelivery_api.modules.restaurant.repositories.RestaurantTransactionRepository restaurantTransactionRepository;

    // Lấy tổng số dư của TẤT CẢ nhà hàng thuộc về chủ quán này
    @GetMapping("/balance")
    public ApiResponse<Map<String, BigDecimal>> getBalance(Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        List<Restaurant> restaurants = restaurantRepository.findByMerchantId(merchant.getId());
        
        BigDecimal totalBalance = BigDecimal.ZERO;
        for (Restaurant r : restaurants) {
            if (r.getBalance() != null) {
                totalBalance = totalBalance.add(r.getBalance());
            }
        }

        Map<String, BigDecimal> response = new HashMap<>();
        response.put("balance", totalBalance);
        return ApiResponse.success(response);
    }

    // Rút tiền doanh thu
    @PostMapping("/withdraw")
    public ApiResponse<String> withdraw(@RequestBody WithdrawRequest request, Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền rút phải lớn hơn 0");
        }

        List<Restaurant> restaurants = restaurantRepository.findByMerchantId(merchant.getId());
        if (restaurants.isEmpty()) {
            throw new RuntimeException("Bạn không sở hữu cửa hàng nào!");
        }

        boolean success = false;
        for (Restaurant r : restaurants) {
            if (request.getRestaurantId() != null && !r.getId().equals(request.getRestaurantId())) {
                continue;
            }
            if (r.getBalance() != null && r.getBalance().compareTo(request.getAmount()) >= 0) {
                r.setBalance(r.getBalance().subtract(request.getAmount()));
                restaurantRepository.save(r);
                
                restaurantTransactionRepository.save(com.uit.fooddelivery_api.modules.restaurant.entities.RestaurantTransaction.builder()
                        .restaurant(r)
                        .amount(request.getAmount().negate())
                        .balanceAfter(r.getBalance())
                        .type("WITHDRAW")
                        .referenceId("WITHDRAW_" + System.currentTimeMillis())
                        .description("Rút tiền mặt doanh thu")
                        .build());
                        
                success = true;
                break;
            }
        }

        if (!success) {
            throw new RuntimeException("Số dư cửa hàng không đủ để rút tiền!");
        }

        return ApiResponse.success("Rút tiền thành công " + request.getAmount() + "đ");
    }

    // Chuyển tiền từ doanh thu cửa hàng sang ví cá nhân
    @PostMapping("/transfer")
    public ApiResponse<String> transferToPersonal(@RequestBody TransferRequest request, Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền chuyển phải lớn hơn 0");
        }

        List<Restaurant> restaurants = restaurantRepository.findByMerchantId(merchant.getId());
        if (restaurants.isEmpty()) {
            throw new RuntimeException("Bạn không sở hữu cửa hàng nào!");
        }

        boolean success = false;
        for (Restaurant r : restaurants) {
            if (request.getRestaurantId() != null && !r.getId().equals(request.getRestaurantId())) {
                continue;
            }
            if (r.getBalance() != null && r.getBalance().compareTo(request.getAmount()) >= 0) {
                // Trừ tiền cửa hàng
                r.setBalance(r.getBalance().subtract(request.getAmount()));
                restaurantRepository.save(r);
                
                restaurantTransactionRepository.save(com.uit.fooddelivery_api.modules.restaurant.entities.RestaurantTransaction.builder()
                        .restaurant(r)
                        .amount(request.getAmount().negate())
                        .balanceAfter(r.getBalance())
                        .type("TRANSFER_OUT")
                        .referenceId("TRANS_" + System.currentTimeMillis())
                        .description("Điều chuyển về ví cá nhân")
                        .build());
                
                // Cộng tiền vào ví cá nhân
                walletService.processTransaction(
                    merchant.getId(), 
                    request.getAmount(), 
                    "TRANSFER_FROM_MERCHANT", 
                    "TRANS_" + System.currentTimeMillis(), 
                    "Chuyển tiền từ doanh thu cửa hàng " + r.getName()
                );
                
                success = true;
                break;
            }
        }

        if (!success) {
            throw new RuntimeException("Số dư cửa hàng không đủ để thực hiện giao dịch!");
        }

        return ApiResponse.success("Chuyển tiền thành công " + request.getAmount() + "đ vào ví cá nhân");
    }

    // Lấy sao kê giao dịch của cửa hàng
    @GetMapping("/transactions")
    public ApiResponse<List<com.uit.fooddelivery_api.modules.wallet.dtos.TransactionResponseDTO>> getTransactions(Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        List<Restaurant> restaurants = restaurantRepository.findByMerchantId(merchant.getId());
        List<Long> restaurantIds = restaurants.stream().map(Restaurant::getId).collect(java.util.stream.Collectors.toList());
        
        if (restaurantIds.isEmpty()) {
            return ApiResponse.success(java.util.Collections.emptyList());
        }
        
        List<com.uit.fooddelivery_api.modules.restaurant.entities.RestaurantTransaction> txs = 
                restaurantTransactionRepository.findByRestaurantIdsOrderByCreatedAtDesc(restaurantIds);
                
        List<com.uit.fooddelivery_api.modules.wallet.dtos.TransactionResponseDTO> dtos = txs.stream().map(tx -> 
            com.uit.fooddelivery_api.modules.wallet.dtos.TransactionResponseDTO.builder()
                .id(tx.getId())
                .amount(tx.getAmount())
                .balanceAfter(tx.getBalanceAfter())
                .type(tx.getType())
                .referenceId(tx.getReferenceId())
                .description(tx.getDescription())
                .createdAt(tx.getCreatedAt())
                .build()
        ).collect(java.util.stream.Collectors.toList());
        
        return ApiResponse.success(dtos);
    }

    @Data
    public static class WithdrawRequest {
        private Long restaurantId;
        private BigDecimal amount;
    }

    @Data
    public static class TransferRequest {
        private Long restaurantId;
        private BigDecimal amount;
    }
}
