package com.example.uitpayapp.transaction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TransactionHistory {
    private String id;
    private String title;         // Mô tả thông tin lịch sử giao dịch
    private long amount;          // Số tiền giao dịch
    private long remain;          // Số dư tài khoản
    private String date;          // Thời gian
    private String category;      // Phân loại giao dịch
    private String status;        // Trạng thái của giao dịch
    private String source;        //Nguồn giao dịch
    private boolean isIncome;     // true nếu là tiền vào (+), false nếu tiền ra (-)
    private int mainIconId;       // id của icon

    public TransactionHistory(String id, int mainIconId ,String title, long amount, long remain, String date, String category, String status, String source, boolean isIncome) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.remain = remain;
        this.date = date;
        this.category = category;
        this.status = status;
        this.source = source;
        this.isIncome = isIncome;
        this.mainIconId = mainIconId;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public long getAmount() { return amount; }
    public long getRemain() { return remain; }
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getSource() { return source; }
    public boolean isIncome() { return isIncome; }
    public int getMainIconId() { return mainIconId; }
    public int getYear() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            Date d = format.parse(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(d);

            return cal.get(Calendar.YEAR);
        } catch (Exception e) {
            return -1;
        }
        }
    public int getMonth(){
        try {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
            Date d = format.parse(date);

            Calendar cal = Calendar.getInstance();
            cal.setTime(d);

            return cal.get(Calendar.MONTH) + 1;
        } catch (Exception e) {
            return -1;
        }
    }
}