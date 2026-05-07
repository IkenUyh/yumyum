package com.example.uitpayapp.giftexchange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;
import com.example.uitpayapp.UITpayPriority.PriorityUITpayActivity;
import com.example.uitpayapp.home.ImageSliderAdapter;
import com.example.uitpayapp.suggestion.SuggestAdapter;
import com.example.uitpayapp.suggestion.SuggestionModel;
import com.example.uitpayapp.voucher.VoucherActivity;

import java.util.ArrayList;
import java.util.List;

public class GiftExchangeActivity extends AppCompatActivity {

    private RecyclerView rvCategory;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryList;

    private RecyclerView rvExchangeVoucher, rvExchangeVoucherDemo, rvGift1Coin;
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
        setContentView(R.layout.activity_gift_exchange);
        
        initView();
        setCategoryData();
        setExchangeVoucherData();
        setGift1CoinData();
        setBannerData();
        findViewById(R.id.gift_exchange_my_voucher).setOnClickListener(v ->
        {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivity(intent);
            this.finish();
        });
        findViewById(R.id.gift_exchange_priority).setOnClickListener(v ->
        {
            Intent intent = new Intent(this, PriorityUITpayActivity.class);
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
        rvGift1Coin = findViewById(R.id.rv_gift_1coin);
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
        categoryList.add(new CategoryModel("Hóa đơn", "hoadon", false));
        categoryList.add(new CategoryModel("Điện thoại", "dienthoai", false));
        categoryList.add(new CategoryModel("Bảo hiểm", "baohiem", false));
        categoryList.add(new CategoryModel("Mua sắm & Ăn uống", "muasam_anuong", false));

        categoryAdapter = new CategoryAdapter(categoryList, this::HandleCategoryClick);

        rvCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCategory.setAdapter(categoryAdapter);
    }

    private void setExchangeVoucherData() {
        allVoucherList = new ArrayList<>();
        allVoucherList.add(new ExchangeVoucherModel(-1, "Giảm 50K Hóa đơn", "Cho hóa đơn điện từ 500K", "50", "5K", "hoadon"));
        allVoucherList.add(new ExchangeVoucherModel(-1, "Nạp thẻ 20K", "Chiết khấu nạp tiền điện thoại", "20", "2K", "dienthoai"));
        allVoucherList.add(new ExchangeVoucherModel(-1, "Giảm 100K Bảo hiểm", "Áp dụng bảo hiểm xe máy", "100", "10K", "baohiem"));
        allVoucherList.add(new ExchangeVoucherModel(-1, "Voucher Highlands 30K", "Áp dụng toàn quốc", "30", "3K", "muasam_anuong"));
        allVoucherList.add(new ExchangeVoucherModel(-1, "Giảm 20K Nước", "Hóa đơn nước trên 100K", "20", "1K", "hoadon"));
        allVoucherList.add(new ExchangeVoucherModel(-1, "Data 4G 10GB", "Gói cước Viettel 30 ngày", "50", "5K", "dienthoai"));
        allVoucherList.add(new ExchangeVoucherModel(-1, "Voucher Phúc Long", "Giảm 10% tổng hóa đơn", "15", "Free", "muasam_anuong"));
        
        // Setup Demo RV
        List<ExchangeVoucherModel> demoList = new ArrayList<>();
        for (int i = 0; i < Math.min(4, allVoucherList.size()); i++) {
            demoList.add(allVoucherList.get(i));
        }
        ExchangeVoucherAdapter demoAdapter = new ExchangeVoucherAdapter(demoList);
        rvExchangeVoucherDemo.setLayoutManager(new GridLayoutManager(this, 2));
        rvExchangeVoucherDemo.setAdapter(demoAdapter);
        rvExchangeVoucherDemo.setNestedScrollingEnabled(false);

        // Setup Main RV
        displayVoucherList = new ArrayList<>(allVoucherList);
        exchangeVoucherAdapter = new ExchangeVoucherAdapter(displayVoucherList);
        rvExchangeVoucher.setLayoutManager(new GridLayoutManager(this, 2));
        rvExchangeVoucher.setAdapter(exchangeVoucherAdapter);
        rvExchangeVoucher.setNestedScrollingEnabled(false);
    }

    private void setGift1CoinData() {
        List<SuggestionModel> list1Coin = new ArrayList<>();
        list1Coin.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL, R.drawable.img_priority_banner1, "[Cỏ Cây Hoa Lá] E-voucher Giảm 20%", "1 Xu"));
        list1Coin.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL, R.drawable.img_priority_banner2, "[PamperMe] E-voucher Giảm 50% ưu đãi", "1 Xu"));
        list1Coin.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL, R.drawable.img_priority_banner3, "[Ladomax] E-voucher Giảm 50%", "1 Xu"));
        
        SuggestAdapter adapter = new SuggestAdapter(list1Coin);
        rvGift1Coin.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvGift1Coin.setAdapter(adapter);
    }

    private void HandleCategoryClick(CategoryModel category) {
        String type = category.getType();
        displayVoucherList.clear();
        if (type.equals("all")) {
            displayVoucherList.addAll(allVoucherList);
        } else {
            for (ExchangeVoucherModel item : allVoucherList) {
                if (item.getType().equals(type)) {
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
}
