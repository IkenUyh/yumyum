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
import android.widget.RadioButton;

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
    private long rankDiscount = 0;

    // Payment Selection Views
    private View layoutPayWallet, layoutPayZaloPay;
    private RadioButton rbPayWallet, rbPayZaloPay;
    private TextView tvWalletMethodBalance;
    private boolean isZaloPaySelected = false;
    private long currentWalletBalance = 0;

    private Long restaurantId = null;
    private Long addressId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_checkout);
        getWindow().setStatusBarColor(android.graphics.Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        cartManager = CartManager.getInstance();

        initViews();
        loadCartData();
        initAddress(); // Dynamic loading of user address
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

        CardView cardDeliveryAddress = findViewById(R.id.card_delivery_address);
        if (cardDeliveryAddress != null) {
            cardDeliveryAddress.setOnClickListener(v -> selectDeliveryAddress());
        }

        // Bind Payment Selector Views
        layoutPayWallet = findViewById(R.id.layout_pay_wallet);
        layoutPayZaloPay = findViewById(R.id.layout_pay_zalopay);
        rbPayWallet = findViewById(R.id.rb_pay_wallet);
        rbPayZaloPay = findViewById(R.id.rb_pay_zalopay);
        tvWalletMethodBalance = findViewById(R.id.tv_wallet_method_balance);

        layoutPayWallet.setOnClickListener(v -> {
            rbPayWallet.setChecked(true);
            rbPayZaloPay.setChecked(false);
            isZaloPaySelected = false;
        });

        layoutPayZaloPay.setOnClickListener(v -> {
            rbPayWallet.setChecked(false);
            rbPayZaloPay.setChecked(true);
            isZaloPaySelected = true;
        });

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

        if (cartItems != null && !cartItems.isEmpty()) {
            for (CartItem item : cartItems) {
                if (item.getMenuItem() != null && item.getMenuItem().getRestaurantId() != null) {
                    restaurantId = item.getMenuItem().getRestaurantId();
                    break;
                }
            }
        }
        if (restaurantId == null) {
            restaurantId = 1L; // fallback
        }

        // Fetch wallet balance
        TextView tvWalletBalance = findViewById(R.id.tv_wallet_balance);
        com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
        walletRepo.getBalance(
                new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse>() {
                    @Override
                    public void onSuccess(com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse data) {
                        runOnUiThread(() -> {
                            java.text.NumberFormat format = java.text.NumberFormat
                                    .getInstance(new java.util.Locale("vi", "VN"));
                            String balanceFormatted = format.format(data.getBalance()) + "đ";
                            if (tvWalletBalance != null) {
                                tvWalletBalance.setText(balanceFormatted);
                            }
                            if (tvWalletMethodBalance != null) {
                                tvWalletMethodBalance.setText("Số dư: " + balanceFormatted);
                            }
                            currentWalletBalance = data.getBalance().longValue();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            if (tvWalletBalance != null) {
                                tvWalletBalance.setText("Lỗi lấy số dư");
                            }
                            if (tvWalletMethodBalance != null) {
                                tvWalletMethodBalance.setText("Lỗi lấy số dư");
                            }
                        });
                    }
                });
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

        long totalDiscount = discount + totalCoinsDiscount + rankDiscount;
        // Ensure total doesn't go below 0
        if (totalDiscount > subtotalAmount + deliveryFee) {
            totalDiscount = subtotalAmount + deliveryFee;
        }

        totalAmount = subtotalAmount + deliveryFee - totalDiscount;

        tvSubtotal.setText(String.format("%,dđ", subtotalAmount).replace(',', '.'));
        tvDeliveryFee.setText(String.format("%,dđ", deliveryFee).replace(',', '.'));
        btnConfirmCheckout.setText("Đặt đơn - " + String.format("%,dđ", totalAmount).replace(',', '.'));

        if (discount > 0 || rankDiscount > 0) {
            layoutDiscount.setVisibility(View.VISIBLE);
            tvDiscountAmount.setText("-" + String.format("%,dđ", discount + rankDiscount).replace(',', '.'));
            if (selectedVoucher != null) {
                tvSelectedVoucher.setText(selectedVoucher.getTitle() + (rankDiscount > 0 ? " (+Hạng)" : ""));
            } else {
                tvSelectedVoucher.setText("Ưu đãi Hạng Thành viên");
            }
            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#388E3C"));
        } else {
            layoutDiscount.setVisibility(View.GONE);
            tvSelectedVoucher.setText("Chọn hoặc nhập mã ưu đãi");
            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#757575"));
        }
    }

    private void fetchPreviewData() {
        com.example.uitpayapp.network.SessionManager session = com.example.uitpayapp.network.SessionManager.getInstance(this);
        if (addressId == null && session.getDeliveryAddressText() == null) {
            runOnUiThread(() -> tvDeliveryFee.setText("Chưa chọn địa chỉ"));
            return;
        }
        com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest request = new com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest(
                restaurantId != null ? restaurantId : 1L,
                addressId,
                "STANDARD",
                new ArrayList<>(),
                session.getDeliveryLatitude(),
                session.getDeliveryLongitude(),
                session.getDeliveryAddressText());

        com.example.uitpayapp.modules.order.OrderRepository orderRepo = new com.example.uitpayapp.modules.order.OrderRepository();
        orderRepo.previewOrder(request,
                new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.order.models.responses.OrderPreviewResponse>() {
                    @Override
                    public void onSuccess(
                            com.example.uitpayapp.modules.order.models.responses.OrderPreviewResponse data) {
                        runOnUiThread(() -> {
                            deliveryFee = (long) data.getShippingFee();
                            subtotalAmount = (long) data.getFoodTotal();
                            rankDiscount = (long) data.getRankDiscount();
                            updateTotals();
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(FoodCheckoutActivity.this, "Không thể tính phí ship: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
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

        // Lọc voucher đủ điều kiện (tuỳ chọn, ở đây mình cho hiện hết nhưng cảnh báo
        // nếu không đủ điều kiện)
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

        if (isZaloPaySelected) {
            // Thanh toán qua ZaloPay
            android.app.ProgressDialog progress = new android.app.ProgressDialog(this);
            progress.setMessage("Đang tạo giao dịch ZaloPay...");
            progress.setCancelable(false);
            progress.show();

            com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
            walletRepo.createZaloPayTopUp(totalAmount,
                    new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Object>>() {
                        @Override
                        public void onSuccess(java.util.Map<String, Object> data) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                if (data != null && data.containsKey("order_url") && data.containsKey("app_trans_id")) {
                                    String orderUrl = (String) data.get("order_url");
                                    String appTransId = (String) data.get("app_trans_id");

                                    // Lưu app_trans_id vào SharedPreferences để kiểm tra khi quay lại app
                                    getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                                            .putString("PENDING_CHECKOUT_ZALOPAY_TRANS_ID", appTransId)
                                            .apply();

                                    // Mở ZaloPay hoặc trình duyệt thanh toán
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                            android.net.Uri.parse(orderUrl));
                                    startActivity(browserIntent);
                                } else {
                                    Toast.makeText(FoodCheckoutActivity.this,
                                            "Không lấy được thông tin thanh toán ZaloPay", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                Toast.makeText(FoodCheckoutActivity.this, "Lỗi tạo đơn ZaloPay: " + errorMessage,
                                        Toast.LENGTH_LONG).show();
                            });
                        }
                    });
        } else {
            // Thanh toán qua Ví nội bộ
            if (currentWalletBalance < totalAmount) {
                Toast.makeText(this, "Số dư ví không đủ! Vui lòng chọn ZaloPay hoặc nạp thêm tiền.", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            executeConfirmCheckout();
        }
    }

    private void executeConfirmCheckout() {
        com.example.uitpayapp.network.SessionManager session = com.example.uitpayapp.network.SessionManager.getInstance(this);
        if (addressId == null && session.getDeliveryAddressText() == null) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng trước!", Toast.LENGTH_SHORT).show();
            return;
        }
        String productNames = cartManager.getProductSummary();
        com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest request = new com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest(
                restaurantId != null ? restaurantId : 1L,
                addressId,
                "STANDARD",
                new ArrayList<>(),
                session.getDeliveryLatitude(),
                session.getDeliveryLongitude(),
                session.getDeliveryAddressText());

        com.example.uitpayapp.modules.order.OrderRepository orderRepo = new com.example.uitpayapp.modules.order.OrderRepository();
        orderRepo.createOrder(request,
                new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.order.models.responses.OrderResponse>() {
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
                            Toast.makeText(FoodCheckoutActivity.this, "Lỗi đặt đơn: " + errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        });
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload wallet balance to get the most updated balance
        loadCartData();

        android.content.SharedPreferences paymentPrefs = getSharedPreferences("PaymentPrefs", MODE_PRIVATE);
        String pendingTransId = paymentPrefs.getString("PENDING_CHECKOUT_ZALOPAY_TRANS_ID", null);
        if (pendingTransId != null) {
            showZaloPayCheckStatusDialog(pendingTransId);
        }
    }

    private void showZaloPayCheckStatusDialog(String appTransId) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Đang chờ thanh toán");
        builder.setMessage(
                "Vui lòng hoàn tất thanh toán trên ZaloPay. Sau khi thanh toán xong, hãy bấm nút dưới đây để hoàn tất đặt đơn hàng!");
        builder.setCancelable(false);

        builder.setPositiveButton("Xác nhận đã thanh toán", null);
        builder.setNegativeButton("Hủy bỏ", (dialog, which) -> {
            getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                    .remove("PENDING_CHECKOUT_ZALOPAY_TRANS_ID")
                    .apply();
            dialog.dismiss();
            Toast.makeText(this, "Đã hủy giao dịch thanh toán ZaloPay", Toast.LENGTH_SHORT).show();
        });

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            android.app.ProgressDialog progress = new android.app.ProgressDialog(this);
            progress.setMessage("Đang kiểm tra giao dịch...");
            progress.setCancelable(false);
            progress.show();

            com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
            walletRepo.queryZaloPayOrderStatus(appTransId,
                    new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Object>>() {
                        @Override
                        public void onSuccess(java.util.Map<String, Object> data) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                if (data != null && data.containsKey("return_code")) {
                                    int returnCode = ((Number) data.get("return_code")).intValue();
                                    if (returnCode == 1) {
                                        getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                                                .remove("PENDING_CHECKOUT_ZALOPAY_TRANS_ID")
                                                .apply();
                                        dialog.dismiss();
                                        Toast.makeText(FoodCheckoutActivity.this, "Thanh toán ZaloPay thành công!",
                                                Toast.LENGTH_SHORT).show();
                                        executeConfirmCheckout();
                                    } else {
                                        String msg = data.containsKey("return_message")
                                                ? (String) data.get("return_message")
                                                : "Chưa hoàn tất thanh toán";
                                        Toast.makeText(FoodCheckoutActivity.this, "Giao dịch chưa hoàn tất: " + msg,
                                                Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(FoodCheckoutActivity.this, "Không thể xác định trạng thái giao dịch",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> {
                                progress.dismiss();
                                Toast.makeText(FoodCheckoutActivity.this, "Lỗi kiểm tra trạng thái: " + errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
        });
    }

    private void initAddress() {
        com.example.uitpayapp.network.SessionManager session = com.example.uitpayapp.network.SessionManager
                .getInstance(this);
        addressId = session.getDeliveryAddressId();
        String addressText = session.getDeliveryAddressText();

        if (addressText != null) {
            String name = session.getUserName();
            String phone = session.getUserPhone();
            updateAddressUI(name, phone, addressText);
            fetchPreviewData();
        } else {
            new com.example.uitpayapp.modules.user.AddressRepository().getMyAddresses(
                    new com.example.uitpayapp.network.ApiCallback<java.util.List<com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO>>() {
                        @Override
                        public void onSuccess(
                                java.util.List<com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO> result) {
                            if (result != null && !result.isEmpty()) {
                                com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO defaultAddress = result
                                        .get(0);
                                for (com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO addr : result) {
                                    if (addr.getIsDefault() != null && addr.getIsDefault()) {
                                        defaultAddress = addr;
                                        break;
                                    }
                                }
                                final com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO selected = defaultAddress;
                                session.saveDeliveryAddress(selected.getId(), selected.getDetailedAddress());
                                if (selected.getLatitude() != null && selected.getLongitude() != null) {
                                    session.saveDeliveryCoordinates(selected.getLatitude().doubleValue(), selected.getLongitude().doubleValue());
                                }
                                runOnUiThread(() -> {
                                    addressId = selected.getId();
                                    updateAddressUI(selected.getRecipientName(), selected.getPhoneNumber(),
                                            selected.getDetailedAddress());
                                    fetchPreviewData();
                                });
                            } else {
                                runOnUiThread(() -> {
                                    updateAddressUI(null, null,
                                            "Chưa có địa chỉ giao hàng. Vui lòng bấm để chọn/thêm!");
                                });
                            }
                        }

                        @Override
                        public void onError(String errorMessage) {
                            runOnUiThread(() -> {
                                updateAddressUI(null, null, "Không thể tải địa chỉ giao hàng: " + errorMessage);
                            });
                        }
                    });
        }
    }

    private void updateAddressUI(String recipient, String phone, String detailedAddress) {
        TextView tvAddressDesc = findViewById(R.id.tv_delivery_address_desc);
        if (tvAddressDesc != null) {
            String displayText = "";
            if (recipient != null && !recipient.isEmpty()) {
                displayText += recipient;
            }
            if (phone != null && !phone.isEmpty()) {
                if (!displayText.isEmpty())
                    displayText += " | ";
                displayText += phone;
            }
            if (detailedAddress != null && !detailedAddress.isEmpty()) {
                if (!displayText.isEmpty())
                    displayText += "\n";
                displayText += detailedAddress;
            }
            tvAddressDesc.setText(displayText.trim().isEmpty() ? "Bấm để chọn địa điểm giao hàng" : displayText.trim());
        }
    }

    private void selectDeliveryAddress() {
        com.example.uitpayapp.network.SessionManager session = com.example.uitpayapp.network.SessionManager
                .getInstance(this);
        new com.example.uitpayapp.modules.user.AddressRepository().getMyAddresses(
                new com.example.uitpayapp.network.ApiCallback<java.util.List<com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO>>() {
                    @Override
                    public void onSuccess(
                            java.util.List<com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO> result) {
                        runOnUiThread(() -> {
                            com.example.uitpayapp.utils.AddressBottomSheetHelper.showAddressBottomSheet(
                                    FoodCheckoutActivity.this,
                                    result,
                                    addressId,
                                    selectedAddress -> {
                                        addressId = selectedAddress.getId();
                                        session.saveDeliveryAddress(selectedAddress.getId(),
                                                selectedAddress.getDetailedAddress());
                                        if (selectedAddress.getLatitude() != null && selectedAddress.getLongitude() != null) {
                                            session.saveDeliveryCoordinates(selectedAddress.getLatitude().doubleValue(), selectedAddress.getLongitude().doubleValue());
                                        }
                                        updateAddressUI(selectedAddress.getRecipientName(),
                                                selectedAddress.getPhoneNumber(), selectedAddress.getDetailedAddress());
                                        fetchPreviewData();
                                    });
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            Toast.makeText(FoodCheckoutActivity.this,
                                    "Không thể lấy danh sách địa chỉ: " + errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }
}
