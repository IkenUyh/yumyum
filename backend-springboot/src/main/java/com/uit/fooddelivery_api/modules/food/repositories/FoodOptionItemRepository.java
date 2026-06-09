package com.uit.fooddelivery_api.modules.food.repositories;

import com.uit.fooddelivery_api.modules.food.entities.FoodOptionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodOptionItemRepository extends JpaRepository<FoodOptionItem, Long> {
}