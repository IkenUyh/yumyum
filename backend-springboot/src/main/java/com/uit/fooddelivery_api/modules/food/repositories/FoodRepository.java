package com.uit.fooddelivery_api.modules.food.repositories;

import com.uit.fooddelivery_api.modules.food.entities.Food;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    @org.springframework.data.jpa.repository.Query("SELECT f FROM Food f JOIN FETCH f.restaurant")
    List<Food> findAllWithRestaurant();

    List<Food> findByRestaurantId(Long restaurantId);
}