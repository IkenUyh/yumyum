package com.example.uitpayapp.history;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uitpayapp.R;

public class OrderDetailActivity extends AppCompatActivity {

    // Ánh xạ chính xác theo ID trong file activity_order_detail.xml của bạn
    private TextView tvOrderTimeHeader, tvOrderStatusTitle;
    private RelativeLayout layoutDriverInfo;
    private TextView tvMerchantName, tvMerchantAddress;
    private TextView tvDestAddress, tvCustomerContact;
    private TextView tvItemsSubtotal, tvShipFee, tvDiscount, tvTotalPaid;
    private TextView tvUtensilsValue, tvOrderNote, tvDetailOrderId, tvOrderPlacedTime, tvPaymentMethod;
    private Button btnCancelOrder;
    private ImageButton btnMapBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        initViews();

        // Lấy ORDER_ID truyền từ Intent qua
        String orderId = getIntent().getStringExtra("ORDER_ID");

        if (orderId != null) {
            tvDetailOrderId.setText(orderId);

            // Bắt điều kiện mã đơn để đổ data động (không lo bị lệch thông tin)
            if ("15016-594977098".equals(orderId)) {
                loadDealOrderData();
            } else {
                loadDefaultActiveOrderData();
            }
        }

        // Xử lý nút quay lại
        if (btnMapBack != null) {
            btnMapBack.setOnClickListener(v -> finish());
        }
    }

    private void initViews() {
        tvOrderTimeHeader = findViewById(R.id.tvOrderTimeHeader);
        tvOrderStatusTitle = findViewById(R.id.tvOrderStatusTitle);
        layoutDriverInfo = findViewById(R.id.layoutDriverInfo);

        tvMerchantName = findViewById(R.id.tvMerchantName);
        tvMerchantAddress = findViewById(R.id.tvMerchantAddress);

        tvDestAddress = findViewById(R.id.tvDestAddress);
        tvCustomerContact = findViewById(R.id.tvCustomerContact);

        tvItemsSubtotal = findViewById(R.id.tvItemsSubtotal);
        tvShipFee = findViewById(R.id.tvShipFee);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvTotalPaid = findViewById(R.id.tvTotalPaid);

        tvUtensilsValue = findViewById(R.id.tvUtensilsValue);
        tvOrderNote = findViewById(R.id.tvOrderNote);
        tvDetailOrderId = findViewById(R.id.tvDetailOrderId);
        tvOrderPlacedTime = findViewById(R.id.tvOrderPlacedTime);
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);

        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnMapBack = findViewById(R.id.btnMapBack);
    }

    /**
     * Khớp 100% thông tin đơn Deal hoàn thành (Ảnh mẫu 3 & 4)
     */
    private void loadDealOrderData() {
        tvOrderTimeHeader.setText("Đơn hoàn tất");
        tvOrderStatusTitle.setText("Hoàn thành");

        // Đơn đã xong -> Ẩn thanh thông tin tài xế shipper đang chạy đi
        if (layoutDriverInfo != null) {
            layoutDriverInfo.setVisibility(View.GONE);
        }

        // Thông tin Tuyến đường quán ăn & điểm đến
        tvMerchantName.setText("Trà Sữa An Viên - Đường 30 Tháng 4");
        tvMerchantAddress.setText("188C Đường 30 Tháng 4, P. Xuân Khánh, Quận Ninh Kiều, Cần Thơ");
        tvDestAddress.setText("Ký túc xá sinh viên Khu A - Đại học Cần Thơ, Đường 3 tháng 2, P. Xuân Khánh, Cần Thơ");
        tvCustomerContact.setText("Cẩm Tiên - (+84) 839186864");

        // Tính toán chi tiết dòng tiền hóa đơn hóa đơn
        tvItemsSubtotal.setText("0đ"); // Áp dụng ưu đãi của Trùm Deal
        tvShipFee.setText("15.000đ");
        tvDiscount.setText("-15.000đ");
        tvTotalPaid.setText("11.000đ"); // Tổng cộng thanh toán thực tế là 11k

        // Khối thông tin hành chính đơn hàng bên dưới
        tvUtensilsValue.setText("Không lấy dụng cụ ăn uống");
        tvOrderNote.setText("Không có");
        tvOrderPlacedTime.setText("16:31 15/01/2026");
        tvPaymentMethod.setText("ShopeePay • VCB");

        // Đơn hoàn thành thì không cho hủy nữa
        if (btnCancelOrder != null) {
            btnCancelOrder.setVisibility(View.GONE);
        }
    }

    /**
     * Khớp thông tin đơn hàng đang đi giao kiểm thử (Ảnh mẫu cũ f85bd1 & f85bcb)
     */
    private void loadDefaultActiveOrderData() {
        tvOrderTimeHeader.setText("Thời gian giao dự kiến: 18:04");
        tvOrderStatusTitle.setText("Đơn đang được giao...");

        // Hiện khung thông tin tài xế Trần Hữu Tân lên
        if (layoutDriverInfo != null) {
            layoutDriverInfo.setVisibility(View.VISIBLE);
        }

        tvMerchantName.setText("Bun Burrito - Trần Quốc Toản");
        tvMerchantAddress.setText("63 Trần Quốc Toản, P. Dĩ An, Dĩ An, Bình Dương");
        tvDestAddress.setText("Quốc Lộ 1k, Khu Phố Đông Hòa, Dĩ An, Bình Dương");
        tvCustomerContact.setText("Huy Kiên - (+84) 987301126");

        tvItemsSubtotal.setText("58.000đ");
        tvShipFee.setText("15.000đ");
        tvDiscount.setText("0đ");
        tvTotalPaid.setText("76.000đ"); // Khớp số liệu 76k đang giao của ảnh f85bcb

        tvUtensilsValue.setText("Lấy dụng cụ ăn uống");
        tvOrderNote.setText("Không có");
        tvOrderPlacedTime.setText("Hôm nay 17:14");
        tvPaymentMethod.setText("Tiền mặt");

        if (btnCancelOrder != null) {
            btnCancelOrder.setVisibility(View.VISIBLE);
        }
    }
}