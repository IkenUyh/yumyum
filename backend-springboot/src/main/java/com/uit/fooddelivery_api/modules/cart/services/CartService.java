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

        Optional<CartItem> existingItemOpt = cartItemRepository.findByUserIdAndFoodId(user.getId(), food.getId());

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + dto.getQuantity();

            if (newQuantity <= 0) {
                cartItemRepository.delete(existingItem);
            } else {
                existingItem.setQuantity(newQuantity);
                cartItemRepository.save(existingItem);
            }
        } else {
            if (dto.getQuantity() > 0) {
                CartItem newItem = CartItem.builder()
                        .user(user)
                        .food(food)
                        .quantity(dto.getQuantity())
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
}