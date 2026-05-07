package com.example.uitpayapp.home.receipt;

public class PendingBill {
    private String name;
    private String provider;
    private String dueDate;
    private String amount;
    private int iconResId;
    private boolean isPaid;
    private boolean isAutoPay;

    public PendingBill(String name, String provider, String dueDate, String amount, int iconResId, boolean isPaid) {
        this.name = name;
        this.provider = provider;
        this.dueDate = dueDate;
        this.amount = amount;
        this.iconResId = iconResId;
        this.isPaid = isPaid;
        this.isAutoPay = false;
    }

    public String getName() { return name; }
    public String getProvider() { return provider; }
    public String getDueDate() { return dueDate; }
    public String getAmount() { return amount; }
    public int getIconResId() { return iconResId; }
    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }
    public boolean isAutoPay() { return isAutoPay; }
    public void setAutoPay(boolean autoPay) { isAutoPay = autoPay; }
}
