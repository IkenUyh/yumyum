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
        SetCheckInData();
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
            int safeTopPadding = cutout.top + 10;
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
        List<Integer> bannerList = new ArrayList<>();
        bannerList.add(R.drawable.img_priority_banner1);
        bannerList.add(R.drawable.img_priority_banner2);
        bannerList.add(R.drawable.img_priority_banner3);
        ImageSliderAdapter adapter = new ImageSliderAdapter(bannerList);
        BannerSlider.setAdapter(adapter);
        sliderHandler = new Handler(Looper.getMainLooper());
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentitem = BannerSlider.getCurrentItem();
                currentitem = (currentitem + 1) % bannerList.size();
                BannerSlider.setCurrentItem(currentitem, true);
                sliderHandler.postDelayed(this, 3000);
            }
        };
        sliderHandler.post(sliderRunnable);
    }
    private void SetCheckInData() {
        List<CheckInModel> checkInList = new ArrayList<>();
        //sau nay goi api voi xu ly o day
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_1, true,false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_2, false,false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_3, false,false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_4, false,false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_5, false,false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_6, false,false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_7, false,false));
        checkinAdapter= new PriorityCheckInAdapter(checkInList, item -> HandleCheckIn(item));
        rvCheckIn.setAdapter(checkinAdapter);
    }
    private void HandleCheckIn(CheckInModel item)
    {
        //sau nay goi api voi xu ly o day
        item.setChecked(true);
        checkinAdapter.notifyDataSetChanged();
    }
}
