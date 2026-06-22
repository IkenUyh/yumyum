package com.uit.fooddelivery_api.modules.wallet.repositories;

import com.uit.fooddelivery_api.modules.wallet.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    // Tim vi bang ID cua User
    Optional<Wallet> findByUserId(Long userId);
}