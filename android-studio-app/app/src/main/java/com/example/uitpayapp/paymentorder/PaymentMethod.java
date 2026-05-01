package com.example.uitpayapp.paymentorder;

public class PaymentMethod {
    private String name;
    private String detail;
    private int iconResId;
    private boolean enabled;

    public PaymentMethod(String name, String detail, int iconResId, boolean enabled) {
        this.name = name;
        this.detail = detail;
        this.iconResId = iconResId;
        this.enabled = enabled;
    }

    public String getName() { return name; }
    public String getDetail() { return detail; }
    public int getIconResId() { return iconResId; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}