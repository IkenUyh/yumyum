package com.example.uitpayapp.giftexchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;
import com.example.uitpayapp.YumYumPriority.PriorityYumYumActivity;
import com.example.uitpayapp.home.home_adapters.ImageSliderAdapter;
import com.example.uitpayapp.voucher.VoucherActivity;

import java.util.ArrayList;
import java.util.List;

public class GiftExchangeActivity extends AppCompatActivity {

    private RecyclerView rvCategory,rvCheckIn;
    private CategoryAdapter categoryAdapter;
    private PriorityCheckInAdapter checkinAdapter;
    private List<CategoryModel> categoryList;

    private RecyclerView rvExchangeVoucher, rvExchangeVoucherDemo;
    private ExchangeVoucherAdapter exchangeVoucherAdapter;
    private List<ExchangeVoucherModel> allVoucherList;
    private List<ExchangeVoucherModel> displayVoucherList;
    
    private NestedScrollView nestedScrollView;
    private View layoutNewGifts;
    private TextView tvSeeMore;
    private EditText etSearch;

    private ViewPager2 BannerSlider;
    Handler sliderHandler;
    Runnable sliderRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_gift_exchange);
        
        initView();
        setCategoryData();
        setExchangeVoucherData();
        setBannerData();

        checkinAdapter = new PriorityCheckInAdapter(new ArrayList<>(), item -> HandleCheckIn(item));
        rvCheckIn.setAdapter(checkinAdapter);

        loadLoyaltyData();

        findViewById(R.id.gift_exchange_my_voucher).setOnClickListener(v ->
        {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivity(intent);
            this.finish();
        });
        findViewById(R.id.gift_exchange_priority).setOnClickListener(v ->
        {
            Intent intent = new Intent(this, PriorityYumYumActivity.class);
            startActivity(intent);
            this.finish();
        });
    }


    private void initView() {
        View topBar = findViewById(R.id.top_bar_gift_exchange);
        nestedScrollView = findViewById(R.id.gift_exchange_container);
        rvCategory = findViewById(R.id.rv_new_gift_exchange_menu);
        rvExchangeVoucher = findViewById(R.id.rv_new_gift_exchange);
        rvExchangeVoucherDemo = findViewById(R.id.rv_gift_exchange_demo);
        rvCheckIn = findViewById(R.id.rv_daily_checkin);
        BannerSlider = findViewById(R.id.gift_exchange_image_slider);
        layoutNewGifts = findViewById(R.id.layout_new_gifts_section);
        tvSeeMore = findViewById(R.id.tv_see_more_exchange_voucher);
        etSearch = findViewById(R.id.et_search_gift);

        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom + 10;
            if (nestedScrollView != null) {
                nestedScrollView.setPadding(nestedScrollView.getPaddingLeft(), nestedScrollView.getPaddingTop(), nestedScrollView.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });

        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView)topBar.findViewById(R.id.top_bar_title)).setText("Dùng xu đổi quà");

        tvSeeMore.setOnClickListener(v -> {
            // Cuộn xuống phần "Khám phá quà mới"
            nestedScrollView.smoothScrollTo(0, layoutNewGifts.getTop());
        });
    }

    private void setCategoryData() {
        categoryList = new ArrayList<>();
        categoryList.add(new CategoryModel("Tất cả", "all", true));
        categoryList.add(new CategoryModel("Giảm giá món", "FOOD_DISCOUNT", false));
        categoryList.add(new CategoryModel("Phí vận chuyển", "SHIPPING_FEE", false));

        categoryAdapter = new CategoryAdapter(categoryList, this::HandleCategoryClick);

        rvCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategory.setAdapter(categoryAdapter);
    }

    private void setExchangeVoucherData() {
        allVoucherList = new ArrayList<>();
        allVoucherList.add(new ExchangeVoucherModel("Khao 50K trà sữa", "Thưởng thức trà sữa đậm vị", "50", ExchangeVoucherModel.ExchangeVoucherType.FOOD_DISCOUNT));
        allVoucherList.add(new ExchangeVoucherModel("Miễn phí vận chuyển", "Freeship cho mọi đơn hàng từ 0đ", "20", ExchangeVoucherModel.ExchangeVoucherType.SHIPPING_FEE));
        allVoucherList.add(new ExchangeVoucherModel("Giảm 100K tiệc ngon", "Ăn uống linh đình, không lo về giá", "100", ExchangeVoucherModel.ExchangeVoucherType.FOOD_DISCOUNT));
        allVoucherList.add(new ExchangeVoucherModel("Highlands Coffee -30K", "Đậm vị cà phê, khao bạn ly mới", "30", ExchangeVoucherModel.ExchangeVoucherType.FOOD_DISCOUNT));
        allVoucherList.add(new ExchangeVoucherModel("Giảm ngay 20K phí ship", "Giao hàng hỏa tốc trong 30 phút", "20", ExchangeVoucherModel.ExchangeVoucherType.SHIPPING_FEE));
        allVoucherList.add(new ExchangeVoucherModel("Freeship Extra 15K", "Giảm phí ship cho đơn từ 50K", "50", ExchangeVoucherModel.ExchangeVoucherType.SHIPPING_FEE));
        allVoucherList.add(new ExchangeVoucherModel("Voucher Phúc Long 10%", "Trà sữa đậm vị chuẩn gu bạn", "15", ExchangeVoucherModel.ExchangeVoucherType.FOOD_DISCOUNT));

        List<ExchangeVoucherModel> demoList = new ArrayList<>();
        for (int i = 0; i < Math.min(4, allVoucherList.size()); i++) {
            demoList.add(allVoucherList.get(i));
        }
        ExchangeVoucherAdapter demoAdapter = new ExchangeVoucherAdapter(demoList);
        rvExchangeVoucherDemo.setLayoutManager(new GridLayoutManager(this, 2));
        rvExchangeVoucherDemo.setAdapter(demoAdapter);
        rvExchangeVoucherDemo.setNestedScrollingEnabled(false);

        displayVoucherList = new ArrayList<>(allVoucherList);
        exchangeVoucherAdapter = new ExchangeVoucherAdapter(displayVoucherList);
        rvExchangeVoucher.setLayoutManager(new GridLayoutManager(this, 2));
        rvExchangeVoucher.setAdapter(exchangeVoucherAdapter);
        rvExchangeVoucher.setNestedScrollingEnabled(false);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void HandleCategoryClick(CategoryModel category) {
        String type = category.getType();
        displayVoucherList.clear();
        if (type.equals("all")) {
            displayVoucherList.addAll(allVoucherList);
        } else {
            for (ExchangeVoucherModel item : allVoucherList) {
                if (item.getVoucherType().name().equals(type)) {
                    displayVoucherList.add(item);
                }
            }
        }
        exchangeVoucherAdapter.notifyDataSetChanged();
    }

    private void setBannerData() {
        ImageSliderAdapter adapter = new ImageSliderAdapter(new ArrayList<>());
        BannerSlider.setAdapter(adapter);
        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                if (adapter.getItemCount() > 1) {
                    int currentitem = BannerSlider.getCurrentItem();
                    currentitem = (currentitem + 1) % adapter.getItemCount();
                    BannerSlider.setCurrentItem(currentitem, true);
                }
                sliderHandler.postDelayed(this, 3000);
            }
        };
        sliderHandler.post(sliderRunnable);
        
        com.example.uitpayapp.network.RetrofitClient.getHomeApiService().getHomeCore("").enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<com.example.uitpayapp.home.network.Banner> banners = response.body().getData().getBanners();
                    if (banners != null && !banners.isEmpty()) {
                        List<String> urls = new ArrayList<>();
                        for (com.example.uitpayapp.home.network.Banner b : banners) urls.add(b.getImageUrl());
                        adapter.updateData(urls);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.home.network.HomeCoreResponse>> call, Throwable t) {}
        });
    }
    private void loadLoyaltyData() {
        new com.example.uitpayapp.modules.loyalty.LoyaltyRepository().getMyLoyaltyInfo(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO data) {
                runOnUiThread(() -> {
                    TextView tvUserCoins = findViewById(R.id.tv_user_coins);
                    if (tvUserCoins != null) {
                        tvUserCoins.setText(" " + (data.getCurrentPoints() != null ? data.getCurrentPoints() : 0));
                    }

                    int streak = data.getCheckinStreak() != null ? data.getCheckinStreak() : 0;
                    boolean canCheckIn = data.getCanCheckInToday() != null && data.getCanCheckInToday();

                    List<CheckInModel> checkInList = new ArrayList<>();
                    CheckInModel.DayConfig[] dayConfigs = CheckInModel.DayConfig.values();

                    int todayIndex = canCheckIn ? (streak % 7) : -1;

                    for (int i = 0; i < 7; i++) {
                        boolean isOpened;
                        boolean isChecked;

                        if (canCheckIn) {
                            if (i < todayIndex) {
                                isOpened = true;
                                isChecked = true;
                            } else if (i == todayIndex) {
                                isOpened = true;
                                isChecked = false;
                            } else {
                                isOpened = false;
                                isChecked = false;
                            }
                        } else {
                            int lastCheckedIndex = (streak > 0) ? ((streak - 1) % 7) : -1;
                            if (i <= lastCheckedIndex) {
                                isOpened = true;
                                isChecked = true;
                            } else {
                                isOpened = false;
                                isChecked = false;
                            }
                        }

                        checkInList.add(new CheckInModel(dayConfigs[i], isOpened, isChecked));
                    }

                    checkinAdapter = new PriorityCheckInAdapter(checkInList, item -> HandleCheckIn(item));
                    rvCheckIn.setAdapter(checkinAdapter);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(GiftExchangeActivity.this, "Lỗi tải thông tin xu: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void HandleCheckIn(CheckInModel item) {
        new com.example.uitpayapp.modules.loyalty.LoyaltyRepository().dailyCheckIn(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO data) {
                runOnUiThread(() -> {
                    TextView tvUserCoins = findViewById(R.id.tv_user_coins);
                    if (tvUserCoins != null) {
                        tvUserCoins.setText(" " + (data.getCurrentPoints() != null ? data.getCurrentPoints() : 0));
                    }
                    loadLoyaltyData();
                    android.widget.Toast.makeText(GiftExchangeActivity.this, "Điểm danh thành công! Nhận " + item.getConfig().getCoins() + " xu.", android.widget.Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(GiftExchangeActivity.this, "Lỗi điểm danh: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
