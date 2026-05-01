package com.example.uitpayapp.giftexchange;

public class ExchangeVoucherModel {
    private int brandLogo;
    private String title;
    private String condition;
    private String coinCost;
    private String originalCost;
    private String type;

    public ExchangeVoucherModel(int brandLogo, String title, String condition, String coinCost, String originalCost, String type) {
        this.brandLogo = brandLogo;
        this.title = title;
        this.condition = condition;
        this.coinCost = coinCost;
        this.originalCost = originalCost;
        this.type = type;
    }

    public int getBrandLogo() {
        return brandLogo;
    }

    public String getTitle() {
        return title;
    }

    public String getCondition() {
        return condition;
    }

    public String getCoinCost() {
        return coinCost;
    }

    public String getOriginalCost() {
        return originalCost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
