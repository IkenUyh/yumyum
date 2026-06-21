package com.uit.fooddelivery_api.modules.restaurant.repositories;

import com.uit.fooddelivery_api.modules.restaurant.entities.RestaurantTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantTransactionRepository extends JpaRepository<RestaurantTransaction, Long> {

    @Query("SELECT rt FROM RestaurantTransaction rt WHERE rt.restaurant.id IN :restaurantIds ORDER BY rt.createdAt DESC")
    List<RestaurantTransaction> findByRestaurantIdsOrderByCreatedAtDesc(@Param("restaurantIds") List<Long> restaurantIds);
    
}
