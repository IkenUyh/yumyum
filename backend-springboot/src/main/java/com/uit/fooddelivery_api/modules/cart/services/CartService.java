package com.uit.fooddelivery_api.modules.cart.services;

import com.uit.fooddelivery_api.modules.cart.dtos.CartItemRequestDTO;
import com.uit.fooddelivery_api.modules.cart.entities.CartItem;
import com.uit.fooddelivery_api.modules.cart.repositories.CartItemRepository;
import com.uit.fooddelivery_api.modules.food.entities.Food;
import com.uit.fooddelivery_api.modules.food.repositories.FoodRepository;
import com.uit.fooddelivery_api.modules.user.entities.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final FoodRepository foodRepository;
    private final com.uit.fooddelivery_api.modules.food.repositories.FoodOptionItemRepository optionItemRepository;

    // 1. Lấy danh sách món trong giỏ
    public List<CartItem> getMyCart(User user) {
        return cartItemRepository.findByUserId(user.getId());
    }

    // 2. Thêm hoặc cập nhật món vào giỏ
    @Transactional
    public void addOrUpdateCartItem(CartItemRequestDTO dto, User user) {
        Food food = foodRepository.findById(dto.getFoodId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn!"));

        if (!food.getIsAvailable()) {
            throw new RuntimeException("Món ăn này hiện đang ngừng bán!");
        }

        // TỰ KHỞI TẠO OBJECT MAPPER Ở ĐÂY ĐỂ TRÁNH LỖI BEAN
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();

        // 1. Quét danh sách Topping khách chọn và đóng gói thành JSON
        String optionsJson = "[]";
        if (dto.getSelectedOptionItemIds() != null && !dto.getSelectedOptionItemIds().isEmpty()) {
            List<java.util.Map<String, Object>> optionsSnapshot = new java.util.ArrayList<>();

            for (Long optId : dto.getSelectedOptionItemIds()) {
                com.uit.fooddelivery_api.modules.food.entities.FoodOptionItem opt = optionItemRepository.findById(optId)
                        .orElseThrow(() -> new RuntimeException("Có Topping không tồn tại!"));

                // Lưu vết lại tên và giá
                java.util.Map<String, Object> map = new java.util.HashMap<>();
                map.put("name", opt.getName());
                map.put("price", opt.getAdditionalPrice());
                map.put("imageUrl", opt.getImageUrl());
                optionsSnapshot.add(map);
            }
            try {
                optionsJson = objectMapper.writeValueAsString(optionsSnapshot);
            } catch (Exception e) {
                throw new RuntimeException("Lỗi xử lý Topping!");
            }
        }

        // 2. Tìm xem có giỏ hàng nào TRÙNG MÓN và TRÙNG Y HỆT TOPPING không?
        List<CartItem> existingItems = cartItemRepository.findByUserId(user.getId());
        if (!existingItems.isEmpty()) {
            Long existingRestaurantId = existingItems.get(0).getFood().getRestaurant().getId();
            if (!existingRestaurantId.equals(food.getRestaurant().getId())) {
                cartItemRepository.deleteByUserId(user.getId());
                existingItems = new java.util.ArrayList<>();
            }
        }
        CartItem targetItem = null;

        for (CartItem item : existingItems) {
            if (item.getFood().getId().equals(food.getId())) {
                String existingOptions = item.getSelectedOptions() == null ? "[]" : item.getSelectedOptions();
                String existingPromotion = item.getAppliedPromotion() == null ? "NORMAL" : item.getAppliedPromotion();
                String newPromotion = dto.getAppliedPromotion() == null ? "NORMAL" : dto.getAppliedPromotion();

                if (existingOptions.equals(optionsJson) && existingPromotion.equalsIgnoreCase(newPromotion)) {
                    targetItem = item; // Trùng y hệt món, y hệt topping VÀ trùng y hệt promotion -> gộp vào!
                    break;
                }
            }
        }

        // 3. Xử lý lưu
        if (targetItem != null) {
            // Cộng dồn số lượng
            int newQuantity = targetItem.getQuantity() + dto.getQuantity();
            if (newQuantity <= 0) {
                cartItemRepository.delete(targetItem);
            } else {
                targetItem.setQuantity(newQuantity);
                cartItemRepository.save(targetItem);
            }
        } else {
            // Tạo giỏ mới (vì món này có topping khác biệt hoặc promotion khác biệt)
            if (dto.getQuantity() > 0) {
                CartItem newItem = CartItem.builder()
                        .user(user)
                        .food(food)
                        .quantity(dto.getQuantity())
                        .selectedOptions(optionsJson) // Lưu JSON vào DB
                        .appliedPromotion(dto.getAppliedPromotion() == null ? "NORMAL" : dto.getAppliedPromotion())
                        .build();
                cartItemRepository.save(newItem);
            }
        }
    }

    // 3. Xóa 1 món cụ thể khỏi giỏ
    @Transactional
    public void removeItem(Long cartItemId, User user) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mục này trong giỏ hàng!"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa món trong giỏ của người khác!");
        }

        cartItemRepository.delete(item);
    }

    // 4. Xóa sạch giỏ hàng (Gọi hàm này sau khi Order thành công)
    @Transactional
    public void clearCart(User user) {
        cartItemRepository.deleteByUserId(user.getId());
    }

    // 5. Lấy tổng số lượng các món trong giỏ hàng
    public Integer getCartItemCount(User user) {
        return cartItemRepository.countTotalQuantityByUserId(user.getId());
    }
}