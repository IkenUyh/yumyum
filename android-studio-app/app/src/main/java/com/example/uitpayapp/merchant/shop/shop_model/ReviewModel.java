package com.example.uitpayapp.merchant.shop.shop_model;

public class ReviewModel {
    private String id;
    private String userName;
    private String userAvatar;
    private float rating;
    private String ratingText;
    private String orderId;
    private String date;
    private String content;
    private String reviewImage; // URL or resource for the food image

    // Reply fields
    private String replyName;
    private String replyDate;
    private String replyContent;
    private boolean hasReply;

    public ReviewModel(String id, String userName, String userAvatar, float rating, String ratingText, String orderId, String date, String content, String reviewImage) {
        this.id = id;
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.ratingText = ratingText;
        this.orderId = orderId;
        this.date = date;
        this.content = content;
        this.reviewImage = reviewImage;
        this.hasReply = false;
    }

    public String getId() { return id; }

    public void setReply(String replyName, String replyDate, String replyContent) {
        this.replyName = replyName;
        this.replyDate = replyDate;
        this.replyContent = replyContent;
        this.hasReply = true;
    }

    public String getUserName() { return userName; }
    public String getUserAvatar() { return userAvatar; }
    public float getRating() { return rating; }
    public String getRatingText() { return ratingText; }
    public String getOrderId() { return orderId; }
    public String getDate() { return date; }
    public String getContent() { return content; }
    public String getReviewImage() { return reviewImage; }
    public String getReplyName() { return replyName; }
    public String getReplyDate() { return replyDate; }
    public String getReplyContent() { return replyContent; }
    public boolean hasReply() { return hasReply; }
}
