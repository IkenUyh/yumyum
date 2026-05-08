package com.example.uitpayapp.home.accmulated_balance;

public class AccmulatedTransaction {
    private String title;
    private String date;
    private String amount;
    private boolean isDeposit; // true = nạp, false = rút

    public AccmulatedTransaction(String title, String date, String amount, boolean isDeposit) {
        this.title = title;
        this.date = date;
        this.amount = amount;
        this.isDeposit = isDeposit;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getAmount() { return amount; }
    public boolean isDeposit() { return isDeposit; }
}
