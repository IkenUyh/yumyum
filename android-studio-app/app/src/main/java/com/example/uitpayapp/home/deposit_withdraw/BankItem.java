package com.example.uitpayapp.home.deposit_withdraw;

public class BankItem {
    private String name;
    private String fullName;
    private int iconResId;

    public BankItem(String name, String fullName, int iconResId) {
        this.name = name;
        this.fullName = fullName;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}