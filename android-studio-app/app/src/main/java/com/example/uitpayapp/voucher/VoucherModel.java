package com.example.uitpayapp.voucher;

public class VoucherModel {
    private int icon;
    private String type;
    private String mainTitle;
    private String subTitle;
    private String voucherExpiration;
    public VoucherModel(int icon, String type, String mainTitle, String subTitle, String voucherExpiration) {
        this.icon = icon;
        this.type = type;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.voucherExpiration = voucherExpiration;
    }
    public int getIcon() { return icon; }
    public String getType() { return type; }
    public String getMainTitle() { return mainTitle; }
    public String getSubTitle() { return subTitle; }
    public String getVoucherExpiration() { return voucherExpiration; }
}
