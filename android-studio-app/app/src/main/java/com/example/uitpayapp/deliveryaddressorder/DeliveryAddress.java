package com.example.uitpayapp.deliveryaddressorder;

import com.example.uitpayapp.R;

public class DeliveryAddress {
    public enum AddressType {
        HOME("Nhà", com.example.uitpayapp.R.drawable.ic_home),
        WORK("Nơi làm việc", R.drawable.ic_other_place);

        private final String displayName;
        private final int iconResId;

        AddressType(String displayName, int iconResId) {
            this.displayName = displayName;
            this.iconResId = iconResId;
        }

        public String getDisplayName() { return displayName; }
        public int getIconResId() { return iconResId; }
    }
    private AddressType addressType;
    private String addressDetail;
    private String receiverName;
    private String phoneNumber;

    public DeliveryAddress(AddressType addressType, String addressDetail, String receiverName, String phoneNumber) {
        this.addressType = addressType;
        this.addressDetail = addressDetail;
        this.receiverName = receiverName;
        this.phoneNumber = phoneNumber;
    }

    public AddressType getAddressType() { return addressType; }
    public String getAddressDetail() { return addressDetail; }
    public String getReceiverName() { return receiverName; }
    public String getPhoneNumber() { return phoneNumber; }
}