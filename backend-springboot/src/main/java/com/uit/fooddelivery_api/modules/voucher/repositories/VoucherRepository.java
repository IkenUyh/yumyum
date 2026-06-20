package com.uit.fooddelivery_api.modules.voucher.repositories;

import com.uit.fooddelivery_api.modules.voucher.entities.Voucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    // Chỉ lấy những Voucher đang được bật (is_active = true)
    Optional<Voucher> findByCodeAndIsActiveTrue(String code);

    // Quét các Voucher đang kích hoạt nhưng đã quá ngày kết thúc
    @org.springframework.data.jpa.repository.Query("SELECT v " +
            "FROM Voucher v " +
            "WHERE v.isActive = true AND v.endDate <= :now")
    java.util.List<Voucher> findExpiredActiveVouchers(@org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);

    // Lấy các Voucher đang còn hiệu lực và còn tồn kho
    @org.springframework.data.jpa.repository.Query("SELECT v " +
            "FROM Voucher v " +
            "WHERE v.isActive = true AND v.startDate <= :now AND v.endDate >= :now AND v.stockQuantity > 0")
    java.util.List<Voucher> findActiveVouchers(@org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);
}