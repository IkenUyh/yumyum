package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.food.dtos.CreateOptionGroupDTO;
import com.uit.fooddelivery_api.modules.food.dtos.CreateOptionItemDTO;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.entities.FoodOptionGroup;
import com.uit.fooddelivery_api.modules.food.entities.FoodOptionItem;
import com.uit.fooddelivery_api.modules.food.repositories.FoodOptionGroupRepository;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodOptionService {

    private final FoodOptionGroupRepository groupRepository;
    private final FoodRepository foodRepository;

    // 1. Chủ quán thêm Topping cho món
    @Transactional
    public FoodOptionGroup addOptionGroupToFood(Long foodId, CreateOptionGroupDTO dto, User merchant) {
        Food food = foodRepository.findById(foodId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn!"));

        // Bảo mật: Phải là chủ của cái nhà hàng bán món này mới được sửa
        if (!food.getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa món ăn của quán khác!");
        }

        FoodOptionGroup group = FoodOptionGroup.builder()
                .food(food)
                .name(dto.getName())
                .isRequired(dto.getIsRequired())
                .maxChoices(dto.getMaxChoices())
                .build();

        List<FoodOptionItem> items = new ArrayList<>();
        if (dto.getItems() != null) {
            for (CreateOptionItemDTO itemDto : dto.getItems()) {
                FoodOptionItem item = FoodOptionItem.builder()
                        .group(group)
                        .name(itemDto.getName())
                        .additionalPrice(itemDto.getAdditionalPrice())
                        .isAvailable(true)
                        .build();
                items.add(item);
            }
        }

        // Liên kết 2 chiều
        group.setOptionItems(items);

        return groupRepository.save(group);
    }

    // 2. Lấy danh sách Topping của 1 món để Khách hàng xem
    public List<FoodOptionGroup> getFoodOptions(Long foodId) {
        return groupRepository.findByFoodId(foodId);
    }
}