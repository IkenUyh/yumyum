package com.example.uitpayapp.admin;

public class PendingStore {
    private String id;
    private String storeName;
    private String ownerName;
    private String address;
    private String storeType;
    private int imageRes;
    private String status; // "pending", "approved", "rejected"
    private String rejectReason;
    private String submittedDate;

    public PendingStore(String id, String storeName, String ownerName, String address, String storeType, int imageRes, String status, String submittedDate) {
        this.id = id;
        this.storeName = storeName;
        this.ownerName = ownerName;
        this.address = address;
        this.storeType = storeType;
        this.imageRes = imageRes;
        this.status = status;
        this.submittedDate = submittedDate;
    }

    public String getId() { return id; }
    public String getStoreName() { return storeName; }
    public String getOwnerName() { return ownerName; }
    public String getAddress() { return address; }
    public String getStoreType() { return storeType; }
    public int getImageRes() { return imageRes; }
    public String getStatus() { return status; }
    public String getRejectReason() { return rejectReason; }
    public String getSubmittedDate() { return submittedDate; }

    public void setStatus(String status) { this.status = status; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
}
