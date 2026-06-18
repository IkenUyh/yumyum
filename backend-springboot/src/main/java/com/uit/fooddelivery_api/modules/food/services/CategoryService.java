package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.food.dtos.CategoryFoodCountResponseDTO;
import com.uit.fooddelivery_api.modules.food.entities.Category;
import com.uit.fooddelivery_api.modules.food.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<CategoryFoodCountResponseDTO> getCategoryFoodCounts() {
        List<Object[]> results = categoryRepository.findCategoriesWithFoodCount();
        return results.stream()
                .map(row -> CategoryFoodCountResponseDTO.builder()
                        .id((Long) row[0])
                        .name((String) row[1])
                        .imageUrl((String) row[2])
                        .foodCount((Long) row[3])
                        .build())
                .toList();
    }
}
