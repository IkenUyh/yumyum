package com.example.uitpayapp.modules.cart.models.requests;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CartItemRequestDTO {
    @SerializedName("foodId")
    private Long foodId;

    @SerializedName("quantity")
    private Integer quantity;

    @SerializedName("selectedOptionItemIds")
    private List<Long> selectedOptionItemIds;

    @SerializedName("appliedPromotion")
    private String appliedPromotion;

    public CartItemRequestDTO() {}

    public CartItemRequestDTO(Long foodId, Integer quantity, List<Long> selectedOptionItemIds, String appliedPromotion) {
        this.foodId = foodId;
        this.quantity = quantity;
        this.selectedOptionItemIds = selectedOptionItemIds;
        this.appliedPromotion = appliedPromotion;
    }

    public Long getFoodId() { return foodId; }
    public void setFoodId(Long foodId) { this.foodId = foodId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public List<Long> getSelectedOptionItemIds() { return selectedOptionItemIds; }
    public void setSelectedOptionItemIds(List<Long> selectedOptionItemIds) { this.selectedOptionItemIds = selectedOptionItemIds; }

    public String getAppliedPromotion() { return appliedPromotion; }
    public void setAppliedPromotion(String appliedPromotion) { this.appliedPromotion = appliedPromotion; }
}