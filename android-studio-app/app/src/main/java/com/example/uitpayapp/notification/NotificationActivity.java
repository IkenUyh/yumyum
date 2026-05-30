package com.example.uitpayapp.notification;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView rvPromotions;
    private PromoAdapter promoAdapter;
    private List<PromoNotification> promoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification);

        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.bottom_container), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            // Thiết lập phần padding bottom của bottom_container bằng chính độ cao thanh điều hướng
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(findViewById(R.id.bottom_container) != null ? R.id.bottom_container : R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom);
            return insets;
        });

        // Ánh xạ và chuyển hướng màn hình con chính xác như mẫu
        findViewById(R.id.btnNavNews).setOnClickListener(v -> {
            startActivity(new Intent(NotificationActivity.this, NotificationNewsActivity.class));
        });

        findViewById(R.id.btnNavOrders).setOnClickListener(v -> {
            startActivity(new Intent(NotificationActivity.this, NotificationOrderActivity.class));
        });

        rvPromotions = findViewById(R.id.rvPromotions);
        promoList = new ArrayList<>();
        loadPromoDummyData();

        promoAdapter = new PromoAdapter(promoList);
        rvPromotions.setAdapter(promoAdapter);
        rvPromotions.setLayoutManager(new LinearLayoutManager(this));
        setupBottomNavigation();
    }

    private void loadPromoDummyData() {
        promoList.add(new PromoNotification("1",
                "[HCMC, HN, DN] Coca-Cola mời COMBO CỰC HỜI!",
                "⚡ Giảm 48.000Đ đơn từ 100.000Đ\n🥰 Áp dụng combo 2 món + 2 Coca-Cola\n🥤 Uống Coca-Cola mát lạnh, ăn ngon hết sẩy\n⚡ Đặt ngay ShopeeFood bạn nha!",
                "30/05/2026 15:00", android.R.drawable.ic_menu_gallery));

        promoList.add(new PromoNotification("2",
                "[HCMC, HN, DN] Quán mới KHAO BẠN 30.000Đ",
                "🌮 Bánh mì nướng muối ớt, nước long nhãn\n🥥 Sương Sâm Dừa Nước...\n🧡 Ăn xế thảnh thơi, đặt liền bạn ơi!",
                "30/05/2026 13:30", android.R.drawable.ic_menu_gallery));
    }

    private void setupBottomNavigation() {
        LinearLayout navHome = findViewById(R.id.navHome);
        LinearLayout navHistory = findViewById(R.id.navHistory);
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