package com.example.uitpayapp.home.checkout;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;



public class FoodCheckoutActivity extends AppCompatActivity {

    private CartManager cartManager;
    private long subtotalAmount;
    private long totalAmount;
    private VoucherModel selectedVoucher = null;

    private TextView tvSubtotal, tvSelectedVoucher, tvDiscountAmount;
    private View layoutDiscount;
    
    private SwitchMaterial switchCoins;
    private SwitchMaterial switchUtensils;
    private EditText etNote;
    private TextView tvDeliveryFee;
    private View layoutCoinsDiscount;
    private TextView tvCoinsDiscountAmount;
    private Button btnConfirmCheckout;
    private long deliveryFee = 15000;
    private long coinsDiscount = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_checkout);
        getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        cartManager = CartManager.getInstance();

        initViews();
        loadCartData();
        fetchPreviewData(); // Fetch real data from backend
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvSelectedVoucher = findViewById(R.id.tv_selected_voucher);
        tvDiscountAmount = findViewById(R.id.tv_discount_amount);
        layoutDiscount = findViewById(R.id.layout_discount);

        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        layoutCoinsDiscount = findViewById(R.id.layout_coins_discount);
        tvCoinsDiscountAmount = findViewById(R.id.tv_coins_discount_amount);

        switchCoins = findViewById(R.id.switch_coins);
        switchCoins.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotals());

        switchUtensils = findViewById(R.id.switch_utensils);
        etNote = findViewById(R.id.et_note);

        CardView cardVoucher = findViewById(R.id.card_voucher);
        cardVoucher.setOnClickListener(v -> showVoucherBottomSheet());

        btnConfirmCheckout = findViewById(R.id.btn_confirm_checkout);
        btnConfirmCheckout.setOnClickListener(v -> processCheckout());

        // Init suggested items
        RecyclerView rvSuggested = findViewById(R.id.rv_suggested_items);
        rvSuggested.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        List<SuggestedItemAdapter.SuggestedItem> suggestedItems = new ArrayList<>();
        suggestedItems.add(new SuggestedItemAdapter.SuggestedItem("Trà sữa trân châu", 25000, 0));
        suggestedItems.add(new SuggestedItemAdapter.SuggestedItem("Gà rán giòn", 35000, 0));
        suggestedItems.add(new SuggestedItemAdapter.SuggestedItem("Pizza phô mai", 45000, 0));
        rvSuggested.setAdapter(new SuggestedItemAdapter(suggestedItems));
    }

    private void loadCartData() {
        RecyclerView rvItems = findViewById(R.id.rv_checkout_items);
        rvItems.setLayoutManager(new LinearLayoutManager(this));

        List<CartItem> cartItems = cartManager.getCart();
        FoodCheckoutAdapter adapter = new FoodCheckoutAdapter(cartItems);
        rvItems.setAdapter(adapter);

        subtotalAmount = cartManager.getTotalPrice();

        // Fetch wallet balance
        TextView tvWalletBalance = findViewById(R.id.tv_wallet_balance);
        if (tvWalletBalance != null) {
            com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
            walletRepo.getBalance(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse>() {
                @Override
                public void onSuccess(com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse data) {
                    runOnUiThread(() -> {
                        java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                        tvWalletBalance.setText(format.format(data.getBalance()) + "đ");
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        tvWalletBalance.setText("Lỗi lấy số dư");
                    });
                }
            });
        }
    }

    private void updateTotals() {
        long discount = 0;
        if (selectedVoucher != null) {
            discount = selectedVoucher.getDiscountAmount();
        }

        long totalCoinsDiscount = 0;
        if (switchCoins.isChecked()) {
            totalCoinsDiscount = coinsDiscount;
            layoutCoinsDiscount.setVisibility(View.VISIBLE);
        } else {
            layoutCoinsDiscount.setVisibility(View.GONE);
        }

        long totalDiscount = discount + totalCoinsDiscount;
        // Ensure total doesn't go below 0
        if (totalDiscount > subtotalAmount + deliveryFee) {
            totalDiscount = subtotalAmount + deliveryFee;
        }

        totalAmount = subtotalAmount + deliveryFee - totalDiscount;

        tvSubtotal.setText(String.format("%,dđ", subtotalAmount).replace(',', '.'));
        tvDeliveryFee.setText(String.format("%,dđ", deliveryFee).replace(',', '.'));
        btnConfirmCheckout.setText("Đặt đơn - " + String.format("%,dđ", totalAmount).replace(',', '.'));

        if (discount > 0) {
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscountAmount.setText("-" + String.format("%,dđ", discount).replace(',', '.'));
            tvSelectedVoucher.setText(selectedVoucher.getTitle());
            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#388E3C"));
        } else {
            layoutDiscount.setVisibility(View.GONE);
            tvSelectedVoucher.setText("Chọn hoặc nhập mã ưu đãi");
            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#757575"));
        }
    }

    private void fetchPreviewData() {
        com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest request = 
                new com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest(
                1L, 1L, "STANDARD", new ArrayList<>() // hardcoded cho demo
        );

        com.example.uitpayapp.modules.order.OrderRepository orderRepo = new com.example.uitpayapp.modules.order.OrderRepository();
        orderRepo.previewOrder(request, new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.order.models.responses.OrderPreviewResponse>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.order.models.responses.OrderPreviewResponse data) {
                runOnUiThread(() -> {
                    deliveryFee = (long) data.getShippingFee();
                    subtotalAmount = (long) data.getFoodTotal();
                    updateTotals();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(FoodCheckoutActivity.this, "Không thể tính phí ship: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
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

        // TÍCH HỢP API THANH TOÁN
        com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest request = 
                new com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest(
                1L, 1L, "STANDARD", new ArrayList<>() // hardcoded cho demo
        );

        com.example.uitpayapp.modules.order.OrderRepository orderRepo = new com.example.uitpayapp.modules.order.OrderRepository();
        orderRepo.createOrder(request, new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.order.models.responses.OrderResponse>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.order.models.responses.OrderResponse data) {
                runOnUiThread(() -> {
                    Intent intent = new Intent(FoodCheckoutActivity.this, TransferSuccessActivity.class);
                    intent.putExtra("KEY_AMOUNT", String.valueOf(totalAmount));
                    intent.putExtra("KEY_IS_FOOD_ORDER", true);
                    intent.putExtra("KEY_FOOD_PRODUCTS", productNames);
                    intent.putExtra("KEY_DELIVERY_FEE", deliveryFee);
                    
                    long discountAmount = selectedVoucher != null ? selectedVoucher.getDiscountAmount() : 0;
                    intent.putExtra("KEY_DISCOUNT", discountAmount);
                    
                    startActivity(intent);

                    cartManager.clearCartSync(new com.example.uitpayapp.network.ApiCallback<String>() {
                        @Override
                        public void onSuccess(String data) {
                            runOnUiThread(() -> finish());
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> finish());
                        }
                    });
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(FoodCheckoutActivity.this, "Lỗi thanh toán: " + errorMessage + ". Vui lòng nạp thêm tiền!", Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
