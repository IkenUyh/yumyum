package com.example.uitpayapp;

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

    // Các hàm Getters (Đã bổ sung đầy đủ)
    public String getId() { return id; }
    public String getTitle() { return title; }
    public long getAmount() { return amount; }
    public long getRemain() { return remain; }    // Đã thêm
    public String getDate() { return date; }
    public String getCategory() { return category; }
    public String getStatus() { return status; }
    public String getSource() { return source; }  // Đã thêm
    public boolean isIncome() { return isIncome; }
    public int getMainIconId() { return mainIconId; }
}