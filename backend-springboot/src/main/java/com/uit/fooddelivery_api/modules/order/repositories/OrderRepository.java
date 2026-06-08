package com.uit.fooddelivery_api.modules.order.repositories;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    java.util.List<Order> findByStatus(String status);

    // 1. Tìm tất cả đơn hàng của một Khách hàng (sắp xếp mới nhất lên đầu)
    java.util.List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 2. Tìm tất cả đơn hàng thuộc về các Nhà hàng của một Chủ quán
    java.util.List<Order> findByRestaurantMerchantIdOrderByCreatedAtDesc(Long merchantId);

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "WHERE o.restaurant.merchant.id = :merchantId AND o.status = 'COMPLETED'")
    java.math.BigDecimal calculateTotalRevenueByMerchant(@org.springframework.data.repository.query.Param("merchantId") Long merchantId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.restaurant.merchant.id = :merchantId AND o.status = 'COMPLETED'")
    Long countCompletedOrdersByMerchant(@org.springframework.data.repository.query.Param("merchantId") Long merchantId);

    // Tính tổng số lượng bán ra của từng món, xếp giảm dần
    @org.springframework.data.jpa.repository.Query("SELECT oi.food.id, oi.food.name, SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "WHERE oi.order.restaurant.merchant.id = :merchantId AND oi.order.status = 'COMPLETED' " +
            "GROUP BY oi.food.id, oi.food.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    java.util.List<Object[]> getFoodSalesStatsByMerchant(@org.springframework.data.repository.query.Param("merchantId") Long merchantId);

    // Lấy các đơn hàng PENDING mà thời gian tạo đã vượt qua mốc thời gian quy định
    @org.springframework.data.jpa.repository.Query("SELECT o " +
            "FROM Order o " +
            "WHERE o.status = 'PENDING' AND o.createdAt <= :cutoffTime")
    java.util.List<Order> findStalePendingOrders(@org.springframework.data.repository.query.Param("cutoffTime") java.time.LocalDateTime cutoffTime);
}