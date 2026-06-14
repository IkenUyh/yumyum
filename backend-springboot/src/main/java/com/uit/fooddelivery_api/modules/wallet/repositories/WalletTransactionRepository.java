package com.uit.fooddelivery_api.modules.wallet.repositories;

import com.uit.fooddelivery_api.modules.wallet.entities.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    // Lấy sao kê giao dịch của một ví, xếp từ mới nhất đến cũ nhất
    List<WalletTransaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}