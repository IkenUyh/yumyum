package com.example.uitpayapp.history;

import java.util.Date;

public class CalendarItem {
    public static final int TYPE_MONTH_HEADER = 0;
    public static final int TYPE_WEEK_HEADER = 1;
    public static final int TYPE_DAY = 2;

    private int type;
    private String text;
    private Date date;
    private boolean isPadding; // Ô trống đệm đầu tháng để lệch đúng Thứ

    public CalendarItem(int type, String text, Date date, boolean isPadding) {
        this.type = type;
        this.text = text;
        this.date = date;
        this.isPadding = isPadding;
    }

    public int getType() { return type; }
    public String getText() { return text; }
    public Date getDate() { return date; }
    public boolean isPadding() { return isPadding; }
}