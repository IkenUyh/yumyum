package com.uit.fooddelivery_api.modules.order.repositories;

import com.uit.fooddelivery_api.modules.order.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    java.util.List<Order> findByStatus(String status);
}