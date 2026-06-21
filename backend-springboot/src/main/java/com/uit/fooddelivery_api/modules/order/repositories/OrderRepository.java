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

    @org.springframework.data.jpa.repository.Query("SELECT COALESCE(SUM(o.totalAmount), 0) " +
            "FROM Order o " +
            "WHERE o.restaurant.id = :restaurantId AND o.status = 'COMPLETED' " +
            "AND o.createdAt >= :startOfDay AND o.createdAt <= :endOfDay")
    java.math.BigDecimal calculateRevenueByRestaurantAndDate(
            @org.springframework.data.repository.query.Param("restaurantId") Long restaurantId,
            @org.springframework.data.repository.query.Param("startOfDay") java.time.LocalDateTime startOfDay,
            @org.springframework.data.repository.query.Param("endOfDay") java.time.LocalDateTime endOfDay);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.restaurant.merchant.id = :merchantId AND o.status = 'COMPLETED'")
    Long countCompletedOrdersByMerchant(@org.springframework.data.repository.query.Param("merchantId") Long merchantId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.restaurant.id = :restaurantId AND o.status = 'COMPLETED' " +
            "AND o.createdAt >= :startOfDay AND o.createdAt <= :endOfDay")
    Long countCompletedOrdersByRestaurantAndDate(
            @org.springframework.data.repository.query.Param("restaurantId") Long restaurantId,
            @org.springframework.data.repository.query.Param("startOfDay") java.time.LocalDateTime startOfDay,
            @org.springframework.data.repository.query.Param("endOfDay") java.time.LocalDateTime endOfDay);

    // Tính tổng số lượng bán ra của từng món, xếp giảm dần
    @org.springframework.data.jpa.repository.Query("SELECT oi.food.id, oi.food.name, SUM(oi.quantity) " +
            "FROM OrderItem oi " +
            "WHERE oi.order.restaurant.merchant.id = :merchantId AND oi.order.status = 'COMPLETED' " +
            "GROUP BY oi.food.id, oi.food.name " +
            "ORDER BY SUM(oi.quantity) DESC")
    java.util.List<Object[]> getFoodSalesStatsByMerchant(@org.springframework.data.repository.query.Param("merchantId") Long merchantId);

    // Lấy các đơn hàng PENDING mà thời gian tạo đã vượt qua mốc thời gian quy định
    @org.springframework.data.jpa.repository.Query("SELECT o " +
            "FROM Order o JOIN FETCH o.user JOIN FETCH o.restaurant " +
            "WHERE o.status = 'PENDING' AND o.createdAt <= :cutoffTime")
    java.util.List<Order> findStalePendingOrders(@org.springframework.data.repository.query.Param("cutoffTime") java.time.LocalDateTime cutoffTime);

    // Lấy các đơn hàng UNPAID mà thời gian tạo đã vượt qua mốc thời gian quy định
    @org.springframework.data.jpa.repository.Query("SELECT o " +
            "FROM Order o JOIN FETCH o.user JOIN FETCH o.restaurant " +
            "WHERE o.status = 'UNPAID' AND o.createdAt <= :cutoffTime")
    java.util.List<Order> findStaleUnpaidOrders(@org.springframework.data.repository.query.Param("cutoffTime") java.time.LocalDateTime cutoffTime);

    // Lấy doanh thu theo từng ngày trong tháng cho một cửa hàng
    @org.springframework.data.jpa.repository.Query(
            "SELECT DAY(o.createdAt), COALESCE(SUM(o.totalAmount), 0), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.restaurant.id = :restaurantId AND o.status = 'COMPLETED' " +
            "AND MONTH(o.createdAt) = :month AND YEAR(o.createdAt) = :year " +
            "GROUP BY DAY(o.createdAt) " +
            "ORDER BY DAY(o.createdAt)")
    java.util.List<Object[]> getRevenueByDayInMonth(
            @org.springframework.data.repository.query.Param("restaurantId") Long restaurantId,
            @org.springframework.data.repository.query.Param("month") int month,
            @org.springframework.data.repository.query.Param("year") int year);

    // Đếm xem quán này đang có bao nhiêu đơn ở trạng thái CHỜ XỬ LÝ hoặc ĐANG NẤU
    @org.springframework.data.jpa.repository.Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.restaurant.id = :restaurantId " +
            "AND o.status IN ('PENDING', 'PREPARING')")
    Long countActiveOrdersByRestaurant(@org.springframework.data.repository.query.Param("restaurantId") Long restaurantId);
}