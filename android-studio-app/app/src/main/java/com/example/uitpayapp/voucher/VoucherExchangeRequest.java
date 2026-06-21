package com.example.uitpayapp.voucher;

import com.google.gson.annotations.SerializedName;

public class VoucherExchangeRequest {
    @SerializedName("title")
    private String title;

    @SerializedName("type")
    private String type;

    @SerializedName("coinCost")
    private Integer coinCost;

    public VoucherExchangeRequest(String title, String type, Integer coinCost) {
        this.title = title;
        this.type = type;
        this.coinCost = coinCost;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getCoinCost() { return coinCost; }
    public void setCoinCost(Integer coinCost) { this.coinCost = coinCost; }
}
