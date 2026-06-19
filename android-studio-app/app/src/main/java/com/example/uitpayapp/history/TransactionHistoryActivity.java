package com.example.uitpayapp.history;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.graphics.Insets;
import androidx.activity.OnBackPressedCallback;
import android.view.MotionEvent;
import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.order.OrderRepository;
import com.example.uitpayapp.modules.order.models.responses.OrderResponse;
import com.example.uitpayapp.network.ApiCallback;
import com.google.android.material.tabs.TabLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private View layoutEmpty;
    private TextView tvEmptyTitle, tvEmptyDesc;
    private TextView tvFilterService, tvFilterStatus, tvFilterDate;
    private EditText etSearch;
    private ImageView ivSearch;
    private RecyclerView recyclerView;
    private FoodOrderAdapter adapter;

    private LinearLayout layoutCoinsBanner;
    private LinearLayout layoutFilters;
    private LinearLayout layoutRecommendations;
    private RecyclerView rvRecommendations;

    // Khai báo các thành phần của Bộ Lịch mới
    private androidx.constraintlayout.widget.ConstraintLayout layoutCalendarOverlay;
    private RecyclerView rvCalendarGrid;
    private CalendarAdapter calendarAdapter;
    private ImageButton btnDismissCalendar;
    private Button btnClearDateFilter, btnApplyDateFilter;

    private View layoutLoading;
    private android.widget.ImageView ivEmpty;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefreshLayout;
    private boolean isErrorState = false;
    private String currentErrorMessage = "";
    private boolean isLoadingData = false;

    private List<FoodOrder> allOrders;
    private List<FoodOrder> displayOrders;

    private String currentTab = "Đang đến";
    private String currentService = "Tất cả";
    private String currentStatus = "Tất cả";
    private String currentQuery = "";

    // Lưu trữ bộ lọc khoảng thời gian đang active (Mặc định null là không lọc)
    private Date filterStartDate = null;
    private Date filterEndDate = null;
    // Thêm vào cùng các biến toàn cục hiện tại của bạn
    private RecyclerView rvDeals;
    private DealHistoryAdapter dealAdapter;
    private List<DealHistory> allDealsList;
    private OrderRepository orderRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutHeader), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // Ánh xạ hệ thống cũ
        tabLayout = findViewById(R.id.layoutTabs);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptyDesc = findViewById(R.id.tvEmptyDesc);
        tvFilterService = findViewById(R.id.tvFilterService);
        tvFilterStatus = findViewById(R.id.tvFilterStatus);
        tvFilterDate = findViewById(R.id.tvFilterDate);
        etSearch = findViewById(R.id.etSearch);
        ivSearch = findViewById(R.id.ivSearch);
        recyclerView = findViewById(R.id.recyclerView);
        layoutCoinsBanner = findViewById(R.id.layoutCoinsBanner);
        layoutFilters = findViewById(R.id.layoutFilters);
        layoutRecommendations = findViewById(R.id.layoutRecommendations);
        rvRecommendations = findViewById(R.id.rvRecommendations);

        // Ánh xạ hệ thống Lịch mới thêm
        layoutCalendarOverlay = findViewById(R.id.layoutCalendarOverlay);
        rvCalendarGrid = findViewById(R.id.rvCalendarGrid);
        btnDismissCalendar = findViewById(R.id.btnDismissCalendar);
        btnClearDateFilter = findViewById(R.id.btnClearDateFilter);
        btnApplyDateFilter = findViewById(R.id.btnApplyDateFilter);
        layoutLoading = findViewById(R.id.layoutLoading);
        ivEmpty = findViewById(R.id.ivEmpty);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        allOrders = new ArrayList<>();
        displayOrders = new ArrayList<>();

        adapter = new FoodOrderAdapter(displayOrders);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(order -> {
            Intent intent = new Intent(TransactionHistoryActivity.this, OrderDetailActivity.class);
            intent.putExtra("ORDER_ID", order.getOrderId());
            intent.putExtra("ORDER_STATUS", order.getStatus());
            startActivity(intent);
        });

        // Tìm đến cuối hàm onCreate() và bổ sung đoạn này:
        rvDeals = findViewById(R.id.rvDeals);
        rvDeals.setLayoutManager(new LinearLayoutManager(this));

        allDealsList = new ArrayList<>();

        dealAdapter = new DealHistoryAdapter(allDealsList);
        rvDeals.setAdapter(dealAdapter);

        // Khởi chạy cài đặt thành phần
        setupRecommendations();
        setupCalendarSystem(); // Khởi tạo lưới lịch

        orderRepository = new OrderRepository();
        fetchOrdersFromBackend();
        setupTabs();
        setupFilterMenus();
        setupSearchLogic();
        setupBottomNavigation();
        applyFilter();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchOrdersFromBackend();
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (etSearch.getVisibility() == View.VISIBLE) {
                    closeSearch();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void closeSearch() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        etSearch.setText("");
        currentQuery = "";
        etSearch.setVisibility(View.GONE);
        tvTitle.setVisibility(View.VISIBLE);
        ivSearch.setImageResource(R.drawable.ic_search);
        applyFilter();
    }

    private void setupCalendarSystem() {
        List<CalendarItem> calendarData = generateCalendarData();
        calendarAdapter = new CalendarAdapter(calendarData);

        // Thiết lập Grid 7 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 7);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // Tiêu đề tháng chiếm trọn 7 cột, ô ngày/thứ chiếm 1 cột
                return calendarData.get(position).getType() == CalendarItem.TYPE_MONTH_HEADER ? 7 : 1;
            }
        });

        rvCalendarGrid.setLayoutManager(gridLayoutManager);
        rvCalendarGrid.setAdapter(calendarAdapter);

        // Bật màn hình lịch khi click dòng ngày tháng
        tvFilterDate.setOnClickListener(v -> layoutCalendarOverlay.setVisibility(View.VISIBLE));
        btnDismissCalendar.setOnClickListener(v -> layoutCalendarOverlay.setVisibility(View.GONE));

        // NÚT XÓA FILTER NGÀY THÁNG
        btnClearDateFilter.setOnClickListener(v -> {
            filterStartDate = null;
            filterEndDate = null;
            calendarAdapter.clearSelection();
            tvFilterDate.setText("Tất cả thời gian ∨");
            layoutCalendarOverlay.setVisibility(View.GONE);
            applyFilter();
        });

        // NÚT LƯU CHỌN FILTER NGÀY THÁNG
        btnApplyDateFilter.setOnClickListener(v -> {
            if (calendarAdapter.getStartDate() == null) {
                Toast.makeText(this, "Vui lòng chọn ngày để bắt đầu lọc!", Toast.LENGTH_SHORT).show();
                return;
            }
            filterStartDate = calendarAdapter.getStartDate();
            filterEndDate = calendarAdapter.getEndDate() != null ? calendarAdapter.getEndDate() : filterStartDate;

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
            tvFilterDate.setText(sdf.format(filterStartDate) + " - " + sdf.format(filterEndDate) + " ∨");

            layoutCalendarOverlay.setVisibility(View.GONE);
            applyFilter(); // Kích hoạt bộ lọc
        });
    }

    // THUẬT TOÁN SINH MẢNG LỊCH TỰ ĐỘNG GIỚI HẠN THÁNG 6/2025 -> THÁNG 12/2026
    private List<CalendarItem> generateCalendarData() {
        List<CalendarItem> list = new ArrayList<>();
        String[] weekDays = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};

        Calendar current = Calendar.getInstance();
        current.set(2025, Calendar.JUNE, 1, 0, 0, 0); // Điểm quá khứ xa nhất
        current.set(Calendar.MILLISECOND, 0);

        Calendar endBound = Calendar.getInstance();
        endBound.set(2026, Calendar.DECEMBER, 31, 23, 59, 59); // Điểm tương lai xa nhất

        String[] monthNames = {"Tháng Một", "Tháng Hai", "Tháng Ba", "Tháng Tư", "Tháng Năm", "Tháng Sáu",
                "Tháng Bảy", "Tháng Tám", "Tháng Chín", "Tháng Mười", "Tháng Mười Một", "Tháng Mười Hai"};

        while (current.before(endBound)) {
            int year = current.get(Calendar.YEAR);
            int month = current.get(Calendar.MONTH);

            // 1. Thêm hàng tiêu đề tháng
            list.add(new CalendarItem(CalendarItem.TYPE_MONTH_HEADER, monthNames[month] + " " + year, null, false));

            // 2. Thêm hàng tiêu đề thứ
            for (String day : weekDays) {
                list.add(new CalendarItem(CalendarItem.TYPE_WEEK_HEADER, day, null, false));
            }

            // Tính toán độ lệch ô đệm đầu tháng
            Calendar monthCal = Calendar.getInstance();
            monthCal.set(year, month, 1);
            int firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK); // 1 = CN, 2 = T2...
            for (int i = 1; i < firstDayOfWeek; i++) {
                list.add(new CalendarItem(CalendarItem.TYPE_DAY, "", null, true));
            }

            // 3. Đổ các ngày thực tế của tháng vào lưới
            int daysInMonth = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int i = 1; i <= daysInMonth; i++) {
                Calendar dayCal = Calendar.getInstance();
                dayCal.set(year, month, i, 0, 0, 0);
                dayCal.set(Calendar.MILLISECOND, 0);
                list.add(new CalendarItem(CalendarItem.TYPE_DAY, String.valueOf(i), dayCal.getTime(), false));
            }

            // Tịnh tiến sang tháng tiếp theo
            current.add(Calendar.MONTH, 1);
        }
        return list;
    }

    // THUẬT TOÁN CHUYỂN ĐỔI CHUỖI ĐƠN HÀNG MOCK ĐỂ SO SÁNH TIMESTAMPS CHÍNH XÁC
    private Date parseOrderDate(String dateStr) {
        try {
            if (dateStr.contains("Hôm nay")) {
                Calendar cal = Calendar.getInstance(); // Ngày 09/06/2026 hiện tại
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                return cal.getTime();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return new Date();
        }
    }

    private void applyFilter() {
        displayOrders.clear();

        if ("Đang đến".equalsIgnoreCase(currentTab)) {
            layoutCoinsBanner.setVisibility(View.GONE);
            layoutFilters.setVisibility(View.GONE);
            layoutRecommendations.setVisibility(View.GONE); // Đã ẩn theo yêu cầu

            recyclerView.setVisibility(View.VISIBLE);
            rvDeals.setVisibility(View.GONE); // Ẩn danh sách Deal
            layoutEmpty.setVisibility(View.GONE);
        }
        else if ("Deal đã mua".equalsIgnoreCase(currentTab)) {
            // Đúng theo ảnh mẫu 1: Tab Deal chỉ hiện xu thưởng, ẩn hoàn toàn bộ lọc Dịch vụ/Trạng thái/Ngày
            layoutCoinsBanner.setVisibility(View.VISIBLE);
            layoutFilters.setVisibility(View.GONE);
            layoutRecommendations.setVisibility(View.GONE);

            recyclerView.setVisibility(View.GONE); // Ẩn danh sách đơn hàng lịch sử
            if (allDealsList.isEmpty()) {
                rvDeals.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                if (isErrorState) {
                    ivEmpty.setImageResource(R.drawable.ic_internet);
                    tvEmptyTitle.setText("Lỗi tải dữ liệu");
                    tvEmptyDesc.setText(currentErrorMessage);
                } else {
                    ivEmpty.setImageResource(R.drawable.img_transactionhistory_notransaction);
                    tvEmptyTitle.setText("Chưa có deal nào");
                    tvEmptyDesc.setText("Bạn chưa mua deal nào gần đây!");
                }
            } else {
                rvDeals.setVisibility(View.VISIBLE);
                layoutEmpty.setVisibility(View.GONE);
            }
            return; // Ngắt hàm luôn để không chạy vòng lặp lọc đơn hàng phía dưới
        }
        else {
            // Các tab Lịch sử, Đánh giá thông thường
            layoutCoinsBanner.setVisibility(View.VISIBLE);
            layoutFilters.setVisibility(View.VISIBLE);
            layoutRecommendations.setVisibility(View.GONE);

            recyclerView.setVisibility(View.VISIBLE);
            rvDeals.setVisibility(View.GONE);
        }

        for (FoodOrder order : allOrders) {
            boolean matchTab = false;
            if ("Đánh giá".equalsIgnoreCase(currentTab)) {
                matchTab = "Hoàn thành".equalsIgnoreCase(order.getStatus()) && !order.isReviewed() && !order.isReviewExpired();
            } else {
                matchTab = order.getCategory().equalsIgnoreCase(currentTab);
            }
            boolean matchService = currentService.equals("Tất cả") || order.getService().equalsIgnoreCase(currentService);
            boolean matchStatus = currentStatus.equals("Tất cả") || order.getStatus().equalsIgnoreCase(currentStatus);

            boolean matchSearch = currentQuery.isEmpty() || order.getMerchantName().toLowerCase().contains(currentQuery.toLowerCase());
            if (!matchSearch && order.getSubItems() != null) {
                for (FoodOrder.SubItem subItem : order.getSubItems()) {
                    if (subItem.getName().toLowerCase().contains(currentQuery.toLowerCase())) {
                        matchSearch = true;
                        break;
                    }
                }
            }

            // TÍCH HỢP ĐIỀU KIỆN LỌC KHOẢNG THỜI GIAN
            boolean matchDate = true;
            if (filterStartDate != null && filterEndDate != null) {
                Date orderDate = parseOrderDate(order.getDate());
                // Kiểm tra xem ngày đặt có nằm trọn trong [Đầu, Cuối] không
                matchDate = (!orderDate.before(filterStartDate) && !orderDate.after(filterEndDate));
            }

            if (matchTab && matchService && matchStatus && matchSearch && matchDate) {
                displayOrders.add(order);
            }
        }

        adapter.setData(displayOrders);

        if (isLoadingData) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.GONE);
            return;
        }

        if (displayOrders.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            if (isErrorState) {
                ivEmpty.setImageResource(R.drawable.ic_internet);
                tvEmptyTitle.setText("Lỗi tải dữ liệu");
                tvEmptyDesc.setText(currentErrorMessage);
            } else {
                ivEmpty.setImageResource(R.drawable.img_transactionhistory_notransaction);
                tvEmptyTitle.setText("Quên chưa đặt món rồi nè bạn ơi?");
                tvEmptyDesc.setText("Bạn sẽ nhìn thấy các món đang được chuẩn bị hoặc giao đi tại đây để kiểm tra đơn hàng nhanh hơn!");
            }
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }

    private void setupRecommendations() {
        rvRecommendations.setLayoutManager(new LinearLayoutManager(this));
        List<RecommendedShop> recList = new ArrayList<>();
        List<com.example.uitpayapp.home.home_models.Restaurant> restaurants = com.example.uitpayapp.home.HomeActivity.HomeRepository.getInstance().getRestaurants();
        
        if (restaurants != null && restaurants.size() >= 3) {
            recList.add(new RecommendedShop(restaurants.get(0).getName(), 4.8, 1.2, 15, "Mã giảm 20%", restaurants.get(0).getMenu().get(0).getImageResId()));
            recList.add(new RecommendedShop(restaurants.get(1).getName(), 4.6, 2.5, 25, "Mã giảm 15%", restaurants.get(1).getMenu().get(0).getImageResId()));
            recList.add(new RecommendedShop(restaurants.get(2).getName(), 4.5, 3.1, 30, "Mã giảm 10%", restaurants.get(2).getMenu().get(0).getImageResId()));
        } else {
            recList.add(new RecommendedShop("Ăn Vặt Minh Mập - Mì Xào & Nuôi Xào - Quốc Lộ 1k", 4.4, 0.2, 22, "Mã giảm 18%", android.R.drawable.ic_menu_gallery));
            recList.add(new RecommendedShop("Bánh Mì Uyên Thư - Quốc Lộ 1K", 4.6, 0.3, 25, "Mã giảm 18%", android.R.drawable.ic_menu_gallery));
            recList.add(new RecommendedShop("GS25 - Nguyễn Bĩnh Khiêm - Bình Dương", 4.2, 0.5, 18, "Mã giảm 10%", android.R.drawable.ic_menu_gallery));
        }
        rvRecommendations.setAdapter(new RecommendedShopAdapter(recList));
    }

    private void createDummyFoodOrders() {
        List<com.example.uitpayapp.home.home_models.FoodMenuItem> popularFoods = com.example.uitpayapp.home.HomeActivity.HomeRepository.getInstance().getPopularFoods();
        
        List<FoodOrder.SubItem> subItems1 = new ArrayList<>();
        subItems1.add(new FoodOrder.SubItem(popularFoods.get(0).getName(), popularFoods.get(0).getImageResId()));
        subItems1.add(new FoodOrder.SubItem(popularFoods.get(1).getName(), popularFoods.get(1).getImageResId()));
        allOrders.add(new FoodOrder("05066-620675729", "Bun Burrito - Trần Quốc Toản", 61000, 1, "Hôm nay 17:14", "Đang giao", "Đồ ăn", true, "Đang đến", subItems1));

        List<FoodOrder.SubItem> subItems2 = new ArrayList<>();
        subItems2.add(new FoodOrder.SubItem(popularFoods.get(2).getName(), popularFoods.get(2).getImageResId()));
        subItems2.add(new FoodOrder.SubItem(popularFoods.get(3).getName(), popularFoods.get(3).getImageResId()));
        allOrders.add(new FoodOrder("04066-570542539", "MêBee - Cơm Tấm Long Xuyên &amp; Trà Sữa", 64000, 3, "04/06/2026", "Hoàn thành", "Đồ ăn", true, "Lịch sử", subItems2));

        List<FoodOrder.SubItem> subItemsSingle = new ArrayList<>();
        subItemsSingle.add(new FoodOrder.SubItem(popularFoods.get(4).getName(), popularFoods.get(4).getImageResId()));
        allOrders.add(new FoodOrder("21099-112233445", "Cơm Tấm Ngô Quyền - Linh Trung", 45000, 1, "15/05/2026", "Hoàn thành", "Đồ ăn", false, "Lịch sử", subItemsSingle));

        List<FoodOrder.SubItem> subItemsAnVien = new ArrayList<>();
        // Đổ món ăn "Lục Trà Chanh Dây" vào danh sách sub-item
        subItemsAnVien.add(new FoodOrder.SubItem("Lục Trà Chanh Dây", popularFoods.get(0).getImageResId()));

        allOrders.add(new FoodOrder(
                "15016-594977098",                        // Mã hóa đơn (Khớp mã liên kết từ tab Deal)
                "Trà Sữa An Viên - Đường 30 Tháng 4",      // Tên quán
                11000,                                     // Tổng thanh toán sau giảm giá (11.000đ)
                1,                                         // Số lượng: 1 món
                "15/01/2026",                              // Ngày đặt hàng trùng khớp
                "Hoàn thành",                              // Trạng thái đơn
                "Đồ ăn",                                   // Loại dịch vụ
                false,                                     // Yêu thích
                "Lịch sử",                                 // CATEGORY LÀ LỊCH SỬ ĐỂ HIỂN THỊ TRONG TAB LỊCH SỬ
                subItemsAnVien                             // Mảng món ăn bên trong
        ));


        List<FoodOrder.SubItem> subItemsFour = new ArrayList<>();
        subItemsFour.add(new FoodOrder.SubItem(popularFoods.get(5).getName(), popularFoods.get(5).getImageResId()));
        allOrders.add(new FoodOrder("12044-998877665", "Gà Rán Popeyes - Võ Văn Ngân", 145000, 4, "12/05/2026", "Hoàn thành", "Đồ ăn", true, "Lịch sử", subItemsFour));
    }

    private void fetchOrdersFromBackend() {
        if (!com.example.uitpayapp.network.SessionManager.getInstance(this).isLoggedIn()) {
            isLoadingData = false;
            layoutLoading.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            isErrorState = true;
            currentErrorMessage = "Vui lòng đăng nhập để xem đơn hàng của bạn";
            allOrders.clear();
            applyFilter();
            return;
        }

        if (orderRepository == null) {
            orderRepository = new OrderRepository();
        }
        
        isLoadingData = true;
        isErrorState = false;
        layoutLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
        
        orderRepository.getCustomerHistory(new ApiCallback<List<OrderResponse>>() {
            @Override
            public void onSuccess(List<OrderResponse> orders) {
                isLoadingData = false;
                layoutLoading.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                allOrders.clear();
                for (OrderResponse order : orders) {
                    String category = "Lịch sử";
                    String statusText = "Hoàn thành";
                    String status = order.getStatus();
                    
                    if ("PENDING".equalsIgnoreCase(status) || "PREPARING".equalsIgnoreCase(status) || "DELIVERING".equalsIgnoreCase(status)) {
                        category = "Đang đến";
                        statusText = "Đang chuẩn bị";
                        if ("DELIVERING".equalsIgnoreCase(status)) {
                            statusText = "Đang giao";
                        }
                    } else if ("CANCELLED".equalsIgnoreCase(status)) {
                        statusText = "Đã hủy";
                    }
                    
                    List<FoodOrder.SubItem> subItems = new ArrayList<>();
                    if (order.getItems() != null) {
                        for (OrderResponse.OrderItemResponse item : order.getItems()) {
                            subItems.add(new FoodOrder.SubItem(item.getName(), item.getImageUrl()));
                        }
                    }
                    
                    String dateStr = order.getCreatedAt();
                    if (dateStr == null || dateStr.isEmpty()) {
                        dateStr = "Hôm nay";
                    } else {
                        try {
                            if (dateStr.contains("T")) {
                                String[] parts = dateStr.split("T");
                                String[] dateParts = parts[0].split("-");
                                dateStr = dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
                            }
                        } catch (Exception e) {
                            // ignore
                        }
                    }
                    
                    String orderIdStr = String.valueOf(order.getId());
                    String merchantName = order.getRestaurantName() != null ? order.getRestaurantName() : "Cửa hàng";
                    long totalPrice = order.getTotalAmount() != null ? order.getTotalAmount().longValue() : 0;
                    int itemCount = order.getItemCount() != null ? order.getItemCount() : 0;
                    
                    allOrders.add(new FoodOrder(
                        orderIdStr,
                        merchantName,
                        totalPrice,
                        itemCount,
                        dateStr,
                        statusText,
                        "Đồ ăn",
                        false,
                        category,
                        subItems,
                        order.getReviewed(),
                        order.getReviewExpired()
                    ));
                }
                applyFilter();
            }

            @Override
            public void onError(String errorMessage) {
                isLoadingData = false;
                layoutLoading.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                isErrorState = true;
                currentErrorMessage = errorMessage;
                allOrders.clear();
                applyFilter();
            }
        });
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Đang đến"), true);
        tabLayout.addTab(tabLayout.newTab().setText("Deal đã mua"));
        tabLayout.addTab(tabLayout.newTab().setText("Lịch sử"));
        tabLayout.addTab(tabLayout.newTab().setText("Đánh giá"));

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

    private void setupFilterMenus() {
        String[] categories = {"Tất cả", "Cơm", "Bún Phở", "Bánh mì", "Fastfood", "Lẩu", "Đồ nướng", "Cafe", "Trà sữa", "Ăn vặt", "Tráng miệng", "Hải sản", "Chay", "Đồ uống", "Gà rán", "Pizza"};
        android.widget.ArrayAdapter<String> serviceAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categories);
        android.widget.ListPopupWindow servicePopup = new android.widget.ListPopupWindow(this);
        servicePopup.setAdapter(serviceAdapter);
        servicePopup.setAnchorView(tvFilterService);
        servicePopup.setWidth((int) (150 * getResources().getDisplayMetrics().density));
        servicePopup.setHeight((int) (200 * getResources().getDisplayMetrics().density));
        
        servicePopup.setOnItemClickListener((parent, view, position, id) -> {
            currentService = categories[position];
            if (currentService.equals("Tất cả")) {
                tvFilterService.setText("Tất cả ▼");
            } else {
                tvFilterService.setText(currentService + " ▼");
            }
            applyFilter();
            servicePopup.dismiss();
        });

        tvFilterService.setOnClickListener(v -> servicePopup.show());
        tvFilterService.setText("Tất cả ▼");

        String[] statuses = {"Tất cả", "Hoàn thành", "Đã hủy"};
        android.widget.ArrayAdapter<String> statusAdapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_list_item_1, statuses);
        android.widget.ListPopupWindow statusPopup = new android.widget.ListPopupWindow(this);
        statusPopup.setAdapter(statusAdapter);
        statusPopup.setAnchorView(tvFilterStatus);
        statusPopup.setWidth((int) (150 * getResources().getDisplayMetrics().density));
        
        statusPopup.setOnItemClickListener((parent, view, position, id) -> {
            currentStatus = statuses[position];
            if (currentStatus.equals("Tất cả")) {
                tvFilterStatus.setText("Tất cả ▼");
            } else {
                tvFilterStatus.setText(currentStatus + " ▼");
            }
            applyFilter();
            statusPopup.dismiss();
        });

        tvFilterStatus.setOnClickListener(v -> statusPopup.show());
        tvFilterStatus.setText("Tất cả ▼");
    }

    private void setupSearchLogic() {
        TextView tvTitle = findViewById(R.id.tvTitle);
        ivSearch.setOnClickListener(v -> {
            if (etSearch.getVisibility() == View.GONE) {
                tvTitle.setVisibility(View.GONE);
                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();
                ivSearch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            } else {
                closeSearch();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                if (currentQuery.length() > 0) {
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.ic_menu_close_clear_cancel, 0);
                } else {
                    etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }
                applyFilter();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (etSearch.getCompoundDrawables()[2] != null) {
                    if (event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundPaddingRight())) {
                        etSearch.setText("");
                        return true;
                    }
                }
            }
            return false;
        });
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
        navFavorite.setOnClickListener(v -> {
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

    @Override
    protected void onResume() {
        super.onResume();
        updateNotificationBadge();
        fetchOrdersFromBackend(); // refresh history list on resume
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