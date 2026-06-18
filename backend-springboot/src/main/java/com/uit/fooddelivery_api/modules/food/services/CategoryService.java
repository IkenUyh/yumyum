package com.uit.fooddelivery_api.modules.food.services;

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
}
