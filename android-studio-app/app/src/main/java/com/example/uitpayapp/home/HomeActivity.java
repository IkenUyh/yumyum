package com.example.uitpayapp.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import com.example.uitpayapp.R;
import com.example.uitpayapp.suggestion.SuggestAdapter;
import com.example.uitpayapp.suggestion.SuggestionModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // =========================================================
        // 1. ÁNH XẠ VIEW VÀ CẤU HÌNH CƠ BẢN
        // =========================================================
        RecyclerView rvDichVuPhoBien = findViewById(R.id.rvDichVuPhoBien);

        rvDichVuPhoBien.setClipChildren(false);
        rvDichVuPhoBien.setClipToPadding(false);


        // =========================================================
        // 2. THIẾT LẬP DANH SÁCH: DỊCH VỤ PHỔ BIẾN
        // =========================================================
        List<ServiceItem> listPhobien = new ArrayList<>();
        listPhobien.add(new ServiceItem("Điện thoại", R.drawable.ic_phone, null));
        listPhobien.add(new ServiceItem("Hóa đơn", R.drawable.ic_receipt, null));
        listPhobien.add(new ServiceItem("Quỹ tiết kiệm", R.drawable.ic_table, null));
        listPhobien.add(new ServiceItem("Đặt đồ ăn", R.drawable.ic_food, null));
        listPhobien.add(new ServiceItem("Vé phim", R.drawable.ic_film, null));
        listPhobien.add(new ServiceItem("Dò vé số", R.drawable.ic_lottery, null));
        listPhobien.add(new ServiceItem("Lì xì", R.drawable.ic_suitcase, null));
        listPhobien.add(new ServiceItem("Mua thẻ Game", R.drawable.ic_game, null));

        ServiceAdapter adapterPhobien = new ServiceAdapter(listPhobien, R.layout.item_service);
        rvDichVuPhoBien.setAdapter(adapterPhobien);

        // =========================================================
        // 3. THIẾT LẬP DANH SÁCH: BANNER ĐỀ XUẤT (DẠNG NGANG)
        // =========================================================
        RecyclerView rcvSuggest = findViewById(R.id.rcvSuggest);
        rcvSuggest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        List<SuggestionModel> listNgang = new ArrayList<>();
        listNgang.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL, R.drawable.img_usagi,"Phất cờ nhận xu, rinh ngay quà khủng!","Mừng đại lễ 30/04, hàng ngàn quà hấp d..."  ));
        listNgang.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL,R.drawable.img_usagi,"Mở tài khoản thanh toán VPBank NEO","Cơ hội nhận đến 210.000đ khi mở tài khoản"));
        listNgang.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL,R.drawable.img_usagi,"Săn vé xem phim cực rẻ","Giảm ngay 50% cho bạn mới sử dụng Zalopay" ));
        listNgang.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL, R.drawable.img_usagi,"Thanh toán hóa đơn trúng vàng","Cơ hội trúng 1 chỉ vàng SJC mỗi tuần"));
        listNgang.add(new SuggestionModel(SuggestionModel.TYPE_HORIZONTAL,R.drawable.img_usagi, "Du lịch xả hơi, ưu đãi tới bến","Hoàn tiền 10% khi đặt vé máy bay và khách sạn"));

        SuggestAdapter adapter = new SuggestAdapter(listNgang);
        rcvSuggest.setAdapter(adapter);

        // =========================================================
        // 4. THIẾT LẬP DANH SÁCH: ƯU ĐÃI SIÊU HỜI (DẠNG LƯỚI 2 CỘT)
        // =========================================================
        RecyclerView rvUuDaiSieuHoi = findViewById(R.id.rvUuDaiSieuHoi);
        rvUuDaiSieuHoi.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));

        List<SuggestionModel> listDoc = new ArrayList<>();
        listDoc.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL,R.drawable.img_miku,"Mở tài khoản thanh toán VPBank NEO - Cơ hội n...","" ));
        listDoc.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL,R.drawable.img_miku,"Cơ hội nhận đến 130.000đ cùng MSB trên...",""));
        listDoc.add(new SuggestionModel( SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku,"Vay tiền nhanh, online 100% giải ngân tức thì","" ));
        listDoc.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL,R.drawable.img_miku,"Nhận đến 220.000đ khi mở tài khoản Shinhan...",""));
        listDoc.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL,R.drawable.img_miku,"Thần Ma Tu Tiên đã ra mắt - Tặng code tân thủ",""));
        listDoc.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL, R.drawable.img_miku, "Lễ lớn, không chờ lương - Đi đãaaaa trả sau!",""));
        listDoc.add(new SuggestionModel(SuggestionModel.TYPE_VERTICAL,R.drawable.img_miku,"Giảm 50% mua vé máy bay Bamboo Airways", ""));

        SuggestAdapter adapterUuDai = new SuggestAdapter(listDoc);
        rvUuDaiSieuHoi.setAdapter(adapterUuDai);

        // =========================================================
        // 5. CHUYỂN TRANG
        // =========================================================
        android.widget.LinearLayout btnChuyenTien = findViewById(R.id.btnChuyenTien);
        btnChuyenTien.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(HomeActivity.this, com.example.uitpayapp.home.money_transfer.MoneyTransferActivity.class);
            startActivity(intent);
        });

        android.widget.LinearLayout btnQRCuaToi = findViewById(R.id.btnQRCuaToi);
        btnQRCuaToi.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(HomeActivity.this, com.example.uitpayapp.home.my_qr.MyQRActivity.class);
            startActivity(intent);
        });

        android.widget.LinearLayout btnNapRut = findViewById(R.id.btnNapRut);
        btnNapRut.setOnClickListener(v-> {
            android.content.Intent intent = new android.content.Intent(HomeActivity.this, com.example.uitpayapp.home.deposit_withdraw.DepositWithdrawActivity.class);
            startActivity(intent);
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        android.widget.LinearLayout navHome = findViewById(R.id.navHome);
        android.widget.LinearLayout navHistory = findViewById(R.id.navHistory);
        android.widget.LinearLayout navGift = findViewById(R.id.navGift);
        android.widget.LinearLayout navAccount = findViewById(R.id.navAccount);

        // 2. Luồng bấm qua LỊCH SỬ
        navHistory.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.transaction.TransactionHistoryActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // Kéo màn hình lên chứ đéo đẻ thêm để nhẹ máy
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        // 3. Luồng bấm qua SĂN QUÀ
        navGift.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.gift.GiftActivity.class);
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