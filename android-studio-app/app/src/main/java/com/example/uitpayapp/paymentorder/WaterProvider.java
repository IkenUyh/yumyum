package com.example.uitpayapp.paymentorder;

public class WaterProvider {
    private String name;
    private String subtitle;
    private int iconRes;

    public WaterProvider(String name, String subtitle, int iconRes) {
        this.name = name;
        this.subtitle = subtitle;
        this.iconRes = iconRes;
    }

    public String getName() { return name; }
    public String getSubtitle() { return subtitle; }
    public int getIconRes() { return iconRes; }
}
