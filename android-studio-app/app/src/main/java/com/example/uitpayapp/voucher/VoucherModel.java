package com.example.uitpayapp.voucher;

public class VoucherModel {
    private int icon;
    private String type;
    private String mainTitle;
    private String subTitle;
    private String voucherExpiration;
    private int coinCost;

    public VoucherModel(int icon, String type, String mainTitle, String subTitle, String voucherExpiration) {
        this.icon = icon;
        this.type = type;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.voucherExpiration = voucherExpiration;
        this.coinCost = 0; // Mặc định là 0
    }
    public VoucherModel(int icon, String type, String mainTitle, String subTitle, String voucherExpiration, int coinCost) {
        this.icon = icon;
        this.type = type;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.voucherExpiration = voucherExpiration;
        this.coinCost = coinCost;
    }

    public int getIcon() { return icon; }
    public String getType() { return type; }
    public String getMainTitle() { return mainTitle; }
    public String getSubTitle() { return subTitle; }
    public String getVoucherExpiration() { return voucherExpiration; }
    public int getCoinCost() { return coinCost; }
}