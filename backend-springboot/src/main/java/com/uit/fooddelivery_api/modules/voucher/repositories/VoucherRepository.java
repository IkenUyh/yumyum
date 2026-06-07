package com.uit.fooddelivery_api.modules.voucher.repositories;

import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    // Chỉ lấy những Voucher đang được bật (is_active = true)
    Optional<Voucher> findByCodeAndIsActiveTrue(String code);
}