package com.uit.fooddelivery_api.modules.food.repositories;

import com.uit.fooddelivery_api.modules.food.entities.FoodOptionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodOptionGroupRepository extends JpaRepository<FoodOptionGroup, Long> {
    List<FoodOptionGroup> findByFoodId(Long foodId);
}