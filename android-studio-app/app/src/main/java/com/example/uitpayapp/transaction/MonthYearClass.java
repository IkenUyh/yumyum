package com.example.uitpayapp.transaction;

public abstract class MonthYearClass {
    public static final int TYPE_NAM = 0;
    public static final int TYPE_THANG = 1;

    public abstract int getType();
}

class YearHeader extends MonthYearClass {
    private int nam;
    public YearHeader(int nam) {
        this.nam = nam;
    }

    public int getNam() {
        return nam;
    }

    @Override
    public int getType() {
        return TYPE_NAM;
    }
}

class MonthItem extends MonthYearClass {
    private int thang;
    private int nam;
    private boolean isSelected;

    public MonthItem(int thang, int nam) {
        this.thang = thang;
        this.nam = nam;
        this.isSelected = false;
    }

    public int getThang() {
        return thang;
    }

    public int getNam() {
        return nam;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int getType() {
        return TYPE_THANG;
    }
}