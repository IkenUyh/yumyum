package com.example.uitpayapp.modules.order.models.responses;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class OrderResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("totalAmount")
    private BigDecimal totalAmount;

    @SerializedName("status")
    private String status;

    @SerializedName("deliveryMode")
    private String deliveryMode;

    @SerializedName("expectedDeliveryTime")
    private String expectedDeliveryTime; // Nhận dạng chuỗi ISO từ LocalDateTime backend

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(String deliveryMode) { this.deliveryMode = deliveryMode; }

    public String getExpectedDeliveryTime() { return expectedDeliveryTime; }
    public void setExpectedDeliveryTime(String expectedDeliveryTime) { this.expectedDeliveryTime = expectedDeliveryTime; }
}