package com.uit.fooddelivery_api.modules.wallet.services;

import com.uit.fooddelivery_api.modules.wallet.dtos.TopUpRequestDTO;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional
    public Wallet topUp(TopUpRequestDTO dto, User user) {
        if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("So tien nap phai lon hon 0!");
        }

        // Tim vi cua chinh user dang dang nhap
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Khong tim thay vi cua nguoi dung!"));

        // Tien hanh cong don tien vao balance
        BigDecimal newBalance = wallet.getBalance().add(dto.getAmount());
        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
    }
}