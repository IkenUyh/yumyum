package com.uit.fooddelivery_api.modules.wallet.services;

import com.uit.fooddelivery_api.modules.user.entities.User;
import com.uit.fooddelivery_api.modules.user.repositories.UserRepository;
import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import com.uit.fooddelivery_api.modules.wallet.entities.WalletTransaction;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletRepository;
import com.uit.fooddelivery_api.modules.wallet.repositories.WalletTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final UserRepository userRepository;

    // Lấy thông tin ví
    public Wallet getMyWallet(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wallet newWallet = Wallet.builder()
                            .user(user)
                            .balance(BigDecimal.ZERO)
                            .build();
                    return walletRepository.save(newWallet);
                });
    }

    // Lấy sao kê
    public List<WalletTransaction> getTransactionHistory(User user) {
        Wallet wallet = getMyWallet(user);
        return transactionRepository.findByWalletIdOrderByCreatedAtDesc(wallet.getId());
    }

    // Hàm chuẩn để biến động số dư (Nạp, Trừ, Chuyển)
    @Transactional
    public Wallet processTransaction(Long userId, BigDecimal amount, String type, String referenceId, String description) {
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng để khởi tạo ví!"));
                    Wallet newWallet = Wallet.builder()
                            .user(user)
                            .balance(BigDecimal.ZERO)
                            .build();
                    return walletRepository.save(newWallet);
                });

        // Kiểm tra số dư nếu là lệnh trừ tiền
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            if (wallet.getBalance().compareTo(amount.abs()) < 0) {
                throw new RuntimeException("Số dư trong ví không đủ để thực hiện giao dịch!");
            }
        }

        // Cập nhật số dư
        BigDecimal newBalance = wallet.getBalance().add(amount);
        wallet.setBalance(newBalance);
        Wallet savedWallet = walletRepository.save(wallet);

        // Lưu vào sổ cái (Ledger)
        WalletTransaction tx = WalletTransaction.builder()
                .wallet(savedWallet)
                .amount(amount)
                .balanceAfter(newBalance)
                .type(type)
                .referenceId(referenceId)
                .description(description)
                .build();

        transactionRepository.save(tx);

        return savedWallet;
    }
}