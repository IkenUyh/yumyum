package com.example.uitpayapp.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;
import com.example.uitpayapp.ScanQRCode.QRScanActivity;
import com.example.uitpayapp.UITpayPriority.PriorityUITpayActivity;
import com.example.uitpayapp.gift.GiftActivity;
import com.example.uitpayapp.insurance.InsuranceActivity;
import com.example.uitpayapp.voucher.VoucherActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    RecyclerView mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        TextView pagetitle = findViewById(R.id.pagetilte);
        mainMenu = findViewById(R.id.main_menu);
        ConstraintLayout navBottom = findViewById(R.id.bottomNavContainer);
        ViewCompat.setOnApplyWindowInsetsListener(pagetitle, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (navBottom != null) {
                navBottom.setPadding(navBottom.getPaddingLeft(), navBottom.getPaddingTop(), navBottom.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        // =========================================================
        // === THÊM MỚI: LOAD DATA TỪ SHAREDPREFERENCES & GLIDE ===
        // =========================================================
        TextView tvName = findViewById(R.id.tv_account_name);
        TextView tvPhone = findViewById(R.id.tv_account_phone);
        ImageView ivAvatar = findViewById(R.id.iv_account_avatar);

        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("FULL_NAME", "Người dùng ZaloPay");
        String savedPhone = sharedPreferences.getString("PHONE_NUMBER", "");
        String savedAvatar = sharedPreferences.getString("AVATAR_URL", "");

        if (tvName != null) tvName.setText(savedName);
        if (tvPhone != null) tvPhone.setText(savedPhone);

        // Load ảnh và bo tròn tự động
        if (ivAvatar != null && !savedAvatar.isEmpty()) {
            Glide.with(this)
                    .load(savedAvatar)
                    .circleCrop()
                    .into(ivAvatar);
        }
        findViewById(R.id.profile_uitpay_priority).setOnClickListener(v->
        {
            Intent intentPriority=new Intent(this, PriorityUITpayActivity.class);
            startActivity(intentPriority);
        });
        findViewById(R.id.profile_show_account_info).setOnClickListener(v->
        {
            Intent intentAccount=new Intent(this, AccountDetailActivity.class);
            startActivity(intentAccount);
        });
        SetDataMainMenu(mainMenu);
        setupBottomNavigation();
    }

    void SetDataMainMenu(RecyclerView mainMenu) {
        if (mainMenu == null) return;
        mainMenu.setLayoutManager(new LinearLayoutManager(this));
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        //Nhóm 1: Ưu đãi
        List<MenuItemData> ListItems_uudai = new ArrayList<>();
        ListItems_uudai.add(new MenuItemData("Quà của tôi", "0 ưu đãi", R.drawable.ic_my_gift,false));
        ListItems_uudai.add(new MenuItemData("Xu tích lũy", "0 xu", R.drawable.ic_my_coin,false));
        ListGroupItem.add(new GroupItemData("Ưu đãi", ListItems_uudai));
        //Nhóm 2: Quản lý tài chính (Mục đặc chứa thành phần đặc biệt)
        List<MenuItemData> ListItems_finance = new ArrayList<>();
        ListItems_finance.add(new MenuItemData("Tài khoản/thẻ liên kết", "", R.drawable.ic_account_card_payment,true));
        ListItems_finance.add(new MenuItemData("Cài đặt thanh toán tự động", "Sắp xếp nguồn tiền, cài đặt dịch vụ", R.drawable.ic_payment,false));
        ListGroupItem.add(new GroupItemData("Quản lý tài chính", ListItems_finance));
        //Nhóm 3: Hỗ trợ
        List<MenuItemData> ListItems_support = new ArrayList<>();
        ListItems_support.add(new MenuItemData("Trung tâm hỗ trợ", "", R.drawable.ic_contact_support_ver2,false));
        ListItems_support.add(new MenuItemData("Trung tâm bảo mật", "", R.drawable.ic_security_user,false));
        ListItems_support.add(new MenuItemData("Cài đặt ứng dụng", "", R.drawable.ic_setting,false));
        ListGroupItem.add(new GroupItemData("Hỗ trợ và Cài đặt", ListItems_support));

        mainMenu.setAdapter(new ProfileMenuAdapter(this, ListGroupItem,ItemClick->HanleItemClick(ItemClick)));

        findViewById(R.id.qr_manage_card).setOnClickListener(v->
        {
            List<MenuItemData> ListItems_QRManger = new ArrayList<>();
            List<GroupItemData> ListGroupItemQRManger = new ArrayList<>();
            ListItems_QRManger.add(new MenuItemData("Mã nhận tiền","Chia sẻ mã này để nhận tiền những người xung quanh",R.drawable.ic_receive,false));
            ListItems_QRManger.add(new MenuItemData("Mã thanh toán","Đưa mã này cho thu ngân cửa hàng để thanh toán nhé",R.drawable.ic_qr_code,false));
            ListItems_QRManger.add(new MenuItemData("Quét QR","Hướng camera vào mã QR để quyét",R.drawable.ic_scan,false));
            ListGroupItemQRManger.add(new GroupItemData("Các mã QR quan trọng",ListItems_QRManger));
            ShowBottomSheet("Quản lý mã",ListGroupItemQRManger);
        });

    }
    public void HanleItemClick(MenuItemData item) {
        if (item == null) return;
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems = new ArrayList<>();
        if (item.IsSpecialItem)
        {
            Intent intent=new Intent(this,AccountManagementActivity.class);
            startActivity(intent);
            return;
        }
        switch (item.getTitle()) {
            case "Cài đặt thanh toán tự động":
                ListItems.add(new MenuItemData("Thanh toán dịch vụ tự động","Sắp xếp thứ tự ưu tiên thẻ/tài khoản",R.drawable.ic_sort_payment,false));
                ListItems.add(new MenuItemData("Thanh toán hóa đơn tự động","",R.drawable.ic_receipt,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Cài đặt thanh toán tự động",ListGroupItem);
                break;
            case "Trung tâm bảo mật":
                ListItems.add(new MenuItemData("Bảo mật tài khoản","",R.drawable.ic_security_user,false));
                ListItems.add(new MenuItemData("Bảo mật giao dịch","",R.drawable.ic_security_transaction,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Trung tâm bảo mật",ListGroupItem);
                break;
            case "Cài đặt ứng dụng":
                ListItems.add(new MenuItemData("Cài đặt thông báo","",R.drawable.ic_notification,false));
                ListGroupItem.add(new GroupItemData("Cài đặt cho ứng dụng UITpay",ListItems));
                List<MenuItemData> ListItems2 = new ArrayList<>();
                ListItems2.add(new MenuItemData("Thông tin ứng dụng","",R.drawable.ic_uitpay_information,false));
                ListItems2.add(new MenuItemData("Dọn dẹp bộ nhớ tạm","",R.drawable.ic_clean,false));
                ListGroupItem.add(new GroupItemData("Khác",ListItems2));
                ShowBottomSheet("Cài đặt ứng dụng",ListGroupItem);
                break;
            case "Quản lý tài chính":
                ListItems.add(new MenuItemData("Tài khoản/thẻ liên kết","",R.drawable.ic_account_card_payment,false));
                ListItems.add(new MenuItemData("Cài đặt thanh toán tự động","Sắp xếp nguồn tiền, cài đặt dịch vụ",R.drawable.ic_payment,false));
                ListItems.add(new MenuItemData("Điểm tin cậy UITpay","",R.drawable.ic_reliable_score,false));
                ListItems.add(new MenuItemData("các liên kết ngân hàng khác","",R.drawable.ic_link_bank,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Quản lý tài chính",ListGroupItem);
                break;
            case "Quà của tôi":
                Intent intentVoucher=new Intent(this, VoucherActivity.class);
                startActivity(intentVoucher);
                break;
            case "Trung tâm hỗ trợ":
                Intent intentSupport=new Intent(this, ProfileWebView.class);
                intentSupport.putExtra("URL_KEY","https://support.zalopay.vn/faq/web");
                startActivity(intentSupport);
                break;
        }
    }
    public static void SetDetailMenuItem(View item, String item_title, String item_subtitle, int item_icon) {
        if (item == null) return;
        TextView titleTv = item.findViewById(R.id.menu_title);
        TextView subtitleTv = item.findViewById(R.id.menu_subtitle);
        ImageView iconIv = item.findViewById(R.id.menu_icon);

        if (titleTv != null) titleTv.setText(item_title);
        if (item_icon==-1)
        {
            iconIv.setVisibility(View.GONE);
        } else
            iconIv.setImageResource(item_icon);
        if (subtitleTv != null) {
            if (item_subtitle != null && !item_subtitle.isEmpty()) {
                subtitleTv.setVisibility(View.VISIBLE);
                subtitleTv.setText(item_subtitle);
            } else {
                subtitleTv.setVisibility(View.GONE);
            }
        }
    }
    //Tam thoi code thu cong do du lieu con it
    private void ShowBottomSheet(String SheetTilte, List<GroupItemData> ListGroupItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View SheetView = getLayoutInflater().inflate(R.layout.layout_dynamic_bottom_sheet, null);
        ((ImageView) SheetView.findViewById(R.id.btn_close)).setOnClickListener(v -> bottomSheetDialog.dismiss());
        ((TextView) SheetView.findViewById(R.id.sheet_title)).setText(SheetTilte);
        LinearLayout container = (LinearLayout) SheetView.findViewById(R.id.sheet_container);
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        recyclerView.setBackgroundColor(android.graphics.Color.parseColor("#f1f5ff"));
        recyclerView.setPadding(16,16,16,16);
        container.addView(recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProfileMenuAdapter(this, ListGroupItem,ItemClick->HanleDetailItemClick(ItemClick)));
        bottomSheetDialog.setContentView(SheetView);
        bottomSheetDialog.show();
    }
    private void HanleDetailItemClick(MenuItemData item) {
        switch (item.getTitle()) {
            case "Thông tin ứng dụng":
                Intent intentInfo=new Intent(this,InfoApplication.class);
                startActivity(intentInfo);
                break;
            case "Cài đặt thông báo":
                Intent intentNotification=new Intent(this, NotificationSettings.class);
                startActivity(intentNotification);
                break;
            case "Thanh toán hóa đơn tự động":
                Intent intentAutoPay=new Intent(this, AutoPaymentActivity.class);
                startActivity(intentAutoPay);
                break;
            case "Bảo mật tài khoản":
                Intent intentSecurity=new Intent(this, SecuritySettingsActivity.class);
                startActivity(intentSecurity);
                break;
            case "Quét QR":
                Intent intentQR=new Intent(this, QRScanActivity.class);
                startActivity(intentQR);
                break;
            case "Bảo mật giao dịch":
                Intent intentTransaction=new Intent(this, SecurityTransactionActivity.class);
                startActivity(intentTransaction);
                break;
            case "Hợp đồng bảo hiểm":
                Intent intentInsurance=new Intent(this, InsuranceActivity.class);
                startActivity(intentInsurance);
                break;
        }
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

        // 3. Luồng bấm qua SĂN QUÀ
        navGift.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.gift.GiftActivity.class);
            intent.addFlags(android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
    }
}
