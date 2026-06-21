package com.uit.fooddelivery_api.modules.food.services;

import com.uit.fooddelivery_api.modules.food.dtos.CreateOptionGroupDTO;
import com.uit.fooddelivery_api.modules.food.dtos.CreateOptionItemDTO;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.entities.FoodOptionGroup;
import com.uit.fooddelivery_api.modules.food.entities.FoodOptionItem;
import com.uit.fooddelivery_api.modules.food.repositories.FoodOptionGroupRepository;
import com.uit.fooddelivery_api.modules.food.repositories.FoodOptionItemRepository;
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
    private final FoodOptionItemRepository itemRepository;

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
                        .imageUrl(itemDto.getImageUrl())
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

    // 3. Sửa nhóm Topping (OptionGroup)
    @Transactional
    public FoodOptionGroup updateOptionGroup(Long groupId, CreateOptionGroupDTO dto, User merchant) {
        FoodOptionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm option!"));

        // Bảo mật
        if (!group.getFood().getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa quán khác!");
        }

        if (dto.getName() != null) group.setName(dto.getName());
        if (dto.getIsRequired() != null) group.setIsRequired(dto.getIsRequired());
        if (dto.getMaxChoices() != null) group.setMaxChoices(dto.getMaxChoices());

        return groupRepository.save(group);
    }

    // 4. Thêm option item vào nhóm
    @Transactional
    public FoodOptionItem addOptionItemToGroup(Long groupId, CreateOptionItemDTO dto, User merchant) {
        FoodOptionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm option!"));

        // Bảo mật
        if (!group.getFood().getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa quán khác!");
        }

        FoodOptionItem item = FoodOptionItem.builder()
                .group(group)
                .name(dto.getName())
                .additionalPrice(dto.getAdditionalPrice())
                .imageUrl(dto.getImageUrl())
                .isAvailable(true)
                .build();

        return itemRepository.save(item);
    }

    // 5. Cập nhật option item (Topping)
    @Transactional
    public FoodOptionItem updateOptionItem(Long itemId, CreateOptionItemDTO dto, User merchant) {
        FoodOptionItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy option item!"));

        // Bảo mật
        if (!item.getGroup().getFood().getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa quán khác!");
        }

        if (dto.getName() != null) item.setName(dto.getName());
        if (dto.getAdditionalPrice() != null) item.setAdditionalPrice(dto.getAdditionalPrice());
        if (dto.getImageUrl() != null) item.setImageUrl(dto.getImageUrl());

        return itemRepository.save(item);
    }

    // 6. Xóa nhóm option
    @Transactional
    public void deleteOptionGroup(Long groupId, User merchant) {
        FoodOptionGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhóm option!"));

        // Bảo mật
        if (!group.getFood().getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa quán khác!");
        }

        groupRepository.delete(group);
    }

    // 7. Xóa option item
    @Transactional
    public void deleteOptionItem(Long itemId, User merchant) {
        FoodOptionItem item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy option item!"));

        // Bảo mật
        if (!item.getGroup().getFood().getRestaurant().getMerchant().getId().equals(merchant.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa quán khác!");
        }

        itemRepository.delete(item);
    }
}