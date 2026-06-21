package com.uit.fooddelivery_api.modules.flashsale.repositories;

import com.uit.fooddelivery_api.modules.flashsale.entities.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    // Tìm tất cả Flashsale đang hoạt động tại thời điểm hiện tại
    @Query("SELECT fs FROM FlashSale fs WHERE fs.isActive = true AND :now BETWEEN fs.startTime AND fs.endTime")
    List<FlashSale> findActiveFlashSales(@Param("now") LocalDateTime now);

    // Vô hiệu hóa tất cả Flashsale đã hết hạn (endTime < now) mà vẫn isActive = true
    @Modifying
    @Query("UPDATE FlashSale fs SET fs.isActive = false WHERE fs.isActive = true AND fs.endTime < :now")
    int deactivateExpiredFlashSales(@Param("now") LocalDateTime now);

    // Tìm Flashsale được tạo tự động (tên bắt đầu bằng "[AUTO]")
    @Query("SELECT fs FROM FlashSale fs WHERE fs.name LIKE '[AUTO]%' AND fs.isActive = true AND :now BETWEEN fs.startTime AND fs.endTime")
    List<FlashSale> findActiveAutoFlashSales(@Param("now") LocalDateTime now);
}
