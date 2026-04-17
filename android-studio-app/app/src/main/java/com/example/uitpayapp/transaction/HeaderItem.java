package com.example.uitpayapp.transaction;

public class HeaderItem {
    String monthYear;
    long totalIncome;
    long totalExpense;

    public HeaderItem(String monthYear, long income, long expense) {
        this.monthYear = monthYear;
        this.totalIncome = income;
        this.totalExpense = expense;
    }
}