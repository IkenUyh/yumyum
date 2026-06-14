package com.example.uitpayapp.merchant.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.home.home_model.OrderItem;
import com.example.uitpayapp.merchant.home.home_model.SellerHistoryOrder;
import com.example.uitpayapp.merchant.home.home_model.SellerOrder;
import com.example.uitpayapp.merchant.shop.SellerShopActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SellerHomeActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private LinearLayout llHistoryFilters;
    private TextView tvFilterAll, tvFilterConfirmed, tvFilterCancelled;
    private EditText etSearch;
    private View navOrders, navNotification, navShop, navMarketing;

    private SellerOrderAdapter orderAdapter;
    private SellerHistoryOrderAdapter historyAdapter;

    private List<SellerOrder> newOrders = new ArrayList<>();
    private List<SellerOrder> confirmedOrders = new ArrayList<>();
    private List<SellerHistoryOrder> historyOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seller_activity_home);

        initViews();
        setupData();
        setupTabLayout();
        setupFilters();
        setupBottomNav();
        showNewOrders();
    }

    private void initViews() {
        View mainContainer = findViewById(R.id.seller_home_container);
        tabLayout = findViewById(R.id.tab_layout);
        rvOrders = findViewById(R.id.rv_orders);
        llHistoryFilters = findViewById(R.id.ll_history_filters);
        tvFilterAll = findViewById(R.id.tv_filter_all);
        tvFilterConfirmed = findViewById(R.id.tv_filter_confirmed);
        tvFilterCancelled = findViewById(R.id.tv_filter_cancelled);
        etSearch = findViewById(R.id.et_seller_search_order);

        navOrders = findViewById(R.id.navOrders);
        navNotification = findViewById(R.id.navNotification);
        navShop = findViewById(R.id.navShop);
        navMarketing = findViewById(R.id.navMarketing);

        rvOrders.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top;
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), safeBottomPadding);
            return insets;
        });
    }

    private void setupTabLayout() {
        updateBadge(0);
        updateBadge(1);
        updateBadge(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        showNewOrders();
                        break;
                    case 1:
                        showConfirmedOrders();
                        break;
                    case 2:
                        showHistory();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupFilters() {
        tvFilterAll.setOnClickListener(v -> filterHistory("all"));
        tvFilterConfirmed.setOnClickListener(v -> filterHistory("confirmed"));
        tvFilterCancelled.setOnClickListener(v -> filterHistory("cancelled"));
    }

    private void filterHistory(String type) {
        List<SellerHistoryOrder> filteredList = new ArrayList<>();
        
        // Reset all filter UI
        tvFilterAll.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvFilterAll.setTextColor(Color.WHITE);
        tvFilterConfirmed.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvFilterConfirmed.setTextColor(Color.WHITE);
        tvFilterCancelled.setBackgroundResource(R.drawable.bg_tab_unselected);
        tvFilterCancelled.setTextColor(Color.WHITE);

        switch (type) {
            case "all":
                filteredList.addAll(historyOrders);
                tvFilterAll.setBackgroundResource(R.drawable.bg_tab_selected);
                tvFilterAll.setTextColor(Color.parseColor("#f24405"));
                break;
            case "confirmed":
                for (SellerHistoryOrder order : historyOrders) {
                    if ("Đã giao".equalsIgnoreCase(order.getStatus()) || "Đang giao".equalsIgnoreCase(order.getStatus())) {
                        filteredList.add(order);
                    }
                }
                tvFilterConfirmed.setBackgroundResource(R.drawable.bg_tab_selected);
                tvFilterConfirmed.setTextColor(Color.parseColor("#f24405"));
                break;
            case "cancelled":
                for (SellerHistoryOrder order : historyOrders) {
                    if ("Đã hủy".equalsIgnoreCase(order.getStatus())) {
                        filteredList.add(order);
                    }
                }
                tvFilterCancelled.setBackgroundResource(R.drawable.bg_tab_selected);
                tvFilterCancelled.setTextColor(Color.parseColor("#f24405"));
                break;
        }

        historyAdapter = new SellerHistoryOrderAdapter(this, filteredList);
        rvOrders.setAdapter(historyAdapter);
    }

    private void updateBadge(int pos) {
        if (tabLayout.getTabAt(pos) != null) {
            BadgeDrawable badge = tabLayout.getTabAt(pos).getOrCreateBadge();
            badge.setVisible(true);
            if (pos == 0) {
                badge.setNumber(newOrders.size());
            } else if (pos == 1) {
                badge.setNumber(confirmedOrders.size());
            } else
                badge.setNumber(historyOrders.size());
            badge.setBackgroundColor(Color.parseColor("#E53935"));
        }
    }

    private void showNewOrders() {
        llHistoryFilters.setVisibility(View.GONE);
        orderAdapter = new SellerOrderAdapter(this, newOrders, new SellerOrderAdapter.OnOrderActionListener() {
            @Override
            public void onAccept(SellerOrder order) {
                Toast.makeText(SellerHomeActivity.this, "Xác nhận đơn của: " + order.getCustomerName(), Toast.LENGTH_SHORT).show();
                order.setStatus("confirmed");
                confirmedOrders.add(order);
                newOrders.remove(order);
                orderAdapter.notifyDataSetChanged();
                updateBadge(1);
                updateBadge(0);
            }

            @Override
            public void onSeeMore(SellerOrder order) {
                showOrderDetailBottomSheet(order);
            }
        });
        rvOrders.setAdapter(orderAdapter);
    }

    private void showConfirmedOrders() {
        llHistoryFilters.setVisibility(View.GONE);
        orderAdapter = new SellerOrderAdapter(this, confirmedOrders, new SellerOrderAdapter.OnOrderActionListener() {
            @Override
            public void onAccept(SellerOrder order) {
                moveToHistory(order);
            }

            @Override
            public void onSeeMore(SellerOrder order) {
                showOrderDetailBottomSheet(order);
            }
        });
        rvOrders.setAdapter(orderAdapter);
    }

    private void moveToHistory(SellerOrder order) {
        Toast.makeText(this, "Đã hoàn tất đơn của: " + order.getCustomerName(), Toast.LENGTH_SHORT).show();

        SellerHistoryOrder historyOrder = new SellerHistoryOrder(
                "ORD-" + System.currentTimeMillis() % 10000,
                order.getCustomerName(),
                "Đang giao",
                "Vừa xong",
                order.getNumberOfDishes(),
                "0.1 km",
                "Hôm nay",
                "Đang giao",
                order.getTotalPrice()
        );

        historyOrders.add(0, historyOrder);
        confirmedOrders.remove(order);

        if (orderAdapter != null) {
            orderAdapter.notifyDataSetChanged();
        }

        updateBadge(1);
        updateBadge(2);
    }

    private void showOrderDetailBottomSheet(SellerOrder order) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order_detail, null);
        dialog.setContentView(view);

        LinearLayout llItems = view.findViewById(R.id.ll_items_container);
        llItems.removeAllViews();

        long subtotal = 0;
        for (OrderItem item : order.getDishes()) {
            View itemView = getLayoutInflater().inflate(R.layout.item_order_sub_item, llItems, false);
            TextView tvName = itemView.findViewById(R.id.tv_sub_item_name);
            TextView tvPrice = itemView.findViewById(R.id.tv_sub_item_price);

            tvName.setText(item.getQuantity() + " x " + item.getDishName());
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText(String.format("%,dđ", item.getPrice()));
            
            llItems.addView(itemView);
            subtotal += (item.getPrice() * item.getQuantity());
        }

        ((TextView) view.findViewById(R.id.tv_subtotal)).setText(String.format("%,dđ", subtotal));
        ((TextView) view.findViewById(R.id.tv_total_received)).setText(order.getTotalPrice());

        view.findViewById(R.id.btn_close).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.btn_modify_order).setOnClickListener(v -> {
            dialog.dismiss();
            showEditOrderBottomSheet(order);
        });

        view.findViewById(R.id.btn_complete_order).setOnClickListener(v -> {
            if ("confirmed".equalsIgnoreCase(order.getStatus())) {
                moveToHistory(order);
            } else {
                order.setStatus("confirmed");
                confirmedOrders.add(order);
                newOrders.remove(order);
                if (orderAdapter != null) orderAdapter.notifyDataSetChanged();
                updateBadge(0);
                updateBadge(1);
                Toast.makeText(this, "Đã xác nhận đơn hàng!", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showEditOrderBottomSheet(SellerOrder order) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_edit_order, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_customer_name)).setText(order.getCustomerName());
        ((TextView) view.findViewById(R.id.tv_customer_phone)).setText("+0327187310");

        view.findViewById(R.id.btn_back).setOnClickListener(v -> {
            dialog.dismiss();
            showOrderDetailBottomSheet(order);
        });

        view.findViewById(R.id.btn_continue).setOnClickListener(v -> {
            Toast.makeText(this, "Yêu cầu chỉnh sửa đã được gửi", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showHistory() {
        llHistoryFilters.setVisibility(View.VISIBLE);
        filterHistory("all");
    }

    private void setupBottomNav() {
        ImageView ivOrders = findViewById(R.id.iv_nav_orders);
        TextView tvOrders = findViewById(R.id.tv_nav_orders);
        ivOrders.setColorFilter(Color.parseColor("#f24405"));
        tvOrders.setTextColor(Color.parseColor("#f24405"));

        navShop.setOnClickListener(v -> {
            Intent intent = new Intent(this, SellerShopActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navNotification.setOnClickListener(v -> Toast.makeText(this, "Thông báo", Toast.LENGTH_SHORT).show());
        navMarketing.setOnClickListener(v -> Toast.makeText(this, "Marketing", Toast.LENGTH_SHORT).show());
    }

    private void setupData() {
        List<OrderItem> dishes1 = new ArrayList<>();
        dishes1.add(new OrderItem(1, "Lý Trà Chanh", 35000));
        dishes1.add(new OrderItem(1, "Trà Sữa Phúc Bồn Tử", 35000));
        newOrders.add(new SellerOrder("Van A", "", 2, "78.000đ", "new", "Vui lòng chuẩn bị món ăn nhanh hơn nữa", dishes1));

        List<OrderItem> dishes2 = new ArrayList<>();
        dishes2.add(new OrderItem(1, "Bún Trấu Phúc Bồn Hạnh Phúc", 80000));
        newOrders.add(new SellerOrder("Van B", "", 1, "80.000đ", "new", "Giao hàng cẩn thận", dishes2));

        List<OrderItem> dishes3 = new ArrayList<>();
        dishes3.add(new OrderItem(1, "Trà Đào", 46500));
        confirmedOrders.add(new SellerOrder("Thi C", "", 1, "46.500đ", "confirmed", "Không đường", dishes3));

        historyOrders.add(new SellerHistoryOrder("02", "Thi C", "Đã giao", "17:46", 1, "0.1 km", "25/04/2024", "18:18", "46.500đ"));
        historyOrders.add(new SellerHistoryOrder("02", "Thi C", "Đã giao", "13:18", 1, "0.1 km", "25/04/2024", "13:18", "16.000đ"));
    }
}
