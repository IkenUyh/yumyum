package com.example.uitpayapp.modules.review.models.responses;

import com.google.gson.annotations.SerializedName;

public class ReviewResponse {
    @SerializedName("id")
    private Long id;

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("restaurantId")
    private Long restaurantId;

    @SerializedName("customerName")
    private String customerName;

    @SerializedName("rating")
    private Integer rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("createdAt")
    private String createdAt; // Nhận dạng chuỗi ISO từ LocalDateTime của Spring Boot

    @SerializedName("merchantReply")
    private String merchantReply;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getRestaurantId() { return restaurantId; }
    public void setRestaurantId(Long restaurantId) { this.restaurantId = restaurantId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getMerchantReply() { return merchantReply; }
    public void setMerchantReply(String merchantReply) { this.merchantReply = merchantReply; }
}