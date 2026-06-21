package com.example.uitpayapp.voucher;

import com.example.uitpayapp.R;

public class VoucherModel {
    public enum VoucherType {
        FOOD_DISCOUNT("Giảm giá món", R.drawable.ic_food, "#f24405"),
        SHIPPING_FEE("Miễn phí vận chuyển", R.drawable.ic_delivery, "#00BFA5");

        private final String displayName;
        private final int iconResId;
        private final String colorHex;

        VoucherType(String displayName, int iconResId, String colorHex) {
            this.displayName = displayName;
            this.iconResId = iconResId;
            this.colorHex = colorHex;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getIconResId() {
            return iconResId;
        }

        public String getColorHex() {
            return colorHex;
        }
    }

    private VoucherType type;
    private String mainTitle;
    private String subTitle;
    private String voucherExpiration;
    private int coinCost;

    public VoucherModel(VoucherType type, String mainTitle, String subTitle, String voucherExpiration) {
        this.type = type;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.voucherExpiration = voucherExpiration;
        this.coinCost = 0;
    }

    public VoucherModel(VoucherType type, String mainTitle, String subTitle, String voucherExpiration, int coinCost) {
        this.type = type;
        this.mainTitle = mainTitle;
        this.subTitle = subTitle;
        this.voucherExpiration = voucherExpiration;
        this.coinCost = coinCost;
    }

    public int getIcon() {
        return type.getIconResId();
    }

    public String getType() {
        return type.getDisplayName();
    }

    public VoucherType getVoucherType() {
        return type;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getVoucherExpiration() {
        return voucherExpiration;
    }

    public int getCoinCost() {
        return coinCost;
    }
}