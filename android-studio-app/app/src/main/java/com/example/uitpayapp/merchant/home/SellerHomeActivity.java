package com.example.uitpayapp.merchant.home;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.home_model.OrderItem;
import com.example.uitpayapp.merchant.home.home_model.SellerOrder;
import com.example.uitpayapp.merchant.marketing.SellerMarketingActivity;
import com.example.uitpayapp.merchant.notification.SellerNotificationActivity;
import com.example.uitpayapp.merchant.shop.SellerShopActivity;
import com.example.uitpayapp.modules.restaurant.RestaurantRepository;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.profile.ProfileWebView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.Calendar;
import java.util.List;

public class SellerHomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private SellerOrderViewModel viewModel;
    private TextView tvShopStatus;
    private RestaurantRepository restaurantRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.seller_activity_home);

        viewModel = new ViewModelProvider(this).get(SellerOrderViewModel.class);
        restaurantRepository = new RestaurantRepository();

        initViews();
        setupTabLayout();
        setupBottomNav();
        setupBadges();
        setupObservers();

        if (savedInstanceState == null) {
            replaceFragment(new NewOrdersFragment());
        }

        // Tải dữ liệu thực từ API
        viewModel.loadData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh khi quay lại màn hình
        viewModel.loadData();
        loadShopStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewModel != null) {
            viewModel.disconnectWebSocket();
        }
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        tvShopStatus = findViewById(R.id.tv_shop_status);
        View mainContainer = findViewById(R.id.seller_home_container);

        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvShopStatus.setOnClickListener(v ->
            startActivity(new Intent(this, SellerStatusActivity.class)));

        findViewById(R.id.seller_home_contact_support).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileWebView.class);
            intent.putExtra("URL_KEY", "https://merchant.shopeefood.vn/edu/collection/co-ban");
            startActivity(intent);
        });
        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_seller_home);swipeRefreshLayout.setOnRefreshListener(() -> {
            loadShopStatus();
            viewModel.loadData();
        });
        viewModel.getIsLoading().observe(this, isLoading -> swipeRefreshLayout.setRefreshing(isLoading));
    }

    private void loadShopStatus() {
        android.content.SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        long restaurantId = prefs.getLong("current_store_id", -1L);
        if (restaurantId == -1L) return;

        restaurantRepository.getRestaurantById(restaurantId, new ApiCallback<RestaurantResponseDTO>() {
            @Override
            public void onSuccess(RestaurantResponseDTO data) {
                runOnUiThread(() -> updateShopStatusUI(data));
            }

            @Override
            public void onError(String message) {
                // Ignore error in background update
            }
        });
    }

    private void updateShopStatusUI(RestaurantResponseDTO restaurant) {
        if (restaurant == null || tvShopStatus == null) return;

        boolean isAccepting = restaurant.getIsAcceptingOrders() != null && restaurant.getIsAcceptingOrders();
        boolean inHours = isCurrentTimeInOpenHours(restaurant.getOpenTime(), restaurant.getCloseTime());

        if (isAccepting && inHours) {
            tvShopStatus.setText("Mở");
            tvShopStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
            tvShopStatus.getBackground().setTint(Color.parseColor("#E8F5E9")); // Light Green
        } else {
            tvShopStatus.setText("Đóng");
            tvShopStatus.setTextColor(Color.parseColor("#B71C1C")); // Dark Red
            tvShopStatus.getBackground().setTint(Color.parseColor("#FFEBEE")); // Very Light Red
        }
    }

    private boolean isCurrentTimeInOpenHours(String openTime, String closeTime) {
        if (openTime == null || closeTime == null || openTime.isEmpty() || closeTime.isEmpty()) {
            return false;
        }
        try {
            String[] openParts = openTime.split(":");
            String[] closeParts = closeTime.split(":");
            if (openParts.length < 2 || closeParts.length < 2) return false;

            int openHour = Integer.parseInt(openParts[0]);
            int openMin = Integer.parseInt(openParts[1]);
            int closeHour = Integer.parseInt(closeParts[0]);
            int closeMin = Integer.parseInt(closeParts[1]);

            Calendar now = Calendar.getInstance();
            int nowHour = now.get(Calendar.HOUR_OF_DAY);
            int nowMin = now.get(Calendar.MINUTE);

            int nowVal = nowHour * 60 + nowMin;
            int openVal = openHour * 60 + openMin;
            int closeVal = closeHour * 60 + closeMin;

            if (closeVal < openVal) {
                return nowVal >= openVal || nowVal <= closeVal;
            } else {
                return nowVal >= openVal && nowVal <= closeVal;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setupObservers() {
        // Hiển thị lỗi dạng Toast
        viewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
        });

        // Hiển thị thông báo thành công
        viewModel.getSuccessMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) {
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: replaceFragment(new NewOrdersFragment()); break;
                    case 1: replaceFragment(new ConfirmedOrdersFragment()); break;
                    case 2: replaceFragment(new OrderHistoryFragment()); break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void setupBadges() {
        viewModel.getNewOrders().observe(this, list -> updateBadge(0, list.size()));
        viewModel.getConfirmedOrders().observe(this, list -> updateBadge(1, list.size()));
        viewModel.getHistoryOrders().observe(this, list -> updateBadge(2, list.size()));
    }

    private void updateBadge(int pos, int count) {
        TabLayout.Tab tab = tabLayout.getTabAt(pos);
        if (tab != null) {
            BadgeDrawable badge = tab.getOrCreateBadge();
            badge.setVisible(count > 0);
            badge.setNumber(count);
            badge.setBackgroundColor(Color.parseColor("#E53935"));
        }
    }

    public void showOrderDetailBottomSheet(SellerOrder order) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order_detail, null);
        dialog.setContentView(view);

        LinearLayout llItems = view.findViewById(R.id.ll_items_container);
        llItems.removeAllViews();

        for (OrderItem item : order.getDishes()) {
            View itemView = getLayoutInflater().inflate(R.layout.item_order_sub_item, llItems, false);
            ((TextView) itemView.findViewById(R.id.tv_sub_item_name)).setText(item.getQuantity() + " x " + item.getDishName());
            TextView tvPrice = itemView.findViewById(R.id.tv_sub_item_price);
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText(String.format("%,dđ", item.getPrice()));
            llItems.addView(itemView);
        }

        long totalAmount = 0;
        try {
            totalAmount = Long.parseLong(order.getTotalPrice().replaceAll("[^0-9]", ""));
        } catch (Exception ignored) {}

        long subtotal = totalAmount - order.getShippingFee() + order.getDiscountAmount();

        ((TextView) view.findViewById(R.id.tv_subtotal)).setText(String.format("%,dđ", subtotal));
        
        TextView tvDeliveryFee = view.findViewById(R.id.tv_delivery_fee);
        if (tvDeliveryFee != null) tvDeliveryFee.setText(String.format("%,dđ", order.getShippingFee()));

        TextView tvDiscount = view.findViewById(R.id.tv_discount);
        if (tvDiscount != null) tvDiscount.setText(String.format("-%,dđ", order.getDiscountAmount()));

        ((TextView) view.findViewById(R.id.tv_total_received)).setText(order.getTotalPrice());
        ((TextView) view.findViewById(R.id.tv_order_id)).setText(order.getId());
        
        TextView tvOrderTime = view.findViewById(R.id.tv_order_time);
        if (tvOrderTime != null) tvOrderTime.setText(order.getOrderTime());
        
        TextView tvPickupTime = view.findViewById(R.id.tv_pickup_time);
        if (tvPickupTime != null) tvPickupTime.setText(order.getPickupTime());

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());

        view.findViewById(R.id.btn_modify_order).setOnClickListener(v -> {
            dialog.dismiss();
            showEditOrderBottomSheet(order);
        });

        view.findViewById(R.id.btn_order_cancel).setOnClickListener(v -> {
            dialog.dismiss();
            showCancelOrderDialog(order);
        });

        TextView btnComplete = view.findViewById(R.id.btn_complete_order);
        String status = order.getStatus() != null ? order.getStatus().toUpperCase() : "";
        if ("PENDING".equals(status)) {
            btnComplete.setText("Xác nhận đơn");
        } else if ("PREPARING".equals(status)) {
            btnComplete.setText("Bàn giao cho Shipper");
        } else if ("DELIVERING".equals(status)) {
            btnComplete.setText("Hoàn thành");
        } else {
            btnComplete.setText("Đóng");
        }

        btnComplete.setOnClickListener(v -> {
            if ("PENDING".equals(status)) {
                viewModel.acceptOrder(order);
            } else if ("PREPARING".equals(status)) {
                viewModel.deliverOrder(order);
            } else if ("DELIVERING".equals(status)) {
                viewModel.completeOrder(order);
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    public void showEditOrderBottomSheet(SellerOrder order) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_edit_order, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_customer_name)).setText(order.getCustomerName());

        CheckBox cbWrongPrice = view.findViewById(R.id.cb_wrong_price);
        CheckBox cbOutOfStock = view.findViewById(R.id.cb_out_of_stock);
        View btnContinue = view.findViewById(R.id.btn_continue);

        // Hiển thị SĐT thực từ API, fallback về chuỗi rỗng nếu chưa có
        TextView tvPhone = view.findViewById(R.id.tv_customer_phone);
        String phone = (order.getCustomerPhone() != null && !order.getCustomerPhone().isEmpty())
                ? order.getCustomerPhone() : "Không có thông tin";
        tvPhone.setText(phone);

        android.widget.CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            boolean hasReason = cbWrongPrice.isChecked() || cbOutOfStock.isChecked();
            btnContinue.setEnabled(hasReason);
            btnContinue.setAlpha(hasReason ? 1.0f : 0.5f);
        };
        cbWrongPrice.setOnCheckedChangeListener(listener);
        cbOutOfStock.setOnCheckedChangeListener(listener);

        view.findViewById(R.id.cv_call_customer).setOnClickListener(v -> {
            String phoneNum = tvPhone.getText().toString();
            if (!phoneNum.isEmpty() && !phoneNum.equals("Không có thông tin")) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phoneNum));
                startActivity(intent);
            } else {
                Toast.makeText(this, "Không có số điện thoại khách hàng", Toast.LENGTH_SHORT).show();
            }
        });

        btnContinue.setOnClickListener(v -> {
            dialog.dismiss();
            showRemoveItemsBottomSheet(order);
        });

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            dialog.dismiss();
            showOrderDetailBottomSheet(order);
        });

        dialog.show();
    }

    public void showCancelOrderDialog(SellerOrder order) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_reject_reason, null);
        builder.setView(dialogView);

        AlertDialog alertDialog = builder.create();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText etReason = dialogView.findViewById(R.id.et_reject_reason);
        dialogView.findViewById(R.id.btn_confirm_reject).setOnClickListener(v -> {
            String reason = etReason.getText().toString().trim();
            if (reason.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập lý do hủy", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gọi API hủy đơn qua ViewModel
            viewModel.cancelOrder(order, reason);
            alertDialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.show();
    }

    public void showRemoveItemsBottomSheet(SellerOrder order) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_remove_items, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_customer_name)).setText(order.getCustomerName());
        ((TextView) view.findViewById(R.id.tv_order_id)).setText(order.getId());

        LinearLayout llItems = view.findViewById(R.id.ll_items_remove_container);
        refreshRemoveItemsList(order, llItems, dialog);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            dialog.dismiss();
            showEditOrderBottomSheet(order);
        });

        view.findViewById(R.id.btn_preview_order).setOnClickListener(v -> {
            // Lưu thay đổi: ViewModel đã cập nhật cục bộ, notify UI
            viewModel.notifyOrderUpdated();
            Toast.makeText(this, "Đã cập nhật đơn hàng", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void refreshRemoveItemsList(SellerOrder order, LinearLayout container, BottomSheetDialog dialog) {
        container.removeAllViews();
        List<OrderItem> dishes = order.getDishes();
        for (int i = 0; i < dishes.size(); i++) {
            OrderItem item = dishes.get(i);
            View itemView = getLayoutInflater().inflate(R.layout.item_edit_order_remove, container, false);
            ((TextView) itemView.findViewById(R.id.tv_item_name)).setText(item.getQuantity() + " x " + item.getDishName());
            ((TextView) itemView.findViewById(R.id.tv_item_price)).setText(String.format("%,dđ", item.getPrice()));

            int finalI = i;
            itemView.findViewById(R.id.tv_remove).setOnClickListener(v -> {
                if (dishes.size() <= 1) {
                    Toast.makeText(this, "Không thể xóa tất cả món, đơn phải có ít nhất 1 món!", Toast.LENGTH_SHORT).show();
                    return;
                }

                OrderItem itemToRemove = dishes.get(finalI);
                // Gọi ViewModel (sẽ tự quyết định API call hay local remove)
                viewModel.removeItemFromOrder(order, itemToRemove, () -> {
                    refreshRemoveItemsList(order, container, dialog);
                });
            });
            container.addView(itemView);
        }
    }

    private void setupBottomNav() {
        ImageView ivOrders = findViewById(R.id.iv_nav_orders);
        TextView tvOrders = findViewById(R.id.tv_nav_orders);
        ivOrders.setColorFilter(Color.parseColor("#f24405"));
        tvOrders.setTextColor(Color.parseColor("#f24405"));

        findViewById(R.id.navNotification).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerNotificationActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navShop).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerShopActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            overridePendingTransition(0, 0);
        });

        findViewById(R.id.navMarketing).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerMarketingActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
            overridePendingTransition(0, 0);
        });
    }
}
