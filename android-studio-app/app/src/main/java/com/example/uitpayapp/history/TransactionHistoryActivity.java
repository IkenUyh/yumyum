package com.example.uitpayapp.history;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private View layoutEmpty;
    private TextView tvEmptyTitle, tvEmptyDesc;
    private TextView tvFilterService, tvFilterStatus;
    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView recyclerView;
    private FoodOrderAdapter adapter;

    private List<FoodOrder> allOrders;
    private List<FoodOrder> displayOrders;

    // Các biến lưu trạng thái lọc hiện tại
    private String currentTab = "Lịch sử";
    private String currentService = "Tất cả";
    private String currentStatus = "Tất cả";
    private String currentQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history);

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            // Thiết lập phần padding bottom của bottom_container bằng chính độ cao thanh điều hướng
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // Ánh xạ View
        tabLayout = findViewById(R.id.layoutTabs);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyDesc = findViewById(R.id.tvEmptyDesc);
        tvFilterService = findViewById(R.id.tvFilterService);
        tvFilterStatus = findViewById(R.id.tvFilterStatus);
        etSearch = findViewById(R.id.etSearch);
        ivSearch = findViewById(R.id.ivSearch);
        recyclerView = findViewById(R.id.recyclerView);

        allOrders = new ArrayList<>();
        displayOrders = new ArrayList<>();

        adapter = new FoodOrderAdapter(displayOrders);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        createDummyFoodOrders();
        setupTabs();
        setupFilterMenus();
        setupSearchLogic();

        setupBottomNavigation();
        applyFilter();
    }

    private void createDummyFoodOrders() {
        allOrders.add(new FoodOrder("21056-633329498", "Hồng Trà Sữa Ba Cô Gái Tam Hảo -...", "Hồng Trà Bí Đao\nĐặc Biệt L", 21400, 1, "21/05/2026", "Hoàn thành", "Thức uống", android.R.drawable.ic_menu_report_image, true, "Lịch sử"));
        allOrders.add(new FoodOrder("21036-701339912", "TIỆM BÚN A NHỬU - BÚN THÁI, BÚN...", "Bún Chả Cá", 23000, 1, "21/03/2026", "Hoàn thành", "Đồ ăn", android.R.drawable.ic_menu_report_image, true, "Lịch sử"));
        // Thêm một đơn Đã hủy để kiểm chứng tính năng bộ lọc hoạt động chính xác
        allOrders.add(new FoodOrder("21099-112233445", "Cơm Tấm Long Xuyên - Nguyễn Văn Cừ", "Cơm Sườn Bì Chả", 45000, 1, "15/05/2026", "Đã hủy", "Đồ ăn", android.R.drawable.ic_menu_report_image, false, "Lịch sử"));
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Đang đến"));
        tabLayout.addTab(tabLayout.newTab().setText("Deal đã mua"));
        tabLayout.addTab(tabLayout.newTab().setText("Lịch sử"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Đánh giá"));
        tabLayout.addTab(tabLayout.newTab().setText("Đơn nhóm"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    currentTab = tab.getText().toString();
                    applyFilter();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    // Thiết lập Menu khi click vào Dịch vụ hoặc Trạng thái
    private void setupFilterMenus() {
        // Bộ lọc Dịch vụ
        tvFilterService.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Tất cả");
            popup.getMenu().add("Đồ ăn");
            popup.getMenu().add("Thức uống");
            popup.setOnMenuItemClickListener(item -> {
                currentService = item.getTitle().toString();
                tvFilterService.setText(currentService + " ∨");
                applyFilter();
                return true;
            });
            popup.show();
        });

        // Bộ lọc Trạng thái đơn hàng
        tvFilterStatus.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.getMenu().add("Tất cả");
            popup.getMenu().add("Hoàn thành");
            popup.getMenu().add("Đã hủy");
            popup.setOnMenuItemClickListener(item -> {
                currentStatus = item.getTitle().toString();
                tvFilterStatus.setText(currentStatus + " ∨");
                applyFilter();
                return true;
            });
            popup.show();
        });
    }

    // Xử lý bật tắt thanh tìm kiếm và bắt text thay đổi
    private void setupSearchLogic() {
        TextView tvTitle = findViewById(R.id.tvTitle);

        ivSearch.setOnClickListener(v -> {
            if (etSearch.getVisibility() == View.GONE) {
                tvTitle.setVisibility(View.GONE);
                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
                ivSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel); // Đổi thành nút X xóa/đóng
            } else {
                etSearch.setText("");
                currentQuery = "";
                etSearch.setVisibility(View.GONE);
                tvTitle.setVisibility(View.VISIBLE);
                ivSearch.setImageResource(android.R.drawable.ic_menu_search); // Trả lại icon kính lúp
                applyFilter();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                applyFilter();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    // Hàm cốt lõi: Kết hợp cả 4 bộ lọc (Tab + Dịch vụ + Trạng thái + Tìm kiếm)
    private void applyFilter() {
        displayOrders.clear();

        for (FoodOrder order : allOrders) {
            // 1. Kiểm tra Tab danh mục
            boolean matchTab = order.getCategory().equalsIgnoreCase(currentTab);

            // 2. Kiểm tra bộ lọc loại Dịch vụ
            boolean matchService = currentService.equals("Tất cả") ||
                    order.getService().equalsIgnoreCase(currentService);

            // 3. Kiểm tra bộ lọc Trạng thái đơn hàng
            boolean matchStatus = currentStatus.equals("Tất cả") ||
                    order.getStatus().equalsIgnoreCase(currentStatus);

            // 4. Kiểm tra từ khóa tìm kiếm (theo Tên quán hoặc Tên món ăn)
            boolean matchSearch = currentQuery.isEmpty() ||
                    order.getMerchantName().toLowerCase().contains(currentQuery.toLowerCase()) ||
                    order.getItemName().toLowerCase().contains(currentQuery.toLowerCase());

            if (matchTab && matchService && matchStatus && matchSearch) {
                displayOrders.add(order);
            }
        }

        adapter.setData(displayOrders);

        // Quản lý hiển thị Layout trống (Empty View) linh hoạt theo từng tình huống
        if (displayOrders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);

            if (currentTab.equals("Đang đến")) {
                tvEmptyTitle.setText("Quên chưa đặt món rồi nè bạn ơi?");
                tvEmptyDesc.setText("Bạn sẽ nhìn thấy các món đang được chuẩn bị hoặc giao đi tại đây!");
            } else {
                tvEmptyTitle.setText("Không tìm thấy đơn hàng");
                tvEmptyDesc.setText("Thử thay đổi từ khóa tìm kiếm hoặc điều chỉnh lại bộ lọc xem sao nhé.");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }


    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navNotification = findViewById(R.id.navNotification);
        LinearLayout navAccount = findViewById(R.id.navAccount);
        LinearLayout navFavorite = findViewById(R.id.navFavorite);
        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.home.HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        navFavorite.setOnClickListener(v->{
            Intent intent = new Intent(this, com.example.uitpayapp.favorite.FavoriteActivity.class);
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

        navAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.profile.ProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }
}