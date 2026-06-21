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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.example.uitpayapp.home.HomeActivity;
import com.example.uitpayapp.home.home_models.Restaurant;
import com.example.uitpayapp.modules.favorite.FavoriteRepository;
import com.example.uitpayapp.modules.favorite.models.FavoriteRestaurantResponseDTO;
import com.example.uitpayapp.network.ApiCallback;

public class FavoriteActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView tvFilterService;
    private View layoutEmptyState;
    private RecyclerView rvMainFavorite;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FavoriteMainAdapter mainAdapter;

    private List<FavoriteShop> baseShops; // Danh sách gốc từ DB
    private List<FavoriteShop> filteredShops; // Danh sách hiển thị sau bộ lọc dọc
    private List<FavoriteShop> topOrderedShops; // Danh sách lướt ngang

    private FavoriteRepository favoriteRepository;
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutHeader), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        tabLayout = findViewById(R.id.layoutTabs);
        tvFilterService = findViewById(R.id.tvFilterService);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        rvMainFavorite = findViewById(R.id.rvMainFavorite);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        favoriteRepository = new FavoriteRepository();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadDatabaseShops();
            swipeRefreshLayout.setRefreshing(false);
        });

        baseShops = new ArrayList<>();
        topOrderedShops = new ArrayList<>();
        filteredShops = new ArrayList<>();
        mainAdapter = new FavoriteMainAdapter(filteredShops, topOrderedShops, shop -> {
            favoriteRepository.toggleFavorite(Long.parseLong(shop.getId()), new ApiCallback<com.example.uitpayapp.modules.favorite.models.ToggleFavoriteResponseDTO>() {
                @Override
                public void onSuccess(com.example.uitpayapp.modules.favorite.models.ToggleFavoriteResponseDTO result) {
                    shop.setFavorited(result.isFavorited());
                    applyFilterAndSorting();
                }

                @Override
                public void onError(String errorMessage) {
                    android.widget.Toast.makeText(FavoriteActivity.this, "Không thể bỏ yêu thích: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });
        rvMainFavorite.setAdapter(mainAdapter);
        rvMainFavorite.setLayoutManager(new LinearLayoutManager(this));

        loadDatabaseShops();

        setupTabs();
        setupDropdownFilters();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
        if (mainAdapter != null) {
            loadDatabaseShops();
        }
    }

    private void loadDatabaseShops() {
        if (!com.example.uitpayapp.network.SessionManager.getInstance(this).isLoggedIn()) {
            applyFilterAndSorting();
            return;
        }
        if (baseShops == null) {
            baseShops = new ArrayList<>();
        } else {
            baseShops.clear();
        }

        if (topOrderedShops == null) {
            topOrderedShops = new ArrayList<>();
        } else {
            topOrderedShops.clear();
        }

        favoriteRepository.getMyFavorites(new ApiCallback<List<FavoriteRestaurantResponseDTO>>() {
            @Override
            public void onSuccess(List<FavoriteRestaurantResponseDTO> favorites) {
                if (favorites != null) {
                    for (int i = 0; i < favorites.size(); i++) {
                        FavoriteRestaurantResponseDTO dto = favorites.get(i);
                        
                        // Try to map to the local resource ID if the name matches our mock list
                        int localResId = 0;
                        String name = dto.getRestaurantName();
                        if (name.contains("KFC")) localResId = 0;
                        else if (name.contains("Phúc Long")) localResId = 0;
                        else if (name.contains("Coffee House")) localResId = 0;
                        else if (name.contains("Jollibee")) localResId = 0;
                        else if (name.contains("Highlands")) localResId = 0;
                        else localResId = 0; // fallback default
                        
                        // Use distance & delivery time from API if available, fallback otherwise
                        double distance;
                        if (dto.getDistance() != null) {
                            distance = dto.getDistance();
                        } else {
                            distance = 0.5 + (Math.random() * 5.0);
                            distance = Math.round(distance * 10.0) / 10.0;
                        }

                        int deliveryTime;
                        if (dto.getDeliveryTime() != null) {
                            deliveryTime = dto.getDeliveryTime();
                        } else {
                            deliveryTime = 15 + (i * 5) % 20;
                        }

                        int orderCount = dto.getReviewCount() != null ? dto.getReviewCount() : (50 + (i * 25));
                        
                        FavoriteShop fs = new FavoriteShop(
                                String.valueOf(dto.getRestaurantId()),
                                dto.getRestaurantName(),
                                dto.getRatingAverage() != null ? dto.getRatingAverage().doubleValue() : 4.5,
                                distance,
                                deliveryTime,
                                localResId,
                                "Mã giảm 15%",
                                dto.getRestaurantName().contains("Trà sữa") || dto.getRestaurantName().contains("Coffee") ? "Cafe" : "Cơm",
                                orderCount
                        );
                        fs.setImageUrl(dto.getRestaurantImageUrl());
                        baseShops.add(fs);

                        if (orderCount >= 100) {
                            topOrderedShops.add(fs);
                        }
                    }
                }
                applyFilterAndSorting();
            }

            @Override
            public void onError(String errorMessage) {
                android.widget.Toast.makeText(FavoriteActivity.this, "Không thể tải danh sách yêu thích: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                applyFilterAndSorting();
            }
        });
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
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupDropdownFilters() {
        java.util.List<String> categories = new java.util.ArrayList<>(java.util.Arrays.asList("Tất cả", "Cơm", "Bánh Mì", "Phở & Bún", "Gà Rán & Fastfood", "Trà Sữa", "Cà Phê & Đồ Uống", "Ăn Vặt", "Món Nhật & Hàn", "Ý & Pizza", "Lẩu & Dimsum", "Đồ Nướng & BBQ", "Hải Sản"));

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, categories);

        com.example.uitpayapp.modules.food.CategoryRepository categoryRepository = new com.example.uitpayapp.modules.food.CategoryRepository();
        categoryRepository.getAllCategories(new com.example.uitpayapp.network.ApiCallback<java.util.List<com.example.uitpayapp.modules.food.models.responses.CategoryResponse>>() {
            @Override
            public void onSuccess(java.util.List<com.example.uitpayapp.modules.food.models.responses.CategoryResponse> result) {
                categories.clear();
                categories.add("Tất cả");
                for (com.example.uitpayapp.modules.food.models.responses.CategoryResponse cat : result) {
                    categories.add(cat.getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String errorMessage) {
            }
        });

        android.widget.ListPopupWindow listPopupWindow = new android.widget.ListPopupWindow(this);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(tvFilterService);
        listPopupWindow.setWidth((int) (220 * getResources().getDisplayMetrics().density));
        listPopupWindow.setHeight((int) (200 * getResources().getDisplayMetrics().density));

        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            currentCategory = categories.get(position);
            if (currentCategory.equals("Tất cả")) {
                tvFilterService.setText("Tất cả ▼");
            } else {
                tvFilterService.setText(currentCategory + " ▼");
            }
            applyFilterAndSorting();
            listPopupWindow.dismiss();
        });

        tvFilterService.setOnClickListener(v -> {
            listPopupWindow.show();
        });
        tvFilterService.setText("Tất cả ▼");
    }

    private void applyFilterAndSorting() {
        // Xóa hẳn các shop đã bị unfavorite khi chuyển tab hoặc filter
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            baseShops.removeIf(shop -> !shop.isFavorited());
            topOrderedShops.removeIf(shop -> !shop.isFavorited());
        } else {
            java.util.Iterator<FavoriteShop> it = baseShops.iterator();
            while (it.hasNext()) {
                if (!it.next().isFavorited())
                    it.remove();
            }
            java.util.Iterator<FavoriteShop> it2 = topOrderedShops.iterator();
            while (it2.hasNext()) {
                if (!it2.next().isFavorited())
                    it2.remove();
            }
        }

        filteredShops.clear();

        // 1. Thực hiện Lọc theo danh mục dịch vụ chọn từ Dropdown (Ảnh 2)
        for (FavoriteShop shop : baseShops) {
            if (currentCategory.equals("Dịch vụ") || currentCategory.equals("Tất cả")
                    || shop.getServiceType().equalsIgnoreCase(currentCategory)) {
                filteredShops.add(shop);
            }
        }

        // 2. Thực hiện Sắp xếp theo khoảng cách nếu đang ở tab Gần tôi (Ảnh 4)
        if (isNearMeActive) {
            Collections.sort(filteredShops, (s1, s2) -> Double.compare(s1.getDistance(), s2.getDistance()));
        }

        mainAdapter.notifyDataSetChanged();

        // 3. Kiểm tra rỗng để hiển thị xe đẩy trống (Ảnh 1)
        // 3. Kiểm tra rỗng để hiển thị xe đẩy trống (Ảnh 1)
        if (!com.example.uitpayapp.network.SessionManager.getInstance(this).isLoggedIn()) {
            rvMainFavorite.setVisibility(android.view.View.GONE);
            layoutEmptyState.setVisibility(android.view.View.VISIBLE);
            android.widget.TextView tvTitle = findViewById(R.id.tvEmptyTitle);
            android.widget.TextView tvDesc = findViewById(R.id.tvEmptyDesc);
            if (tvTitle != null) tvTitle.setText("Vui lòng đăng nhập");
            if (tvDesc != null) tvDesc.setText("Đăng nhập để xem danh sách quán yêu thích của bạn.");
        } else if (filteredShops.isEmpty() && topOrderedShops.isEmpty()) {
            rvMainFavorite.setVisibility(android.view.View.GONE);
            layoutEmptyState.setVisibility(android.view.View.VISIBLE);
            android.widget.TextView tvTitle = findViewById(R.id.tvEmptyTitle);
            android.widget.TextView tvDesc = findViewById(R.id.tvEmptyDesc);
            if (tvTitle != null) tvTitle.setText("Chưa có nhà hàng yêu thích");
            if (tvDesc != null) tvDesc.setText("Bạn chưa thả tim nhà hàng nào");
        } else {
            rvMainFavorite.setVisibility(android.view.View.VISIBLE);
            layoutEmptyState.setVisibility(android.view.View.GONE);
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
        navNotification.setOnClickListener(v -> {
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
    /*
     * @Override
     * protected void onResume() {
     * super.onResume();
     * updateNotificationBadge();
     * }
     */

    private void updateNotificationBadge() {
        final TextView tvNotificationBadge = findViewById(R.id.tv_notification_badge);
        if (tvNotificationBadge == null)
            return;

        com.example.uitpayapp.modules.notification.NotificationRepository repo = new com.example.uitpayapp.modules.notification.NotificationRepository();
        repo.getUnreadCount(new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Long>>() {
            @Override
            public void onSuccess(java.util.Map<String, Long> countData) {
                long unreadCount = countData != null && countData.containsKey("unreadCount")
                        ? countData.get("unreadCount")
                        : 0;
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