package com.uit.fooddelivery_api.modules.cart.repositories;

import com.uit.fooddelivery_api.modules.cart.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Lấy toàn bộ giỏ hàng của user
    List<CartItem> findByUserId(Long userId);

    // Tìm xem món này đã có trong giỏ của user chưa (để cộng dồn số lượng)
    Optional<CartItem> findByUserIdAndFoodId(Long userId, Long foodId);

    // Xóa toàn bộ giỏ hàng sau khi đặt đơn thành công
    void deleteByUserId(Long userId);

    // Tính tổng số lượng tất cả các món trong giỏ hàng của user
    @Query("SELECT COALESCE(SUM(c.quantity), 0) FROM CartItem c WHERE c.user.id = :userId")
    Integer countTotalQuantityByUserId(@Param("userId") Long userId);
}