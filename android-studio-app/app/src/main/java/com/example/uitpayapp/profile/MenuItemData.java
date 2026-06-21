package com.example.uitpayapp.profile;

public class MenuItemData {
    private String title, subtitle;
    private int icon;
    Boolean IsSpecialItem;
    public boolean hideArrow = false;
    public MenuItemData(String title, String subtitle, int icon, Boolean IsSpecialItem) {
        this.title = title; this.subtitle = subtitle; this.icon = icon;
        this.IsSpecialItem = IsSpecialItem;
    }
    public MenuItemData(String title, String subtitle, int icon, Boolean IsSpecialItem, boolean hideArrow) {
        this.title = title; this.subtitle = subtitle; this.icon = icon;
        this.IsSpecialItem = IsSpecialItem;
        this.hideArrow = hideArrow;
    }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    public int getIcon() { return icon; }
}
