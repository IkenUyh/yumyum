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
}