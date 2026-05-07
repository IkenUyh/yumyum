package com.example.uitpayapp.UITpayPriority;

import com.example.uitpayapp.R;

public class CheckInModel {
    // Enum để quản lý cấu hình cố định của 7 ngày
    public enum DayConfig {
        DAY_1("Ngày 1", 100, R.drawable.img_flag),
        DAY_2("Ngày 2", 110, R.drawable.img_flag),
        DAY_3("Ngày 3", 120, R.drawable.img_flag),
        DAY_4("Ngày 4", 130, R.drawable.img_flag),
        DAY_5("Ngày 5", 140, R.drawable.img_flag),
        DAY_6("Ngày 6", 150, R.drawable.img_flag),
        DAY_7("Ngày 7", 500, R.drawable.ic_gift);

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
    private boolean isChecked;

    public CheckInModel(DayConfig config, boolean isChecked) {
        this.config = config;
        this.isChecked = isChecked;
    }

    public DayConfig getConfig() { return config; }
    public void setConfig(DayConfig config) { this.config = config; }
    public boolean isChecked() { return isChecked; }
    public void setChecked(boolean checked) { isChecked = checked; }
}
