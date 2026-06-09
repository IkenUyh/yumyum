package com.uit.fooddelivery_api.modules.food.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.food.dtos.CreateOptionGroupDTO;
import com.uit.fooddelivery_api.modules.food.dtos.OptionGroupResponseDTO;
import com.uit.fooddelivery_api.modules.food.entities.FoodOptionGroup;
import com.uit.fooddelivery_api.modules.food.services.FoodOptionService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@RequiredArgsConstructor
public class FoodOptionController {

    private final FoodOptionService foodOptionService;

    // API: Chủ quán thêm nhóm Topping cho món ăn
    @PostMapping("/{foodId}/options")
    public ApiResponse<OptionGroupResponseDTO> addOptionGroup(
            @PathVariable("foodId") Long foodId,
            Authentication authentication,
            @RequestBody CreateOptionGroupDTO dto) {

        User merchant = (User) authentication.getPrincipal();
        FoodOptionGroup group = foodOptionService.addOptionGroupToFood(foodId, dto, merchant);

        return ApiResponse.success(OptionGroupResponseDTO.fromEntity(group));
    }

    // API: Khách hàng xem danh sách Topping của một món
    @GetMapping("/{foodId}/options")
    public ApiResponse<List<OptionGroupResponseDTO>> getFoodOptions(@PathVariable("foodId") Long foodId) {
        List<OptionGroupResponseDTO> list = foodOptionService.getFoodOptions(foodId)
                .stream()
                .map(OptionGroupResponseDTO::fromEntity)
                .toList();
        return ApiResponse.success(list);
    }
}