package com.example.uitpayapp.modules.review.models.requests;

import com.google.gson.annotations.SerializedName;

public class CreateReviewRequest {
    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("rating")
    private Integer rating;

    @SerializedName("comment")
    private String comment;

    public CreateReviewRequest(Long orderId, Integer rating, String comment) {
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}