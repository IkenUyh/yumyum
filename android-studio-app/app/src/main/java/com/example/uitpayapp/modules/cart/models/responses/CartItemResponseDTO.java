package com.example.uitpayapp.modules.cart.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CartItemResponseDTO {
    @SerializedName("id")
    private Long id;

    @SerializedName("foodId")
    private Long foodId;

    @SerializedName("foodName")
    private String foodName;

    @SerializedName("foodImageUrl")
    private String foodImageUrl;

    @SerializedName("basePrice")
    private BigDecimal basePrice;

    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("restaurantName")
    private String restaurantName;

    @SerializedName("restaurantLatitude")
    private Double restaurantLatitude;

    @SerializedName("restaurantLongitude")
    private Double restaurantLongitude;

    @SerializedName("selectedOptions")
    private List<Map<String, Object>> selectedOptions; // Trả về list mapping map chứa thông tin topping để vẽ UI

    @SerializedName("itemTotal")
    private BigDecimal itemTotal;

    @SerializedName("appliedPromotion")
    private String appliedPromotion;

    // Getters và Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public Double getRestaurantLatitude() { return restaurantLatitude; }
    public void setRestaurantLatitude(Double restaurantLatitude) { this.restaurantLatitude = restaurantLatitude; }

    public Double getRestaurantLongitude() { return restaurantLongitude; }
    public void setRestaurantLongitude(Double restaurantLongitude) { this.restaurantLongitude = restaurantLongitude; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getFoodImageUrl() { return foodImageUrl; }
    public void setFoodImageUrl(String foodImageUrl) { this.foodImageUrl = foodImageUrl; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public List<Map<String, Object>> getSelectedOptions() { return selectedOptions; }
    public void setSelectedOptions(List<Map<String, Object>> selectedOptions) { this.selectedOptions = selectedOptions; }

    public BigDecimal getItemTotal() { return itemTotal; }
    public void setItemTotal(BigDecimal itemTotal) { this.itemTotal = itemTotal; }

    public String getAppliedPromotion() { return appliedPromotion; }
    public void setAppliedPromotion(String appliedPromotion) { this.appliedPromotion = appliedPromotion; }
}