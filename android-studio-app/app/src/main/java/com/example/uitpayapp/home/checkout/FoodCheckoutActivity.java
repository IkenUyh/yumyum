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
    private List<VoucherModel> selectedVouchers = new ArrayList<>();
    private long maxUsableCoins = 0;

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
    private long discountFromVoucher = 0;

    // Payment Selection Views
    private View layoutPayWallet, layoutPayCash;
    private RadioButton rbPayWallet, rbPayCash;
    private TextView tvWalletMethodBalance;
    private boolean isCashSelected = false;
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
        switchCoins.setOnCheckedChangeListener((buttonView, isChecked) -> fetchPreviewData());

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
        layoutPayCash = findViewById(R.id.layout_pay_cash);
        rbPayWallet = findViewById(R.id.rb_pay_wallet);
        rbPayCash = findViewById(R.id.rb_pay_cash);
        tvWalletMethodBalance = findViewById(R.id.tv_wallet_method_balance);

        layoutPayWallet.setOnClickListener(v -> {
            rbPayWallet.setChecked(true);
            rbPayCash.setChecked(false);
            isCashSelected = false;
        });

        layoutPayCash.setOnClickListener(v -> {
            rbPayWallet.setChecked(false);
            rbPayCash.setChecked(true);
            isCashSelected = true;
        });

        btnConfirmCheckout = findViewById(R.id.btn_confirm_checkout);
        btnConfirmCheckout.setOnClickListener(v -> processCheckout());

        // Init suggested items
        RecyclerView rvSuggested = findViewById(R.id.rv_suggested_items);
        rvSuggested.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadSuggestedItems(rvSuggested);
        loadLoyaltyCoins();
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
                
        loadAvailableVouchers();
    }
    
    private void loadAvailableVouchers() {
        new com.example.uitpayapp.voucher.VoucherRepository().getActiveVouchers(new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.voucher.VoucherResponseDTO>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.voucher.VoucherResponseDTO> data) {
                runOnUiThread(() -> {
                    if (data != null && !data.isEmpty()) {
                        int validVouchers = 0;
                        for (com.example.uitpayapp.voucher.VoucherResponseDTO v : data) {
                            if (subtotalAmount >= (v.getMinOrderValue() != null ? v.getMinOrderValue().longValue() : 0)) {
                                validVouchers++;
                            }
                        }
                        if (selectedVouchers.isEmpty() && validVouchers > 0) {
                            tvSelectedVoucher.setText("Có " + validVouchers + " mã giảm giá có thể dùng");
                            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#F57C00"));
                        } else if (selectedVouchers.isEmpty() && validVouchers == 0) {
                            tvSelectedVoucher.setText("Không có mã giảm giá khả dụng");
                            tvSelectedVoucher.setTextColor(android.graphics.Color.parseColor("#757575"));
                        }
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void loadLoyaltyCoins() {
        new com.example.uitpayapp.modules.loyalty.LoyaltyRepository().getMyLoyaltyInfo(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO data) {
                runOnUiThread(() -> {
                    maxUsableCoins = data.getCurrentPoints();
                    TextView tvCoinsTitle = findViewById(R.id.tv_coins_title);
                    TextView tvCoinsSubtitle = findViewById(R.id.tv_coins_subtitle);
                    
                    if (maxUsableCoins > 0) {
                        if (tvCoinsTitle != null) tvCoinsTitle.setText(String.format("Dùng %,d Xu", maxUsableCoins).replace(',', '.'));
                        if (tvCoinsSubtitle != null) tvCoinsSubtitle.setText(String.format("Giảm thêm %,dđ", maxUsableCoins).replace(',', '.'));
                        coinsDiscount = maxUsableCoins; // 1 coin = 1 vnd
                        switchCoins.setEnabled(true);
                    } else {
                        switchCoins.setEnabled(false);
                        if (tvCoinsTitle != null) tvCoinsTitle.setText("Dùng Xu");
                        if (tvCoinsSubtitle != null) tvCoinsSubtitle.setText("Không có Xu");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    switchCoins.setEnabled(false);
                    TextView tvCoinsTitle = findViewById(R.id.tv_coins_title);
                    TextView tvCoinsSubtitle = findViewById(R.id.tv_coins_subtitle);
                    if (tvCoinsTitle != null) tvCoinsTitle.setText("Dùng Xu");
                    if (tvCoinsSubtitle != null) tvCoinsSubtitle.setText("Không có Xu");
                });
            }
        });
    }

    private void loadSuggestedItems(RecyclerView rvSuggested) {
        if (restaurantId == null) return;
        new com.example.uitpayapp.modules.food.FoodRepository().getRestaurantMenu(restaurantId, new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.modules.food.models.responses.FoodResponse>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.modules.food.models.responses.FoodResponse> data) {
                runOnUiThread(() -> {
                    if (data != null && !data.isEmpty()) {
                        List<Long> cartFoodIds = new ArrayList<>();
                        for (CartItem item : cartManager.getCart()) {
                            if (item.getMenuItem() != null && item.getMenuItem().getId() != null) {
                                try {
                                    cartFoodIds.add(Long.parseLong(item.getMenuItem().getId()));
                                } catch (NumberFormatException e) {
                                    // Ignore
                                }
                            }
                        }
                        
                        List<Long> cartCategoryIds = new ArrayList<>();
                        for (com.example.uitpayapp.modules.food.models.responses.FoodResponse food : data) {
                            if (cartFoodIds.contains(food.getId()) && food.getCategoryId() != null) {
                                cartCategoryIds.add(food.getCategoryId());
                            }
                        }
                        
                        List<com.example.uitpayapp.modules.food.models.responses.FoodResponse> filteredData = new ArrayList<>();
                        for (com.example.uitpayapp.modules.food.models.responses.FoodResponse food : data) {
                            if (!cartFoodIds.contains(food.getId()) && cartCategoryIds.contains(food.getCategoryId())) {
                                filteredData.add(food);
                            }
                        }
                        
                        if (filteredData.isEmpty()) {
                            for (com.example.uitpayapp.modules.food.models.responses.FoodResponse food : data) {
                                if (!cartFoodIds.contains(food.getId())) {
                                    filteredData.add(food);
                                }
                            }
                        }
                        
                        java.util.Collections.shuffle(filteredData);
                        int count = Math.min(5, filteredData.size());
                        List<SuggestedItemAdapter.SuggestedItem> suggestedItems = new ArrayList<>();
                        for (int i = 0; i < count; i++) {
                            suggestedItems.add(new SuggestedItemAdapter.SuggestedItem(
                                    filteredData.get(i).getName(),
                                    filteredData.get(i).getPrice().longValue(),
                                    0
                            ));
                        }
                        rvSuggested.setAdapter(new SuggestedItemAdapter(suggestedItems));
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void updateTotals() {
        long discount = discountFromVoucher;

        long totalCoinsDiscount = 0;
        if (switchCoins.isChecked()) {
            totalCoinsDiscount = coinsDiscount;
            layoutCoinsDiscount.setVisibility(View.VISIBLE);
            tvCoinsDiscountAmount.setText("-" + String.format("%,dđ", totalCoinsDiscount).replace(',', '.'));
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
            if (!selectedVouchers.isEmpty()) {
                tvSelectedVoucher.setText(selectedVouchers.size() + " voucher áp dụng" + (rankDiscount > 0 ? " (+Hạng)" : ""));
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
        List<String> codes = new ArrayList<>();
        for (VoucherModel v : selectedVouchers) codes.add(v.getId());

        com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest request = new com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest(
                restaurantId != null ? restaurantId : 1L,
                addressId,
                "STANDARD",
                codes,
                session.getDeliveryLatitude(),
                session.getDeliveryLongitude(),
                session.getDeliveryAddressText(),
                etNote.getText().toString(),
                switchUtensils.isChecked(),
                switchCoins.isChecked(),
                isCashSelected ? "CASH" : "WALLET");

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
                            totalAmount = (long) data.getFinalTotal();
                            discountFromVoucher = (long) data.getTotalDiscountAmount() - rankDiscount;
                            if (switchCoins.isChecked()) {
                                long coinsUsed = ((long)data.getTotalDiscountAmount() - discountFromVoucher - rankDiscount);
                                coinsDiscount = coinsUsed > 0 ? coinsUsed : 0;
                            }
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

        new com.example.uitpayapp.voucher.VoucherRepository().getActiveVouchers(new com.example.uitpayapp.network.ApiCallback<List<com.example.uitpayapp.voucher.VoucherResponseDTO>>() {
            @Override
            public void onSuccess(List<com.example.uitpayapp.voucher.VoucherResponseDTO> data) {
                runOnUiThread(() -> {
                    List<VoucherModel> activeVouchers = new ArrayList<>();
                    for (com.example.uitpayapp.voucher.VoucherResponseDTO dto : data) {
                        String title = "SHIPPING_DISCOUNT".equals(dto.getType()) ? "Giảm ship " + dto.getDiscountPercent() + "%" : "Giảm đơn " + dto.getDiscountPercent() + "%";
                        String description = "Tối đa " + String.format("%,dđ", dto.getMaxDiscount().longValue()) + " đơn từ " + String.format("%,dđ", dto.getMinOrderValue().longValue());
                        activeVouchers.add(new VoucherModel(
                                dto.getCode(),
                                title,
                                description,
                                dto.getMaxDiscount() != null ? dto.getMaxDiscount().longValue() : 0,
                                dto.getMinOrderValue() != null ? dto.getMinOrderValue().longValue() : 0,
                                dto.getType()
                        ));
                    }

                    final VoucherAdapter[] adapterHolder = new VoucherAdapter[1];
                    VoucherAdapter adapter = new VoucherAdapter(activeVouchers, selectedVouchers, voucher -> {
                        if (subtotalAmount >= voucher.getMinOrderAmount()) {
                            boolean isSelected = false;
                            for (VoucherModel v : selectedVouchers) {
                                if (v.getId().equals(voucher.getId())) {
                                    isSelected = true;
                                    selectedVouchers.remove(v);
                                    break;
                                }
                            }
                            if (isSelected) {
                                Toast.makeText(FoodCheckoutActivity.this, "Đã bỏ chọn mã: " + voucher.getTitle(), Toast.LENGTH_SHORT).show();
                            } else {
                                // Auto-replace vouchers of the same type (only 1 shipping discount and 1 order discount allowed)
                                List<VoucherModel> toRemove = new ArrayList<>();
                                for (VoucherModel v : selectedVouchers) {
                                    if (v.getType() != null && v.getType().equals(voucher.getType())) {
                                        toRemove.add(v);
                                    }
                                }
                                selectedVouchers.removeAll(toRemove);

                                selectedVouchers.add(voucher);
                                Toast.makeText(FoodCheckoutActivity.this, "Đã áp dụng mã: " + voucher.getTitle(), Toast.LENGTH_SHORT).show();
                            }
                            if (adapterHolder[0] != null) {
                                adapterHolder[0].notifyDataSetChanged();
                            }
                            fetchPreviewData();
                        } else {
                            Toast.makeText(FoodCheckoutActivity.this, "Đơn hàng chưa đạt giá trị tối thiểu để dùng mã này!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    adapterHolder[0] = adapter;
                    rvVouchers.setAdapter(adapter);
                    bottomSheetDialog.show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> Toast.makeText(FoodCheckoutActivity.this, "Không thể tải mã giảm giá: " + errorMessage, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void processCheckout() {
        if (cartManager.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCashSelected) {
            executeConfirmCheckout();
        } else {
            // Thanh toán qua Ví nội bộ
            if (currentWalletBalance < totalAmount) {
                Toast.makeText(this, "Số dư ví không đủ! Vui lòng chọn Thanh toán tiền mặt hoặc nạp thêm tiền.", Toast.LENGTH_LONG)
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
        List<String> codes = new ArrayList<>();
        for (VoucherModel v : selectedVouchers) codes.add(v.getId());

        com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest request = new com.example.uitpayapp.modules.order.models.requests.CreateOrderRequest(
                restaurantId != null ? restaurantId : 1L,
                addressId,
                "STANDARD",
                codes,
                session.getDeliveryLatitude(),
                session.getDeliveryLongitude(),
                session.getDeliveryAddressText(),
                etNote.getText().toString(),
                switchUtensils.isChecked(),
                switchCoins.isChecked(),
                isCashSelected ? "CASH" : "WALLET");

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
                            intent.putExtra("KEY_ORDER_ID", String.valueOf(data.getId()));

                            long discountAmount = discountFromVoucher;
                            intent.putExtra("KEY_DISCOUNT", discountAmount);

                            startActivity(intent);

                            cartManager.clearCartSync(new com.example.uitpayapp.network.ApiCallback<String>() {
                                @Override
                                public void onSuccess(String data2) {
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
        loadCartData();
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
