package com.example.uitpayapp.history;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;

public class DealDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_detail);

        findViewById(R.id.btnDealDetailBack).setOnClickListener(v -> finish());

        TextView txtMerchant = findViewById(R.id.txtDetailMerchant);
        TextView txtTitle = findViewById(R.id.txtDetailTitle);
        ImageView imgDetailThumb = findViewById(R.id.imgDetailThumb);
        TextView btnViewAppliedOrder = findViewById(R.id.btnViewAppliedOrder);
        TextView txtStatusHeader = findViewById(R.id.txtStatusHeader);
        TextView txtStatusDesc = findViewById(R.id.txtStatusDesc);

        String appliedOrderId = getIntent().getStringExtra("APPLIED_ORDER_ID");
        String merchantName = getIntent().getStringExtra("MERCHANT_NAME");
        String dealTitle = getIntent().getStringExtra("DEAL_TITLE");
        String statusText = getIntent().getStringExtra("STATUS_TEXT");
        String price = getIntent().getStringExtra("PRICE");
        String expiryText = getIntent().getStringExtra("EXPIRY_TEXT");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");

        txtMerchant.setText(merchantName);
        txtTitle.setText(dealTitle);

        // Update button text to look like viewing standard food order detail
        btnViewAppliedOrder.setText("Xem chi tiết đơn hàng ›");

        // Load food image using Glide
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(new android.graphics.drawable.ColorDrawable(android.graphics.Color.parseColor("#E0E0E0")))
                    .into(imgDetailThumb);
        } else {
            imgDetailThumb.setImageResource(android.R.color.darker_gray);
        }

        // Dynamically configure message based on status
        if (statusText != null) {
            String statusUpper = statusText.toUpperCase();
            if (statusText.contains("chuẩn bị") || statusText.contains("giao") || statusText.contains("chờ") ||
                statusUpper.contains("PREPARING") || statusUpper.contains("DELIVERING") || statusUpper.contains("PENDING")) {
                txtStatusHeader.setText("Đơn hàng đang xử lý");
                txtStatusDesc.setText("Món ăn của bạn đang được chuẩn bị hoặc giao đi. Vui lòng kiểm tra chi tiết đơn hàng.");
            } else if (statusText.contains("hủy") || statusUpper.contains("CANCELLED")) {
                txtStatusHeader.setText("Đơn hàng đã hủy");
                txtStatusDesc.setText("Đơn hàng chứa món ăn ưu đãi này đã bị hủy.");
            } else {
                txtStatusHeader.setText("Mua món ăn thành công");
                txtStatusDesc.setText("Bạn đã mua món ăn với giá ưu đãi thành công. Chúc bạn ngon miệng!");
            }
        } else {
            txtStatusHeader.setText("Mua món ăn thành công");
            txtStatusDesc.setText("Bạn đã mua món ăn với giá ưu đãi thành công. Chúc bạn ngon miệng!");
        }

        if (appliedOrderId == null || appliedOrderId.trim().isEmpty()) {
            btnViewAppliedOrder.setVisibility(android.view.View.GONE);
        } else {
            btnViewAppliedOrder.setVisibility(android.view.View.VISIBLE);
            btnViewAppliedOrder.setOnClickListener(v -> {
                Intent orderDetailIntent = new Intent(DealDetailActivity.this, OrderDetailActivity.class);
                orderDetailIntent.putExtra("ORDER_ID", appliedOrderId);
                startActivity(orderDetailIntent);
            });
        }
    }
}