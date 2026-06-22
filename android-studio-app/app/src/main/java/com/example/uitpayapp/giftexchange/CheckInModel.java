package com.example.uitpayapp.giftexchange;

import com.example.uitpayapp.R;

public class CheckInModel {
    // Enum để quản lý cấu hình cố định của 7 ngày
    public enum DayConfig {
        DAY_1("Ngày 1", 150, R.drawable.ic_coin),
        DAY_2("Ngày 2", 200, R.drawable.ic_coin),
        DAY_3("Ngày 3", 250, R.drawable.ic_coin),
        DAY_4("Ngày 4", 300, R.drawable.ic_coin),
        DAY_5("Ngày 5", 350, R.drawable.ic_coin),
        DAY_6("Ngày 6", 400, R.drawable.ic_coin),
        DAY_7("Ngày 7", 450, R.drawable.ic_account_manage_gift);

        private final String title;
        private final int coins;
        private final int iconRes;

        DayConfig(String title, int coins, int iconRes) {
            this.title = title;
            this.coins = coins;
            this.iconRes = iconRes;
        }

        public String getTitle() { return title; }
        public int getCoins() { return coins; }
        public int getIconRes() { return iconRes; }
    }

    private DayConfig config;
    private boolean isOpened;
    private boolean isChecked;

    public CheckInModel(DayConfig config, boolean isOpened, boolean isChecked) {
        this.config = config;
        this.isOpened = isOpened;
        this.isChecked = isChecked;
    }

    public DayConfig getConfig() { return config; }
    public void setConfig(DayConfig config) { this.config = config; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
    public boolean isOpened() { return isOpened; }
}
