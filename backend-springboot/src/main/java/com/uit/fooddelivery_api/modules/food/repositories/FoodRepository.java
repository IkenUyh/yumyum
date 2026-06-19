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

    List<Food> findByCategoryId(Long categoryId);

    List<Food> findByCategoryIdAndIsAvailableTrue(Long categoryId);

    @org.springframework.data.jpa.repository.Query(
        "SELECT DISTINCT f.category FROM Food f WHERE f.restaurant.id = :restaurantId AND f.category IS NOT NULL")
    List<com.uit.fooddelivery_api.modules.food.entities.Category> findDistinctCategoriesByRestaurantId(
        @org.springframework.data.repository.query.Param("restaurantId") Long restaurantId);
}