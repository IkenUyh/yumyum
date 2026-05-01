package com.example.uitpayapp.profile;

import android.graphics.Color;

import com.example.uitpayapp.R;

public class AccountCardModel {

    public enum AccountType {
        TAI_CHINH("#FFF0F5"),
        SO_DU("#E8F6FF");

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
