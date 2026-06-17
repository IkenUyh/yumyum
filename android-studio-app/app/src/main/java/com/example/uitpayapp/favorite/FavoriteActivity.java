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

public class FavoriteActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private TextView tvFilterService;
    private View layoutEmptyState;
    private RecyclerView rvMainFavorite;
    private SwipeRefreshLayout swipeRefreshLayout;
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

        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadDatabaseShops();
            applyFilterAndSorting();
            swipeRefreshLayout.setRefreshing(false);
        });

        loadDatabaseShops();

        filteredShops = new ArrayList<>(baseShops);
        mainAdapter = new FavoriteMainAdapter(filteredShops, topOrderedShops, shop -> {
            shop.setFavorited(!shop.isFavorited());
            mainAdapter.notifyDataSetChanged();
        });
        rvMainFavorite.setAdapter(mainAdapter);
        rvMainFavorite.setLayoutManager(new LinearLayoutManager(this));

        setupTabs();
        setupDropdownFilters();
        applyFilterAndSorting();
        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mainAdapter != null) {
            loadDatabaseShops();
            applyFilterAndSorting();
        }
    }

    private void loadDatabaseShops() {
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

        List<Restaurant> restaurants = HomeActivity.HomeRepository.getInstance().getRestaurants();
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant r = restaurants.get(i);
            
            // Generate a random distance since Restaurant doesn't have it
            double distance = 0.5 + (Math.random() * 5.0);
            distance = Math.round(distance * 10.0) / 10.0;
            
            int orderCount = 50 + (i * 20); // Giả lập lượt đặt
            
            FavoriteShop fs = new FavoriteShop(
                String.valueOf(i), 
                r.getName(), 
                r.getRating(), 
                distance, 
                r.getDeliveryTime(), 
                r.getImageResId(), 
                "Mã giảm " + (10 + (i % 3) * 5) + "%", 
                r.getCategory(), 
                orderCount
            );
            baseShops.add(fs);
            
            if (orderCount >= 100) {
                topOrderedShops.add(fs);
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
        String[] categories = {"Tất cả", "Cơm", "Bún Phở", "Bánh mì", "Fastfood", "Lẩu", "Đồ nướng", "Cafe", "Trà sữa", "Ăn vặt", "Tráng miệng", "Hải sản", "Chay", "Đồ uống", "Gà rán", "Pizza"};
        
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        
        android.widget.ListPopupWindow listPopupWindow = new android.widget.ListPopupWindow(this);
        listPopupWindow.setAdapter(adapter);
        listPopupWindow.setAnchorView(tvFilterService);
        listPopupWindow.setWidth((int) (150 * getResources().getDisplayMetrics().density));
        listPopupWindow.setHeight((int) (200 * getResources().getDisplayMetrics().density));
        
        listPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            currentCategory = categories[position];
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
                if (!it.next().isFavorited()) it.remove();
            }
            java.util.Iterator<FavoriteShop> it2 = topOrderedShops.iterator();
            while (it2.hasNext()) {
                if (!it2.next().isFavorited()) it2.remove();
            }
        }

        filteredShops.clear();

        // 1. Thực hiện Lọc theo danh mục dịch vụ chọn từ Dropdown (Ảnh 2)
        for (FavoriteShop shop : baseShops) {
            if (currentCategory.equals("Dịch vụ") || currentCategory.equals("Tất cả") || shop.getServiceType().equalsIgnoreCase(currentCategory)) {
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
}