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
        SetAccountRankingData();
        SetBannerData();
        setListener();
    }
    private void setListener() {
        findViewById(R.id.priority_question_contact).setOnClickListener(v->{
            Intent intent=new Intent(PriorityYumYumActivity.this, ProfileWebView.class);
            intent.putExtra("URL_KEY","https://help.shopee.vn/portal/4/article/79254-[Shopee-Rewards]-C%C3%A1c-c%C3%A2u-h%E1%BB%8Fi-th%C6%B0%E1%BB%9Dng-g%E1%BA%B7p-v%E1%BB%81-Ch%C6%B0%C6%A1ng-tr%C3%ACnh-Kh%C3%A1ch-h%C3%A0ng-th%C3%A2n-thi%E1%BA%BFt-Shopee-(Shopee-Rewards)");
            startActivity(intent);
        });
        findViewById(R.id.priority_change_gift).setOnClickListener(v->{
            Intent intent=new Intent(PriorityYumYumActivity.this, VoucherActivity.class);
            startActivity(intent);
            this.finish();
        });
        findViewById(R.id.priority_coin).setOnClickListener(v->{
            Intent intent=new Intent(PriorityYumYumActivity.this, GiftExchangeActivity.class);
            startActivity(intent);
            this.finish();
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
    }
    private void SetBannerData() {
        List<Integer> bannerList = new ArrayList<Integer>();
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
