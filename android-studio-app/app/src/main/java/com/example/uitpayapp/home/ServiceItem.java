package com.example.uitpayapp.home;

public class ServiceItem {
    private String name;
    private int iconResId;
    private String badgeText;

    public ServiceItem(String name, int iconResId, String badgeText) {
        this.name = name;
        this.iconResId = iconResId;
        this.badgeText = badgeText;
    }

    public String getName() { return name; }
    public int getIconResId() { return iconResId; }
    public String getBadgeText() { return badgeText; }
}