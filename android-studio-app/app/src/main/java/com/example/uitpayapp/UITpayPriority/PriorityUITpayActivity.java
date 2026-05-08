package com.example.uitpayapp.UITpayPriority;

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
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uitpayapp.R;
import com.example.uitpayapp.giftexchange.ExchangeVoucherAdapter;
import com.example.uitpayapp.giftexchange.ExchangeVoucherModel;
import com.example.uitpayapp.giftexchange.GiftExchangeActivity;
import com.example.uitpayapp.home.ImageSliderAdapter;
import com.example.uitpayapp.profile.ProfileWebView;

import java.util.ArrayList;
import java.util.List;

public class PriorityUITpayActivity extends AppCompatActivity {
    ViewPager2 RankSlider;
    ViewPager2 BannerSlider;
    RecyclerView RVCheckIn;
    Handler sliderHandler;
    Runnable sliderRunnable;
    long currentSpending=200000;//sau nay goi api de cap nhat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uitpay_priority);
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
        RVCheckIn = findViewById(R.id.rv_priority_checkin);
        ((TextView)topBar.findViewById(R.id.top_bar_title)).setText("UITpay Priority");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v->finish());
        View questionContact=findViewById(R.id.priority_question_contact);
        View rule=findViewById(R.id.priority_rule);
        ((ImageView)rule.findViewById(R.id.menu_icon)).setImageResource(R.drawable.ic_receiptscreen_loan);
        ((TextView)rule.findViewById(R.id.menu_title)).setText("Thể lệ và hướng dẫn");
        ((ImageView)questionContact.findViewById(R.id.menu_icon)).setImageResource(R.drawable.ic_question_contact);
        ((TextView)questionContact.findViewById(R.id.menu_title)).setText("Câu hỏi thường gặp");
        SetAccountRankingData();
        SetBannerData();
        SetCheckInData();
        setListener();
        setSecondaryData();
    }
    private void setSecondaryData() {
        View rvExchangeVoucher = findViewById(R.id.rv_exchange_vouchers);
        View rvPriorityTasks = findViewById(R.id.rv_priority_tasks);
        List<ExchangeVoucherModel> ExVoucherList = new ArrayList<>();
        ExVoucherList.add(new ExchangeVoucherModel(-1, "Giảm 50K Hóa đơn", "Cho hóa đơn điện từ 500K", "50", "5K", "hoadon"));
        ExVoucherList.add(new ExchangeVoucherModel(-1, "Nạp thẻ 20K", "Chiết khấu nạp tiền điện thoại", "20", "2K", "dienthoai"));
        ExVoucherList.add(new ExchangeVoucherModel(-1, "Giảm 100K Bảo hiểm", "Áp dụng bảo hiểm xe máy", "100", "10K", "baohiem"));
        ExVoucherList.add(new ExchangeVoucherModel(-1, "Voucher Highlands 30K", "Áp dụng toàn quốc", "30", "3K", "muasam_anuong"));
        ExchangeVoucherAdapter ExVoucherAdapter = new ExchangeVoucherAdapter(ExVoucherList);

    }
    private void setListener() {
        findViewById(R.id.priority_question_contact).setOnClickListener(v->{
            Intent intent=new Intent(PriorityUITpayActivity.this, ProfileWebView.class);
            intent.putExtra("URL_KEY","https://support.zalopay.vn/faq/web/faq-folder-list");
            startActivity(intent);
        });
        findViewById(R.id.priority_rule).setOnClickListener(v->{
            Intent intent=new Intent(PriorityUITpayActivity.this, ProfileWebView.class);
            intent.putExtra("URL_KEY","https://zalopay.vn/dich-vu/zalopay-priority");
            startActivity(intent);
        });
        findViewById(R.id.priority_change_gift).setOnClickListener(v->{
            Intent intent=new Intent(PriorityUITpayActivity.this, GiftExchangeActivity.class);
            startActivity(intent);
            this.finish();
        });
        findViewById(R.id.tv_priority_exchange_showmore).setOnClickListener(v-> {
            Intent intent=new Intent(PriorityUITpayActivity.this, GiftExchangeActivity.class);
            startActivity(intent);
            this.finish();
        });
    }
    private void SetAccountRankingData() {
        List<RankModel> rankList = new ArrayList<>();
        rankList.add(new RankModel(RankModel.RankType.NEW, currentSpending, "Mê hay hông mê?", "5+ đặc quyền"));
        rankList.add(new RankModel(RankModel.RankType.SILVER, currentSpending, "Ưu đãi nạp thẻ", "7+ đặc quyền"));
        rankList.add(new RankModel(RankModel.RankType.GOLD, currentSpending, "Tặng 0.3% sinh lời", "10+ đặc quyền"));
        rankList.add(new RankModel(RankModel.RankType.DIAMOND, currentSpending, "Hoàn tiền không giới hạn", "12+ đặc quyền"));
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
    private void SetCheckInData() {
        List<CheckInModel> checkInList = new ArrayList<>();
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_1, true));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_2, false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_3, false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_4, false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_5, false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_6, false));
        checkInList.add(new CheckInModel(CheckInModel.DayConfig.DAY_7, false));
        PriorityCheckInAdapter adapter = new PriorityCheckInAdapter(checkInList);
        RVCheckIn.setAdapter(adapter);
    }
}
