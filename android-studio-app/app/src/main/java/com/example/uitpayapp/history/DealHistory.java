package com.example.uitpayapp.history;

public class DealHistory {
    private String dealId;
    private String merchantName;
    private String purchaseDate;
    private String dealTitle;
    private String price;
    private String expiryText;
    private String quantityText;
    private String statusText;
    private String appliedOrderId; // ID để liên kết sang màn hình Chi tiết đơn hàng cũ của bạn

    public DealHistory(String dealId, String merchantName, String purchaseDate, String dealTitle, String price, String expiryText, String quantityText, String statusText, String appliedOrderId) {
        this.dealId = dealId;
        this.merchantName = merchantName;
        this.purchaseDate = purchaseDate;
        this.dealTitle = dealTitle;
        this.price = price;
        this.expiryText = expiryText;
        this.quantityText = quantityText;
        this.statusText = statusText;
        this.appliedOrderId = appliedOrderId;
    }

    public String getDealId() { return dealId; }
    public String getMerchantName() { return merchantName; }
    public String getPurchaseDate() { return purchaseDate; }
    public String getDealTitle() { return dealTitle; }
    public String getPrice() { return price; }
    public String getExpiryText() { return expiryText; }
    public String getQuantityText() { return quantityText; }
    public String getStatusText() { return statusText; }
    public String getAppliedOrderId() { return appliedOrderId; }
}