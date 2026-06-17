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

import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.home_model.OrderItem;
import com.example.uitpayapp.merchant.home.home_model.SellerHistoryOrder;
import com.example.uitpayapp.merchant.home.home_model.SellerOrder;
import com.example.uitpayapp.merchant.marketing.SellerMarketingActivity;
import com.example.uitpayapp.merchant.notification.SellerNotificationActivity;
import com.example.uitpayapp.merchant.shop.SellerShopActivity;
import com.example.uitpayapp.profile.ProfileWebView;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class SellerHomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private SellerOrderViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.seller_activity_home);

        viewModel = new ViewModelProvider(this).get(SellerOrderViewModel.class);
        viewModel.loadData();

        initViews();
        setupTabLayout();
        setupBottomNav();
        setupBadges();

        if (savedInstanceState == null) {
            replaceFragment(new NewOrdersFragment());
        }
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tab_layout);
        View mainContainer = findViewById(R.id.seller_home_container);

        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        findViewById(R.id.tv_shop_status).setOnClickListener(v -> 
            startActivity(new Intent(this, SellerStatusActivity.class)));
        findViewById(R.id.seller_home_contact_support).setOnClickListener(v ->{
            Intent intent = new Intent(this, ProfileWebView.class);
            intent.putExtra("URL_KEY","https://merchant.shopeefood.vn/edu/collection/co-ban");
            startActivity(intent);
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

        long subtotal = 0;
        for (OrderItem item : order.getDishes()) {
            View itemView = getLayoutInflater().inflate(R.layout.item_order_sub_item, llItems, false);
            ((TextView) itemView.findViewById(R.id.tv_sub_item_name)).setText(item.getQuantity() + " x " + item.getDishName());
            TextView tvPrice = itemView.findViewById(R.id.tv_sub_item_price);
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText(String.format("%,dđ", item.getPrice()));
            llItems.addView(itemView);
            subtotal += ((long) item.getPrice() * item.getQuantity());
        }

        ((TextView) view.findViewById(R.id.tv_subtotal)).setText(String.format("%,dđ", subtotal));
        ((TextView) view.findViewById(R.id.tv_total_received)).setText(order.getTotalPrice());
        ((TextView) view.findViewById(R.id.tv_order_id)).setText(order.getId());

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
        if ("new".equalsIgnoreCase(order.getStatus())) {
            btnComplete.setText("Xác nhận đơn");
        } else {
            btnComplete.setText("Đóng");
        }

        btnComplete.setOnClickListener(v -> {
            if ("new".equalsIgnoreCase(order.getStatus())) {
                viewModel.acceptOrder(order);
                Toast.makeText(this, "Đã xác nhận đơn hàng", Toast.LENGTH_SHORT).show();
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
        TextView tvPhone = view.findViewById(R.id.tv_customer_phone);
        tvPhone.setText("0327187310");

        android.widget.CompoundButton.OnCheckedChangeListener listener = (buttonView, isChecked) -> {
            boolean hasReason = cbWrongPrice.isChecked() || cbOutOfStock.isChecked();
            btnContinue.setEnabled(hasReason);
            btnContinue.setAlpha(hasReason ? 1.0f : 0.5f);
        };
        cbWrongPrice.setOnCheckedChangeListener(listener);
        cbOutOfStock.setOnCheckedChangeListener(listener);

        view.findViewById(R.id.cv_call_customer).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + tvPhone.getText().toString()));
            startActivity(intent);
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
            
            SellerHistoryOrder historyOrder = new SellerHistoryOrder(
                    order.getId(), order.getCustomerName(), "Đã hủy", "Vừa xong", 
                    order.getNumberOfDishes(), "0.0 km", "Hôm nay", "Bây giờ", order.getTotalPrice()
            );
            viewModel.cancelOrder(historyOrder, order);
            
            Toast.makeText(this, "Đã hủy đơn hàng", Toast.LENGTH_SHORT).show();
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
        refreshRemoveItemsList(order, llItems);

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            dialog.dismiss();
            showEditOrderBottomSheet(order);
        });

        view.findViewById(R.id.btn_preview_order).setOnClickListener(v -> {
            viewModel.notifyOrderUpdated();
            Toast.makeText(this, "Đã cập nhật đơn hàng", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void refreshRemoveItemsList(SellerOrder order, LinearLayout container) {
        container.removeAllViews();
        List<OrderItem> dishes = order.getDishes();
        for (int i = 0; i < dishes.size(); i++) {
            OrderItem item = dishes.get(i);
            View itemView = getLayoutInflater().inflate(R.layout.item_edit_order_remove, container, false);
            ((TextView) itemView.findViewById(R.id.tv_item_name)).setText(item.getQuantity() + " x " + item.getDishName());
            ((TextView) itemView.findViewById(R.id.tv_item_price)).setText(String.format("%,dđ", item.getPrice()));

            int finalI = i;
            itemView.findViewById(R.id.tv_remove).setOnClickListener(v -> {
                dishes.remove(finalI);

                long newTotal = 0;
                for (OrderItem oi : dishes) {
                    newTotal += (long) oi.getPrice() * oi.getQuantity();
                }
                order.setTotalPrice(String.format("%,dđ", newTotal));
                order.setNumberOfDishes(dishes.size());
                
                refreshRemoveItemsList(order, container);
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
