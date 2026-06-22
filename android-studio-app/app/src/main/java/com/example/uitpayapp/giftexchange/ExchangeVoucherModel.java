package com.example.uitpayapp.giftexchange;

import com.example.uitpayapp.R;

public class ExchangeVoucherModel {
    public enum ExchangeVoucherType {
        FOOD_DISCOUNT("Giảm giá món", R.drawable.ic_food, "#f24405"),
        SHIPPING_FEE("Phí vận chuyển", R.drawable.ic_delivery, "#00BFA5");

        private final String displayName;
        private final int iconResId;
        private final String colorHex;

        ExchangeVoucherType(String displayName, int iconResId, String colorHex) {
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

    private int brandLogo;
    private String title;
    private String condition;
    private String coinCost;
    private ExchangeVoucherType type;

    public ExchangeVoucherModel(String title, String condition, String coinCost, ExchangeVoucherType type) {
        this.title = title;
        this.condition = condition;
        this.coinCost = coinCost;
        this.type = type;
    }

    public int getBrandLogo() {
        return brandLogo;
    }

    public String getTitle() {
        return title;
    }

    public String getCondition() {
        return condition;
    }

    public String getCoinCost() {
        return coinCost;
    }

    public ExchangeVoucherType getVoucherType() {
        return type;
    }
}
