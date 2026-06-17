package com.example.uitpayapp.favorite;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FavoriteActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView tvFilterService;
    private View layoutEmptyState;
    private RecyclerView rvMainFavorite;
    private FavoriteMainAdapter mainAdapter;

    private List<FavoriteShop> baseShops;        // Danh sách gốc từ DB
    private List<FavoriteShop> filteredShops;    // Danh sách hiển thị sau bộ lọc dọc
    private List<FavoriteShop> topOrderedShops;  // Danh sách lướt ngang

    private String currentCategory = "Dịch vụ";
    private boolean isNearMeActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        // Đẩy toàn bộ bottom container lên phía trên thanh điều hướng hệ thống
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        tabLayout = findViewById(R.id.layoutTabs);
        tvFilterService = findViewById(R.id.tvFilterService);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        rvMainFavorite = findViewById(R.id.rvMainFavorite);

        loadDatabaseShops();

        filteredShops = new ArrayList<>(baseShops);
        mainAdapter = new FavoriteMainAdapter(filteredShops, topOrderedShops);
        rvMainFavorite.setAdapter(mainAdapter);
        rvMainFavorite.setLayoutManager(new LinearLayoutManager(this));

        setupTabs();
        setupDropdownFilters();
        applyFilterAndSorting();
        setupBottomNavigation();
    }

    private void loadDatabaseShops() {
        baseShops = new ArrayList<>();
        topOrderedShops = new ArrayList<>();

        // Thêm dữ liệu mô phỏng quán ăn
        baseShops.add(new FavoriteShop("1", "Cơm Chiên Dương Châu & Mì Trộn Tóp Mỡ", 4.7, 3.5, 27, android.R.drawable.ic_menu_report_image, "Mã giảm 19%", "Đồ ăn", 200));
        baseShops.add(new FavoriteShop("2", "Banchan House - Quán Ăn Hàn Quốc", 4.4, 0.4, 22, android.R.drawable.ic_menu_report_image, "Mã giảm 19%", "Đồ ăn", 180));
        baseShops.add(new FavoriteShop("3", "TIỆM BÚN A NHỬU - BÚN THÁI", 4.5, 1.2, 15, android.R.drawable.ic_menu_report_image, "Flash Sale", "Đồ ăn", 140));
        baseShops.add(new FavoriteShop("4", "Siêu Thị Lotte Mart - Nguyễn Hữu Thọ", 4.6, 2.1, 30, android.R.drawable.ic_menu_report_image, "Mã giảm 10%", "Siêu thị", 40));

        // Nạp riêng các quán có lượt đặt cao vào mục Ngang
        for (FavoriteShop shop : baseShops) {
            if (shop.getOrderCount() >= 100) {
                topOrderedShops.add(shop);
            }
        }
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Mới nhất"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Gần tôi"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                isNearMeActive = tab.getPosition() == 1; // Tab 1 là tab "Gần tôi"
                applyFilterAndSorting();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupDropdownFilters() {
        tvFilterService.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Dịch vụ"); // Hiển thị tất cả
            popup.getMenu().add("Đồ ăn");
            popup.getMenu().add("Thực phẩm");
            popup.getMenu().add("Rượu bia");
            popup.getMenu().add("Hoa");
            popup.getMenu().add("Siêu thị");

            popup.setOnMenuItemClickListener(item -> {
                currentCategory = item.getTitle().toString();
                tvFilterService.setText(currentCategory + " ∨");
                applyFilterAndSorting();
                return true;
            });
            popup.show();
        });
    }

    private void applyFilterAndSorting() {
        filteredShops.clear();

        // 1. Thực hiện Lọc theo danh mục dịch vụ chọn từ Dropdown (Ảnh 2)
        for (FavoriteShop shop : baseShops) {
            if (currentCategory.equals("Dịch vụ") || shop.getServiceType().equalsIgnoreCase(currentCategory)) {
                filteredShops.add(shop);
            }
        }

        // 2. Thực hiện Sắp xếp theo khoảng cách nếu đang ở tab Gần tôi (Ảnh 4)
        if (isNearMeActive) {
            Collections.sort(filteredShops, (s1, s2) -> Double.compare(s1.getDistance(), s2.getDistance()));
        }

        mainAdapter.notifyDataSetChanged();

        // 3. Kiểm tra rỗng để hiển thị xe đẩy trống (Ảnh 1)
        if (filteredShops.isEmpty() && topOrderedShops.isEmpty()) {
            rvMainFavorite.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvMainFavorite.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navHistory = findViewById(R.id.navHistory);
        LinearLayout navAccount = findViewById(R.id.navAccount);
        LinearLayout navNotification = findViewById(R.id.navNotification);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.home.HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        navNotification.setOnClickListener(v->{
            Intent intent = new Intent(this, com.example.uitpayapp.notification.NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.history.TransactionHistoryActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.profile.ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
    }

    private void updateNotificationBadge() {
        final TextView tvNotificationBadge = findViewById(R.id.tv_notification_badge);
        if (tvNotificationBadge == null) return;
        
        com.example.uitpayapp.modules.notification.NotificationRepository repo = 
                new com.example.uitpayapp.modules.notification.NotificationRepository();
        repo.getUnreadCount(new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Long>>() {
            @Override
            public void onSuccess(java.util.Map<String, Long> countData) {
                long unreadCount = countData != null && countData.containsKey("unreadCount") ? countData.get("unreadCount") : 0;
                if (unreadCount > 0) {
                    tvNotificationBadge.setText(String.valueOf(unreadCount));
                    tvNotificationBadge.setVisibility(View.VISIBLE);
                } else {
                    tvNotificationBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Fail silently
            }
        });
    }
}