package com.example.uitpayapp.YumYumPriority;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;
import com.example.uitpayapp.giftexchange.GiftExchangeActivity;
import com.example.uitpayapp.home.home_adapters.ImageSliderAdapter;
import com.example.uitpayapp.profile.ProfileWebView;
import com.example.uitpayapp.voucher.VoucherActivity;

import java.util.ArrayList;
import java.util.List;

public class PriorityYumYumActivity extends AppCompatActivity {
    ViewPager2 RankSlider;
    ViewPager2 BannerSlider;
    RecyclerView RVCheckIn;
    Handler sliderHandler;
    Runnable sliderRunnable;
    long currentSpending=200000;//sau nay goi api de cap nhat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_yumyum_priority);
        View topBar = findViewById(R.id.top_bar_uit_priority);
        View mainContainer = findViewById(R.id.priority_uitpay_container);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        BannerSlider = findViewById(R.id.priority_image_slider);
        RankSlider = findViewById(R.id.priority_account_rank_slider);
        ((TextView)topBar.findViewById(R.id.top_bar_title)).setText("UITpay Priority");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v->finish());
        SetBannerData();
        fetchLoyaltyData();
    }

    private void fetchLoyaltyData() {
        com.example.uitpayapp.network.RetrofitClient.getLoyaltyService().getMyLoyaltyInfo().enqueue(new retrofit2.Callback<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>> call, retrofit2.Response<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Long totalSpent = response.body().getData().getTotalSpending();
                    if (totalSpent != null) {
                        currentSpending = totalSpent;
                    } else {
                        currentSpending = 0;
                    }
                    SetAccountRankingData();
                } else {
                    SetAccountRankingData(); // default
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.example.uitpayapp.models.ApiResponse<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>> call, Throwable t) {
                SetAccountRankingData();
            }
        });
    }

    private void SetAccountRankingData() {
        List<RankModel> rankList = new ArrayList<>();
        rankList.add(new RankModel(RankModel.RankType.NEW, currentSpending, "Mê hay hông mê?"));
        rankList.add(new RankModel(RankModel.RankType.SILVER, currentSpending, "Ưu đãi độc quyền"));
        rankList.add(new RankModel(RankModel.RankType.GOLD, currentSpending, "Tặng voucher thăng hạng"));
        rankList.add(new RankModel(RankModel.RankType.DIAMOND, currentSpending, "Voucher duy trì hạng"));
        PriorityAccountRankAdapter adapter = new PriorityAccountRankAdapter(rankList);
        RankSlider.setAdapter(adapter);

        RankModel.RankType currentRankType = RankModel.RankType.NEW;
        if (currentSpending >= RankModel.RankType.DIAMOND.getThreshold()) {
            currentRankType = RankModel.RankType.DIAMOND;
        } else if (currentSpending >= RankModel.RankType.GOLD.getThreshold()) {
            currentRankType = RankModel.RankType.GOLD;
        } else if (currentSpending >= RankModel.RankType.SILVER.getThreshold()) {
            currentRankType = RankModel.RankType.SILVER;
        }
        RankModel currentRankModel = new RankModel(currentRankType, currentSpending, "");
        List<com.example.uitpayapp.profile.MenuItemData> benefits = currentRankModel.getRankBenefits();
        RecyclerView rvCurrentBenefits = findViewById(R.id.rv_current_benefits);
        if (rvCurrentBenefits != null) {
            rvCurrentBenefits.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
            List<com.example.uitpayapp.profile.GroupItemData> group = new ArrayList<>();
            group.add(new com.example.uitpayapp.profile.GroupItemData("Chi tiết đặc quyền hiện tại", benefits));
            rvCurrentBenefits.setAdapter(new com.example.uitpayapp.profile.ProfileMenuAdapter(this, group, null));
        }
    }

    public void showRankBenefitsBottomSheet() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setBackgroundColor(android.graphics.Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Tất cả các Đặc quyền");
        title.setTextSize(18);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        title.setTextColor(android.graphics.Color.BLACK);
        title.setPadding(0, 0, 0, 32);
        layout.addView(title);

        RecyclerView rv = new RecyclerView(this);
        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        
        List<com.example.uitpayapp.profile.GroupItemData> allGroups = new ArrayList<>();
        allGroups.add(new com.example.uitpayapp.profile.GroupItemData("Hạng Kim Cương", new RankModel(RankModel.RankType.DIAMOND, 0, "").getRankBenefits()));
        allGroups.add(new com.example.uitpayapp.profile.GroupItemData("Hạng Vàng", new RankModel(RankModel.RankType.GOLD, 0, "").getRankBenefits()));
        allGroups.add(new com.example.uitpayapp.profile.GroupItemData("Hạng Bạc", new RankModel(RankModel.RankType.SILVER, 0, "").getRankBenefits()));
        allGroups.add(new com.example.uitpayapp.profile.GroupItemData("Hạng Mới", new RankModel(RankModel.RankType.NEW, 0, "").getRankBenefits()));
        
        rv.setAdapter(new com.example.uitpayapp.profile.ProfileMenuAdapter(this, allGroups, null));
        layout.addView(rv);

        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.show();
    }
    private void SetBannerData() {
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
}
