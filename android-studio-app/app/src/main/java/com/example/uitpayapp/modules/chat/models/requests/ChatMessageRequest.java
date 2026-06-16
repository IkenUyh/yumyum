package com.example.uitpayapp.modules.chat.models.requests;

import com.google.gson.annotations.SerializedName;

public class ChatMessageRequest {

    @SerializedName("senderId")
    private Long senderId;

    @SerializedName("content")
    private String content;

    public ChatMessageRequest() {
    }

    public ChatMessageRequest(Long senderId, String content) {
        this.senderId = senderId;
        this.content = content;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}