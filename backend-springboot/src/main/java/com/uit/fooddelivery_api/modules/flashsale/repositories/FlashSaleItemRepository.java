package com.uit.fooddelivery_api.modules.flashsale.repositories;

import com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    // Tìm món ăn xem CÓ ĐANG NẰM TRONG 1 FLASHSALE NÀO HOẠT ĐỘNG KHÔNG?
    @org.springframework.data.jpa.repository.Query("SELECT fsi FROM FlashSaleItem fsi JOIN fsi.flashSale fs " +
            "WHERE fsi.food.id = :foodId AND fs.isActive = true AND :now BETWEEN fs.startTime AND fs.endTime")
    Optional<FlashSaleItem> findActiveFlashSaleItemByFoodId(
            @org.springframework.data.repository.query.Param("foodId") Long foodId,
            @org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);

    @org.springframework.data.jpa.repository.Query("SELECT fsi FROM FlashSaleItem fsi JOIN FETCH fsi.food f JOIN FETCH f.restaurant JOIN fsi.flashSale fs " +
            "WHERE fs.isActive = true AND :now BETWEEN fs.startTime AND fs.endTime")
    List<FlashSaleItem> findActiveFlashSaleItems(
            @org.springframework.data.repository.query.Param("now") java.time.LocalDateTime now);
}