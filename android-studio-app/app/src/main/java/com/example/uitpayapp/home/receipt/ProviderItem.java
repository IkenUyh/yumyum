package com.example.uitpayapp.home.receipt;

public class ProviderItem {
    private String title;
    private String subtitle;
    private int iconResId;
    private String iconUrl;

    public ProviderItem(String title, String subtitle, int iconResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconResId = iconResId;
        this.iconUrl = null;
    }

    public ProviderItem(String title, String subtitle, String iconUrl) {
        this.title = title;
        this.subtitle = subtitle;
        this.iconUrl = iconUrl;
        this.iconResId = 0;
    }

    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public int getIconResId() { return iconResId; }
    public String getIconUrl() { return iconUrl; }

    @Override
    public String toString() {
        return title;
    }
}
