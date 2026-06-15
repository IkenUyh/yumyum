package com.example.uitpayapp.modules.chat.models.responses;

import com.google.gson.annotations.SerializedName;

public class ChatMessageResponse {

    @SerializedName("id")
    private Long id;

    @SerializedName("orderId")
    private Long orderId;

    @SerializedName("senderId")
    private Long senderId;

    @SerializedName("senderName")
    private String senderName;

    @SerializedName("content")
    private String content;

    @SerializedName("createdAt")
    private String createdAt; // Nhận dạng chuỗi ISO-8601 từ LocalDateTime của Backend

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}