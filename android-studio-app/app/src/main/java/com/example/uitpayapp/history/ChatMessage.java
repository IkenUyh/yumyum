package com.example.uitpayapp.history;

public class ChatMessage {
    public static final int TYPE_SYSTEM = 0; // Tin nhắn tự động từ tổng đài hỗ trợ
    public static final int TYPE_DRIVER = 1; // Tin nhắn từ shipper
    public static final int TYPE_USER = 2;   // Tin nhắn của chính người dùng

    private String messageId;
    private int senderType;
    private String text;
    private String timestamp;
    private String senderName;

    public ChatMessage(String messageId, int senderType, String text, String timestamp, String senderName) {
        this.messageId = messageId;
        this.senderType = senderType;
        this.text = text;
        this.timestamp = timestamp;
        this.senderName = senderName;
    }

    public String getMessageId() { return messageId; }
    public int getSenderType() { return senderType; }
    public String getText() { return text; }
    public String getTimestamp() { return timestamp; }
    public String getSenderName() { return senderName; }
}