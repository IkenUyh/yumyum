package com.example.uitpayapp.home.exchange_voucher;

public class ExchangeVoucherItem {
    private int id;
    private int iconResource;
    private String title;
    private int cost;

    public ExchangeVoucherItem(int id, int iconResource, String title, int cost) {
        this.id = id;
        this.iconResource = iconResource;
        this.title = title;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public int getIconResource() {
        return iconResource;
    }

    public String getTitle() {
        return title;
    }

    public int getCost() {
        return cost;
    }
}