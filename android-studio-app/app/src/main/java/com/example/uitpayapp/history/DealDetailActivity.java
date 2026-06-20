package com.example.uitpayapp.history;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uitpayapp.R;

public class DealDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_detail);

        findViewById(R.id.btnDealDetailBack).setOnClickListener(v -> finish());

        TextView txtMerchant = findViewById(R.id.txtDetailMerchant);
        TextView txtTitle = findViewById(R.id.txtDetailTitle);
        TextView btnViewAppliedOrder = findViewById(R.id.btnViewAppliedOrder);

        String appliedOrderId = getIntent().getStringExtra("APPLIED_ORDER_ID");
        txtMerchant.setText(getIntent().getStringExtra("MERCHANT_NAME"));
        txtTitle.setText(getIntent().getStringExtra("DEAL_TITLE"));

        if (appliedOrderId == null || appliedOrderId.trim().isEmpty()) {
            btnViewAppliedOrder.setVisibility(android.view.View.GONE);
        } else {
            btnViewAppliedOrder.setVisibility(android.view.View.VISIBLE);
            // LUỒNG TÁI SỬ DỤNG MÀN HÌNH CHI TIẾT ĐƠN HÀNG CŨ CỦA BẠN (Ảnh 3, 4)
            btnViewAppliedOrder.setOnClickListener(v -> {
                // Gọi chính xác tên class màn hình chi tiết đơn hàng cũ của bạn (OrderDetailActivity)
                Intent orderDetailIntent = new Intent(DealDetailActivity.this, OrderDetailActivity.class);
                orderDetailIntent.putExtra("ORDER_ID", appliedOrderId);
                startActivity(orderDetailIntent);
            });
        }
    }
}