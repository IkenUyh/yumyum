package com.example.uitpayapp.merchant.marketing;

public class TransactionModel {
    private String date;
    private String title;
    private String amount;
    private int color;

    public TransactionModel(String date, String title, String amount, int color) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.color = color;
    }

    public String getDate() { return date; }
    public String getTitle() { return title; }
    public String getAmount() { return amount; }
    public int getColor() { return color; }
}
