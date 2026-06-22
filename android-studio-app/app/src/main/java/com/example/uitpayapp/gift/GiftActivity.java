package com.example.uitpayapp.gift;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

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

            //Huong dan doi qua
        TextView tvHuongDan = findViewById(R.id.tvHuongDan);
        tvHuongDan.setOnClickListener(v -> showHuongDanDialog());
        LinearLayout btnLichSu = findViewById(R.id.btnLichSu);
        btnLichSu.setOnClickListener(v -> showHistoryMenu(v));

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
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.history.TransactionHistoryActivity.class);
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

    private void showHuongDanDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.gift_hunt_tutorial);

        // Giúp background trong suốt để bo góc của CardView phát huy tác dụng
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView tvBuoc1 = dialog.findViewById(R.id.tvBuoc1);
        TextView tvBuoc2 = dialog.findViewById(R.id.tvBuoc2);

        // Ánh xạ các nút trong Dialog
        ImageView btnCloseDialog = dialog.findViewById(R.id.btnCloseDialog);
        LinearLayout btnScanQRDialog = dialog.findViewById(R.id.btnScanQRDialog);

        String htmlBuoc1 = "<b>Quét mã QR chuyển tiền ngân hàng thành công </b>để tích xu*";
        String htmlBuoc2 = "Vào <b>Chuyển tiền</b> > chọn <b>Đổi quà</b> > chọn quà bạn muốn đổi";

        // 3. Render HTML và gán vào TextView
        // Cần kiểm tra phiên bản Android vì từ Android N (API 24) cách gọi hàm fromHtml có thay đổi
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvBuoc1.setText(android.text.Html.fromHtml(htmlBuoc1, android.text.Html.FROM_HTML_MODE_LEGACY));
            tvBuoc2.setText(android.text.Html.fromHtml(htmlBuoc2, android.text.Html.FROM_HTML_MODE_LEGACY));
        } else {
            // Hỗ trợ cho các máy Android cũ hơn (dưới API 24)
            tvBuoc1.setText(android.text.Html.fromHtml(htmlBuoc1));
            tvBuoc2.setText(android.text.Html.fromHtml(htmlBuoc2));
        }


        // Tắt popup khi bấm dấu X
        btnCloseDialog.setOnClickListener(v -> dialog.dismiss());

        // Nút quét QR trong Popup
        btnScanQRDialog.setOnClickListener(v -> {
            // TODO: Mở màn hình quét QR ở đây
            Toast.makeText(GiftActivity.this, "Chức năng quét QR đang cập nhật", Toast.LENGTH_SHORT).show();
            dialog.dismiss(); // Tuỳ vào luồng có muốn đóng popup hay không
        });

        dialog.show();
    }


    private void showHistoryMenu(View anchor) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.gift_history_menu, popupMenu.getMenu());

        // Hiển thị icon trên menu (Dành cho Android 10 / API 29 trở lên)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true);
        }

        // Xử lý sự kiện click vào từng item
        popupMenu.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_tich_xu) {
                // TODO: Chuyển sang màn hình Tích/dùng xu
                // Intent intent = new Intent(GiftActivity.this, TichXuActivity.class);
                // startActivity(intent);
                Toast.makeText(this, "Chuyển đến Tích/dùng xu", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.action_qua_da_doi) {
                // TODO: Chuyển sang màn hình Quà đã đổi
                // Intent intent = new Intent(GiftActivity.this, QuaDaDoiActivity.class);
                // startActivity(intent);
                Toast.makeText(this, "Chuyển đến Quà đã đổi", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popupMenu.show();
    }

}
