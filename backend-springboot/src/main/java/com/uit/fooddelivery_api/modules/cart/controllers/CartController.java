package com.uit.fooddelivery_api.modules.cart.controllers;

import com.uit.fooddelivery_api.common.responses.ApiResponse;
import com.uit.fooddelivery_api.modules.cart.dtos.CartItemRequestDTO;
import com.uit.fooddelivery_api.modules.cart.dtos.CartItemResponseDTO;
import com.uit.fooddelivery_api.modules.cart.services.CartService;
import com.uit.fooddelivery_api.modules.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleItemRepository flashSaleItemRepository;

    // Xem giỏ hàng
    @GetMapping
    public ApiResponse<List<CartItemResponseDTO>> getCart(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<CartItemResponseDTO> cartItems = cartService.getMyCart(currentUser)
                .stream()
                .map(item -> CartItemResponseDTO.fromEntity(item, flashSaleItemRepository))
                .toList();
        return ApiResponse.success(cartItems);
    }

    // Thêm món / Cập nhật số lượng (+ / -)
    @PostMapping
    public ApiResponse<String> addOrUpdateItem(
            Authentication authentication,
            @RequestBody CartItemRequestDTO dto) {

        User currentUser = (User) authentication.getPrincipal();
        cartService.addOrUpdateCartItem(dto, currentUser);
        return ApiResponse.success("Đã cập nhật giỏ hàng thành công!");
    }

    // Xóa một món hoàn toàn khỏi giỏ
    @DeleteMapping("/{itemId}")
    public ApiResponse<String> removeItem(
            @PathVariable("itemId") Long itemId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        cartService.removeItem(itemId, currentUser);
        return ApiResponse.success("Đã xóa món khỏi giỏ hàng!");
    }

    // Xóa sạch giỏ hàng
    @DeleteMapping("/clear")
    public ApiResponse<String> clearCart(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        cartService.clearCart(currentUser);
        return ApiResponse.success("Đã dọn sạch giỏ hàng!");
    }

    // Lấy tổng số lượng món trong giỏ hàng
    @GetMapping("/count")
    public ApiResponse<Integer> getCartCount(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Integer count = cartService.getCartItemCount(currentUser);
        return ApiResponse.success(count);
    }
}