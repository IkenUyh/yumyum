package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.food.dtos.CategoryFoodCountResponseDTO;
import com.uit.fooddelivery_api.modules.food.entities.Category;
import com.uit.fooddelivery_api.modules.food.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<CategoryFoodCountResponseDTO> getCategoryFoodCounts() {
        return categoryRepository.findCategoriesWithFoodCount().stream()
                .map(row -> CategoryFoodCountResponseDTO.builder()
                        .id((Long) row[0])
                        .name((String) row[1])
                        .imageUrl((String) row[2])
                        .foodCount((Long) row[3])
                        .build())
                .collect(Collectors.toList());
    }

    public Category createCategory(com.uit.fooddelivery_api.modules.food.dtos.CreateCategoryDTO dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .imageUrl(dto.getImageUrl())
                .build();
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, com.uit.fooddelivery_api.modules.food.dtos.CreateCategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục món ăn với id: " + id));
        category.setName(dto.getName());
        if (dto.getImageUrl() != null) {
            category.setImageUrl(dto.getImageUrl());
        }
        return categoryRepository.save(category);
    }
}

