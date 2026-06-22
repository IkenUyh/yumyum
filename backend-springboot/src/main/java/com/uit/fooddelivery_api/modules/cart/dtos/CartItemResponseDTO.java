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
    private BigDecimal restaurantLatitude;
    private BigDecimal restaurantLongitude;
    private java.time.LocalTime restaurantOpenTime;
    private java.time.LocalTime restaurantCloseTime;
    private Boolean isAcceptingOrders;
    private List<Map<String, Object>> selectedOptions; // Trả về list topping cho frontend dễ vẽ UI
    private BigDecimal itemTotal;
    private String appliedPromotion;

    public static CartItemResponseDTO fromEntity(CartItem cartItem) {
        return fromEntity(cartItem, null);
    }

    public static CartItemResponseDTO fromEntity(CartItem cartItem, com.uit.fooddelivery_api.modules.food.services.PriceCalculationService priceCalculationService) {
        BigDecimal basePrice = cartItem.getFood().getPrice();
        
        if (priceCalculationService != null) {
            com.uit.fooddelivery_api.modules.food.services.PriceCalculationService.PriceResult pr = 
                priceCalculationService.calculateFinalPrice(cartItem.getFood(), cartItem.getAppliedPromotion());
            basePrice = pr.finalPrice;
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
                .restaurantLatitude(cartItem.getFood().getRestaurant().getLatitude())
                .restaurantLongitude(cartItem.getFood().getRestaurant().getLongitude())
                .restaurantOpenTime(cartItem.getFood().getRestaurant().getOpenTime())
                .restaurantCloseTime(cartItem.getFood().getRestaurant().getCloseTime())
                .isAcceptingOrders(cartItem.getFood().getRestaurant().getIsAcceptingOrders())
                .foodName(cartItem.getFood().getName())
                .foodImageUrl(cartItem.getFood().getImageUrl())
                .basePrice(basePrice)
                .quantity(qty)
                .selectedOptions(parsedOptions)
                .itemTotal(itemTotal)
                .appliedPromotion(cartItem.getAppliedPromotion())
                .build();
    }
}