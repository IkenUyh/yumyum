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

    // API: Sửa nhóm option
    @PutMapping("/options/groups/{groupId}")
    public ApiResponse<OptionGroupResponseDTO> updateOptionGroup(
            @PathVariable("groupId") Long groupId,
            Authentication authentication,
            @RequestBody CreateOptionGroupDTO dto) {
        User merchant = (User) authentication.getPrincipal();
        FoodOptionGroup group = foodOptionService.updateOptionGroup(groupId, dto, merchant);
        return ApiResponse.success(OptionGroupResponseDTO.fromEntity(group));
    }

    // API: Thêm topping vào nhóm option
    @PostMapping("/options/groups/{groupId}/items")
    public ApiResponse<com.uit.fooddelivery_api.modules.food.dtos.OptionItemResponseDTO> addOptionItem(
            @PathVariable("groupId") Long groupId,
            Authentication authentication,
            @RequestBody com.uit.fooddelivery_api.modules.food.dtos.CreateOptionItemDTO dto) {
        User merchant = (User) authentication.getPrincipal();
        com.uit.fooddelivery_api.modules.food.entities.FoodOptionItem item = foodOptionService.addOptionItemToGroup(groupId, dto, merchant);
        return ApiResponse.success(com.uit.fooddelivery_api.modules.food.dtos.OptionItemResponseDTO.fromEntity(item));
    }

    // API: Cập nhật topping
    @PutMapping("/options/items/{itemId}")
    public ApiResponse<com.uit.fooddelivery_api.modules.food.dtos.OptionItemResponseDTO> updateOptionItem(
            @PathVariable("itemId") Long itemId,
            Authentication authentication,
            @RequestBody com.uit.fooddelivery_api.modules.food.dtos.CreateOptionItemDTO dto) {
        User merchant = (User) authentication.getPrincipal();
        com.uit.fooddelivery_api.modules.food.entities.FoodOptionItem item = foodOptionService.updateOptionItem(itemId, dto, merchant);
        return ApiResponse.success(com.uit.fooddelivery_api.modules.food.dtos.OptionItemResponseDTO.fromEntity(item));
    }

    // API: Xóa nhóm option
    @DeleteMapping("/options/groups/{groupId}")
    public ApiResponse<String> deleteOptionGroup(
            @PathVariable("groupId") Long groupId,
            Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        foodOptionService.deleteOptionGroup(groupId, merchant);
        return ApiResponse.success("Đã xóa nhóm option thành công!");
    }

    // API: Xóa topping
    @DeleteMapping("/options/items/{itemId}")
    public ApiResponse<String> deleteOptionItem(
            @PathVariable("itemId") Long itemId,
            Authentication authentication) {
        User merchant = (User) authentication.getPrincipal();
        foodOptionService.deleteOptionItem(itemId, merchant);
        return ApiResponse.success("Đã xóa topping thành công!");
    }
}