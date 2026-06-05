package com.example.uitpayapp.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import com.example.uitpayapp.YumYumPriority.PriorityYumYumActivity;
import com.example.uitpayapp.giftexchange.GiftExchangeActivity;
import com.example.uitpayapp.profile.accountPaymentManage.AccountManagementActivity;
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
            Intent intentPriority=new Intent(this, PriorityYumYumActivity.class);
            startActivity(intentPriority);
        });
        findViewById(R.id.profile_show_account_info).setOnClickListener(v->
        {
            Intent intentAccount=new Intent(this, AccountDetailActivity.class);
            startActivity(intentAccount);
        });
        findViewById(R.id.qr_manage_card).setOnClickListener(v->{
            Intent intentQR=new Intent(this, QRScanActivity.class);
            startActivity(intentQR);
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
        ListItems_uudai.add(new MenuItemData("Deal hời cho bạn","",R.drawable.ic_your_deal,false));
        ListItems_uudai.add(new MenuItemData("Ví Voucher", "0 ưu đãi", R.drawable.ic_my_gift,false));
        ListItems_uudai.add(new MenuItemData("Xu tích lũy", "0 xu", R.drawable.ic_my_coin,false));
        ListGroupItem.add(new GroupItemData("Ưu đãi", ListItems_uudai));
        //Nhóm 2: Quản lý tài chính (Mục đặc chứa thành phần đặc biệt)
        List<MenuItemData> ListItems_finance = new ArrayList<>();
        ListItems_finance.add(new MenuItemData("Tài khoản/thẻ liên kết", "", R.drawable.ic_account_card_payment,true));
        ListItems_finance.add(new MenuItemData("Vị trí", "Thêm và sắp xếp các địa chỉ giao hàng của bạn", R.drawable.ic_location,false));
        ListGroupItem.add(new GroupItemData("Quản lý thông tin đơn hàng", ListItems_finance));
        //Nhóm 3: Tiện ích
        List<MenuItemData> ListItems_tienich = new ArrayList<>();
        ListItems_tienich.add(new MenuItemData("Mời bạn bè", "", R.drawable.ic_invite_friend,false));
        ListItems_tienich.add(new MenuItemData("Cửa hàng của bạn", "", R.drawable.ic_my_store,false));
        ListGroupItem.add(new GroupItemData("Tiện ích", ListItems_tienich));
        //Nhóm 4: Hỗ trợ
        List<MenuItemData> ListItems_support = new ArrayList<>();
        ListItems_support.add(new MenuItemData("Trung tâm hỗ trợ", "", R.drawable.ic_contact_support_ver2,false));
        ListItems_support.add(new MenuItemData("Chính sách bảo mật", "", R.drawable.ic_security_user,false));
        ListItems_support.add(new MenuItemData("Cài đặt ứng dụng", "", R.drawable.ic_setting,false));
        ListGroupItem.add(new GroupItemData("Hỗ trợ và Cài đặt", ListItems_support));

        mainMenu.setAdapter(new ProfileMenuAdapter(this, ListGroupItem,ItemClick->HanleItemClick(ItemClick)));

    }
    public void HanleItemClick(MenuItemData item) {
        if (item == null) return;
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems = new ArrayList<>();
        if (item.IsSpecialItem)
        {
            Intent intent=new Intent(this, AccountManagementActivity.class);
            startActivity(intent);
            return;
        }
        switch (item.getTitle()) {
            case "Ví Voucher":
                Intent intentVoucher=new Intent(this,VoucherActivity.class);
                startActivity(intentVoucher);
                break;
            case "Chính sách bảo mật":
                Intent intentSecurity=new Intent(this,ProfileWebView.class);
                intentSecurity.putExtra("URL_KEY","https://help.cs.shopeefood.vn/portal/103/article/73879-Ch%C3%ADnh-s%C3%A1ch-b%E1%BA%A3o-m%E1%BA%ADt");
                startActivity(intentSecurity);
                break;
            case "Cài đặt ứng dụng":
                ListItems.add(new MenuItemData("Cài đặt thông báo","",R.drawable.ic_notification,false));
                ListGroupItem.add(new GroupItemData("Cài đặt cho ứng dụng YumYum",ListItems));
                List<MenuItemData> ListItems2 = new ArrayList<>();
                ListItems2.add(new MenuItemData("Thông tin ứng dụng","",R.drawable.ic_app_information,false));
                ListItems2.add(new MenuItemData("Dọn dẹp bộ nhớ tạm","",R.drawable.ic_clean,false));
                ListGroupItem.add(new GroupItemData("Khác",ListItems2));
                ShowBottomSheet("Cài đặt ứng dụng",ListGroupItem);
                break;
            case "Trung tâm hỗ trợ":
                Intent intentSupport=new Intent(this, ProfileWebView.class);
                intentSupport.putExtra("URL_KEY","https://help.cs.shopeefood.vn/portal/102");
                startActivity(intentSupport);
                break;
            case "Xu tích lũy":
                Intent intentCoin=new Intent(this, GiftExchangeActivity.class);
                startActivity(intentCoin);
                break;
            case "Deal hời cho bạn":
                Intent intentDeal=new Intent(this, com.example.uitpayapp.recommendeddeal.RecommendedDealActivity.class);
                startActivity(intentDeal);
                break;
            case "Mời bạn bè":
                ListItems.add(new MenuItemData("Gửi qua SMS","",R.drawable.ic_bold_check,false));
                ListItems.add(new MenuItemData("Gửi qua Email","",R.drawable.ic_bold_check,false));
                ListItems.add(new MenuItemData("Sao chép đường dẫn tải ứng dụng","",R.drawable.ic_bold_check,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Mời bạn bè",ListGroupItem);
                break;
            case "Vị trí":
                Intent intentLocation=new Intent(this, com.example.uitpayapp.deliveryaddressorder.AddressOrderActivity.class);
                startActivity(intentLocation);
                break;
            case "Cửa hàng của bạn":
                Intent intentStore=new Intent(this, com.example.uitpayapp.registerstore.RegisterStoreActivity.class);
                startActivity(intentStore);
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
        String message="YumYum ứng dụng đặt thức ăn online siêu tiện lợi. Hãy tham gia YumYum ngay để nhận nhiều ưu đãi hấp dẫn";
        switch (item.getTitle()) {
            case "Thông tin ứng dụng":
                Intent intentInfo=new Intent(this,InfoApplication.class);
                startActivity(intentInfo);
                break;
            case "Cài đặt thông báo":
                Intent intentNotification=new Intent(this, NotificationSettings.class);
                startActivity(intentNotification);
                break;
            case "Quét QR":
                Intent intentQR=new Intent(this, QRScanActivity.class);
                startActivity(intentQR);
                break;
            case "Gửi qua SMS":
                Intent intentSMS=new Intent(Intent.ACTION_SENDTO);
                intentSMS.setData(android.net.Uri.parse("smsto:"));
                intentSMS.putExtra("sms_body", message);
                startActivity(intentSMS);
                break;
            case "Gửi qua Email":
                Intent intentEmail=new Intent(Intent.ACTION_SENDTO);
                String subject="Lời mời tham gia YumYum";
                intentEmail.setData(android.net.Uri.parse("mailto:?subject="+android.net.Uri.encode(subject)+"&body=" + android.net.Uri.encode(message)));
                startActivity(intentEmail);
                break;
            case "Sao chép đường dẫn tải ứng dụng":
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(android.content.Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text",message);
                clipboard.setPrimaryClip(clip);
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
            android.content.Intent intent = new android.content.Intent(this, com.example.uitpayapp.history.TransactionHistoryActivity.class);
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
