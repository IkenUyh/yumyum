package com.uit.fooddelivery_api.modules.restaurant.repositories;

import com.uit.fooddelivery_api.modules.restaurant.entities.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}