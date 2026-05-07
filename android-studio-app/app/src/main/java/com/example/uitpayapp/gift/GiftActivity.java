package com.example.uitpayapp.gift;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GiftActivity extends AppCompatActivity {
    RecyclerView rvGifts;
    giftAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gift_hunt);
        setupBottomNavigation();
         rvGifts = findViewById(R.id.rvGifts);
         adapter = new giftAdapter();
        rvGifts.setLayoutManager(new GridLayoutManager(this, 2)); // 2 cột
        rvGifts.setAdapter(adapter);
        loadData();

        //logo brand
        RecyclerView rvLogos = findViewById(R.id.rvLogos);
        rvLogos.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        List<Integer> list = Arrays.asList(
                R.drawable.ic_sanqua_brand1,
                R.drawable.ic_sanqua_brand2,
                R.drawable.ic_sanqua_brand3,
                R.drawable.ic_sanqua_brand4,
                R.drawable.ic_sanqua_brand5,
                R.drawable.ic_sanqua_brand6,
                R.drawable.ic_sanqua_brand7,
                R.drawable.ic_sanqua_brand8
        );
        rvLogos.setAdapter(new LogoAdapter(list));
        //Kiếm xu đổi quà

        RecyclerView rvFeatures = findViewById(R.id.rvFeatures);

        List<exchange> listexchange = new ArrayList<>();
        listexchange.add(new exchange(R.drawable.ic_qr_code_24px,
                "Nhận/Nạp tiền bằng QR", "+5.000", "Còn 1 lần"));

        listexchange.add(new exchange(R.drawable.ic_wallet,
                "Gửi tiết kiệm lần đầu", "+15.000", "Còn 1 lần"));

        listexchange.add(new exchange(R.drawable.ic_phone,
                "Nạp tiền điện thoại", "+1.000", "Còn 1 lần"));

       com.example.uitpayapp.gift.ExchangeAdapter adapterexchange = new ExchangeAdapter(listexchange);

        rvFeatures.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        rvFeatures.setAdapter(adapterexchange);

        //Dịch vụ đề xuất
        RecyclerView rv = findViewById(R.id.rvRecommendService);
        rv.setLayoutManager(new GridLayoutManager(this, 4));
        List<recommendservice> listservice = new ArrayList<>();
        listservice.add(new recommendservice(R.drawable.ic_phone, "Điện thoại"));
        listservice.add(new recommendservice(R.drawable.ic_movie_ticket, "Vé phim"));
        listservice.add(new recommendservice(R.drawable.ic_giaodich_1, "Nhận tiền"));
        listservice.add(new recommendservice(R.drawable.ic_internet, "Nhận tiền quốc tế"));
        listservice.add(new recommendservice(R.drawable.ic_gamepad, "Trò chơi"));
        listservice.add(new recommendservice(R.drawable.ic_gift, "Mở quà"));
        listservice.add(new recommendservice(R.drawable.ic_chart, "Tài chính"));
        listservice.add(new recommendservice(R.drawable.ic_wallet, "Trả sau"));
        RecommendServiceAdapter adapterservice = new RecommendServiceAdapter(listservice);
        rv.setAdapter(adapterservice);

        //banner quảng cáo
            RecyclerView rvBanner = findViewById(R.id.rvBanner);
            rvBanner.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            );
            List<Integer> banners = Arrays.asList(
                    R.drawable.ic_sanqua_uudai1,
                    R.drawable.ic_sanqua_uudai2,
                    R.drawable.ic_sanqua_uudai3
            );
            BannerAdapter adapter = new BannerAdapter(banners);
            rvBanner.setAdapter(adapter);
            PagerSnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(rvBanner);

    }


    private void loadData() {
        List<gift> list = new ArrayList<>();
        String desc="Quét QR chuyển khoản để đổi";
        list.add(new gift(1, R.drawable.ic_sanqua_iphone, "iPhone vjp pro", desc));
        list.add(new gift(2, R.drawable.ic_sanqua_dongho, "Apple Watch vjp vjp", desc));
        list.add(new gift(3, R.drawable.ic_sanqua_iphone, "Tommy Xiaomi", desc));
        list.add(new gift(4, R.drawable.ic_sanqua_iphone, "Samsungsungsung", desc));
        adapter.submitList(list);
    }
    private void setupBottomNavigation() {
        android.widget.LinearLayout navHome = findViewById(R.id.navHome);
        android.widget.LinearLayout navHistory = findViewById(R.id.navHistory);
        android.widget.LinearLayout navGift = findViewById(R.id.navGift);
        android.widget.LinearLayout navAccount = findViewById(R.id.navAccount);

        // 1. Luồng bấm về TRANG CHỦ
        navHome.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.home.HomeActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // 2. Luồng bấm qua LỊCH SỬ
        navHistory.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.transaction.TransactionHistoryActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // 4. Luồng bấm qua TÀI KHOẢN
        navAccount.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.profile.ProfileActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }
}