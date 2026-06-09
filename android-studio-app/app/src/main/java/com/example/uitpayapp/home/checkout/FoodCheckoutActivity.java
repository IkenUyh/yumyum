package com.example.uitpayapp.home.checkout;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class FoodCheckoutActivity extends AppCompatActivity {

    private CartManager cartManager;
    private long subtotalAmount;
    private long totalAmount;
    private VoucherModel selectedVoucher = null;

    private TextView tvSubtotal, tvTotalAmount, tvOriginalAmount, tvSelectedVoucher, tvDiscountAmount;
    private View layoutDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_checkout);
        getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        cartManager = CartManager.getInstance();

        initViews();
        loadCartData();
        updateTotals();
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvOriginalAmount = findViewById(R.id.tv_original_amount);
        tvSelectedVoucher = findViewById(R.id.tv_selected_voucher);
        tvDiscountAmount = findViewById(R.id.tv_discount_amount);
        layoutDiscount = findViewById(R.id.layout_discount);

        tvOriginalAmount.setPaintFlags(tvOriginalAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        CardView cardVoucher = findViewById(R.id.card_voucher);
        cardVoucher.setOnClickListener(v -> showVoucherBottomSheet());

        Button btnConfirmCheckout = findViewById(R.id.btn_confirm_checkout);
        btnConfirmCheckout.setOnClickListener(v -> processCheckout());
    }

    private void loadCartData() {
        RecyclerView rvItems = findViewById(R.id.rv_checkout_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        List<CartItem> cartItems = cartManager.getCart();
        FoodCheckoutAdapter adapter = new FoodCheckoutAdapter(cartItems);
        rvItems.setAdapter(adapter);

        subtotalAmount = cartManager.getTotalPrice();
    }

    private void updateTotals() {
        long discount = 0;
        if (selectedVoucher != null) {
            discount = selectedVoucher.getDiscountAmount();
            // Ensure total doesn't go below 0
            if (discount > subtotalAmount) {
                discount = subtotalAmount;
            }
        }

        totalAmount = subtotalAmount - discount;

        tvSubtotal.setText(String.format("%,dđ", subtotalAmount).replace(',', '.'));
        tvTotalAmount.setText(String.format("%,dđ", totalAmount).replace(',', '.'));

        if (discount > 0) {
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscountAmount.setText("-" + String.format("%,dđ", discount).replace(',', '.'));
            tvOriginalAmount.setVisibility(View.VISIBLE);
            tvOriginalAmount.setText(String.format("%,dđ", subtotalAmount).replace(',', '.'));
            tvSelectedVoucher.setText(selectedVoucher.getTitle());
            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#388E3C"));
        } else {
            layoutDiscount.setVisibility(View.GONE);
            tvOriginalAmount.setVisibility(View.GONE);
            tvSelectedVoucher.setText("Chọn hoặc nhập mã ưu đãi");
            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#757575"));
        }
    }

    private void showVoucherBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_voucher, null);
        bottomSheetDialog.setContentView(sheetView);

        sheetView.findViewById(R.id.btn_close_voucher).setOnClickListener(v -> bottomSheetDialog.dismiss());

        RecyclerView rvVouchers = sheetView.findViewById(R.id.rv_vouchers);
        rvVouchers.setLayoutManager(new LinearLayoutManager(this));

        List<VoucherModel> mockVouchers = new ArrayList<>();
        mockVouchers.add(new VoucherModel("v1", "Giảm 20.000đ", "Áp dụng cho đơn hàng từ 100k", 20000, 100000));
        mockVouchers.add(new VoucherModel("v2", "Giảm 50.000đ", "Áp dụng cho đơn hàng từ 200k", 50000, 200000));
        mockVouchers.add(new VoucherModel("v3", "Freeship 15.000đ", "Áp dụng cho đơn hàng từ 50k", 15000, 50000));

        // Lọc voucher đủ điều kiện (tuỳ chọn, ở đây mình cho hiện hết nhưng cảnh báo nếu không đủ điều kiện)
        VoucherAdapter adapter = new VoucherAdapter(mockVouchers, voucher -> {
            if (subtotalAmount >= voucher.getMinOrderAmount()) {
                selectedVoucher = voucher;
                updateTotals();
                bottomSheetDialog.dismiss();
                Toast.makeText(this, "Đã áp dụng mã: " + voucher.getTitle(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đơn hàng chưa đạt giá trị tối thiểu để dùng mã này!", Toast.LENGTH_SHORT).show();
            }
        });

        rvVouchers.setAdapter(adapter);
        bottomSheetDialog.show();
    }

    private void processCheckout() {
        if (cartManager.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        String productNames = cartManager.getProductSummary();

        Intent intent = new Intent(this, TransferSuccessActivity.class);
        intent.putExtra("KEY_AMOUNT", String.valueOf(totalAmount));
        intent.putExtra("KEY_IS_FOOD_ORDER", true);
        intent.putExtra("KEY_FOOD_PRODUCTS", productNames);
        startActivity(intent);

        cartManager.clearCart();
        finish();
    }
}
