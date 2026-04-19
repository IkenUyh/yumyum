package com.example.uitpayapp.home.money_transfer;

public class RecipientItem {
    private String name;
    private int avatarResId;

    public RecipientItem(String name, int avatarResId) {
        this.name = name;
        this.avatarResId = avatarResId;
    }

    public String getName() {
        return name;
    }

    public int getAvatarResId() {
        return avatarResId;
    }
}