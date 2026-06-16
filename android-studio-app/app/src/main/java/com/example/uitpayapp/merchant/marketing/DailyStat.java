package com.example.uitpayapp.merchant.marketing;

public class DailyStat {
    private String date;
    private float amount;

    public DailyStat(String date, float amount) {
        this.date = date;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public float getAmount() {
        return amount;
    }
}
