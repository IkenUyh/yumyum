package com.uit.fooddelivery_api.modules.food.repositories;

import com.uit.fooddelivery_api.modules.food.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c.id, c.name, c.imageUrl, COUNT(f) " +
           "FROM Category c " +
           "LEFT JOIN Food f ON f.category = c AND f.isAvailable = true " +
           "GROUP BY c.id, c.name, c.imageUrl")
    List<Object[]> findCategoriesWithFoodCount();
}