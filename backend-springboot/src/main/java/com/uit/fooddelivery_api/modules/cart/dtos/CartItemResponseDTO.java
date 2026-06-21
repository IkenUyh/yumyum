package com.uit.fooddelivery_api.modules.cart.dtos;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uit.fooddelivery_api.modules.cart.entities.CartItem;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
public class CartItemResponseDTO {
    private Long id;
    private Long foodId;
    private String foodName;
    private String foodImageUrl;
    private BigDecimal basePrice;
    private Integer quantity;
    private Long restaurantId;
    private String restaurantName;
    private List<Map<String, Object>> selectedOptions; // Trả về list topping cho frontend dễ vẽ UI
    private BigDecimal itemTotal;

    public static CartItemResponseDTO fromEntity(CartItem cartItem) {
        return fromEntity(cartItem, null);
    }

    public static CartItemResponseDTO fromEntity(CartItem cartItem, com.uit.fooddelivery_api.modules.flashsale.repositories.FlashSaleItemRepository flashSaleItemRepository) {
        BigDecimal basePrice = cartItem.getFood().getPrice();
        boolean hasFlashSale = false;
        if (flashSaleItemRepository != null) {
            java.util.Optional<com.uit.fooddelivery_api.modules.flashsale.entities.FlashSaleItem> flashSaleOpt =
                    flashSaleItemRepository.findActiveFlashSaleItemByFoodId(cartItem.getFood().getId(), java.time.LocalDateTime.now());
            if (flashSaleOpt.isPresent()) {
                basePrice = flashSaleOpt.get().getSalePrice();
                hasFlashSale = true;
            }
        }
        if (!hasFlashSale) {
            basePrice = basePrice.multiply(BigDecimal.valueOf(0.8));
        }
        Integer qty = cartItem.getQuantity();
        BigDecimal optionsTotal = BigDecimal.ZERO;
        List<Map<String, Object>> parsedOptions = null;

        // Đọc chuỗi JSON topping từ Database lên
        if (cartItem.getSelectedOptions() != null && !cartItem.getSelectedOptions().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                parsedOptions = mapper.readValue(cartItem.getSelectedOptions(),
                        new TypeReference<List<Map<String, Object>>>() {
                        });

                // Cộng tiền topping vào
                for (Map<String, Object> opt : parsedOptions) {
                    optionsTotal = optionsTotal.add(new BigDecimal(opt.get("price").toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Tổng tiền = (Giá gốc + Tiền Topping) * Số lượng
        BigDecimal finalPricePerItem = basePrice.add(optionsTotal);
        BigDecimal itemTotal = finalPricePerItem.multiply(BigDecimal.valueOf(qty));

        return CartItemResponseDTO.builder()
                .id(cartItem.getId())
                .foodId(cartItem.getFood().getId())
                .restaurantId(cartItem.getFood().getRestaurant().getId())
                .restaurantName(cartItem.getFood().getRestaurant().getName())
                .foodName(cartItem.getFood().getName())
                .foodImageUrl(cartItem.getFood().getImageUrl())
                .basePrice(basePrice)
                .quantity(qty)
                .selectedOptions(parsedOptions)
                .itemTotal(itemTotal)
                .build();
    }
}