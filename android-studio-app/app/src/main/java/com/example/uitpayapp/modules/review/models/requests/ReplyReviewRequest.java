package com.example.uitpayapp.modules.review.models.requests;

public class ReplyReviewRequest {
    private String merchantReply;

    public ReplyReviewRequest(String merchantReply) {
        this.merchantReply = merchantReply;
    }

    public String getMerchantReply() {
        return merchantReply;
    }

    public void setMerchantReply(String merchantReply) {
        this.merchantReply = merchantReply;
    }
}
