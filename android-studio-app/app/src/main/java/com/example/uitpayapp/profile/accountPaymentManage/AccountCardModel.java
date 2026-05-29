package com.example.uitpayapp.profile.accountPaymentManage;

import android.graphics.Color;

public class AccountCardModel {

    public enum AccountType {
        NGAN_HANG("#E8F6FF"),
        SO_DU("#FCE4E4");

        private final String bgColor;

        AccountType(String bgColor) {
            this.bgColor = bgColor;
        }

        public int getBgColor() { return Color.parseColor(bgColor); }
    }
    private String title;
    private String subTitle;
    private String actionText;
    //private String amount;
    private AccountType type;

    public AccountCardModel(String title, String subTitle, String actionText, AccountType type) {
        this.title = title;
        this.subTitle = subTitle;
        this.actionText = actionText;
        //this.amount = amount;
        this.type = type;
    }

    public String getTitle() { return title; }
    public String getSubTitle() { return subTitle; }
    public String getActionText() { return actionText; }
    //public String getAmount() { return amount; }
    public AccountType getType() { return type; }
}
