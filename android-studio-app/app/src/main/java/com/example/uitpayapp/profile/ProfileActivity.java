package com.example.uitpayapp.profile;

// Feature: Wallet PIN entry and Topup Status check
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.activity.EdgeToEdge;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;
import com.example.uitpayapp.YumYumPriority.PriorityYumYumActivity;
import com.example.uitpayapp.auth.SignInActivity;
import com.example.uitpayapp.giftexchange.GiftExchangeActivity;
import com.example.uitpayapp.merchant.home.SellerHomeActivity;

import com.example.uitpayapp.voucher.VoucherActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.example.uitpayapp.network.SessionManager;
import com.example.uitpayapp.modules.user.UserRepository;
import com.example.uitpayapp.modules.user.models.responses.UserResponseDTO;
import com.example.uitpayapp.registerstore.RegisterStoreActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    RecyclerView mainMenu;
    View rlLoginProfile,llUserInfo;
    boolean isLogin = false;
    private long currentCoins = 0;
    private int currentVouchersCount = 0;
    private String currentRankName = "Thành viên";
    private java.math.BigDecimal previousBalance = java.math.BigDecimal.ZERO;
    private android.app.AlertDialog statusDialog = null;
    private android.content.BroadcastReceiver badgeUpdateReceiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_screen);
        TextView pagetitle = findViewById(R.id.pagetilte);
        mainMenu = findViewById(R.id.main_menu);
        ConstraintLayout navBottom = findViewById(R.id.bottomNavContainer);
        ViewCompat.setOnApplyWindowInsetsListener(pagetitle, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top, systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (navBottom != null) {
                navBottom.setPadding(navBottom.getPaddingLeft(), navBottom.getPaddingTop(), navBottom.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        rlLoginProfile = findViewById(R.id.rl_login_profile);
        llUserInfo = findViewById(R.id.ll_user_info);
        checkLoginStatus();
        setListener();
        SetDataMainMenu(mainMenu);
        setupBottomNavigation();
    }
    void checkLoginStatus()
    {
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn())
        {
            isLogin=true;
            rlLoginProfile.setVisibility(View.GONE);
            llUserInfo.setVisibility(View.VISIBLE);
            View cvLogout = findViewById(R.id.cv_logout);
            if (cvLogout != null) cvLogout.setVisibility(View.VISIBLE);
            TextView tvName = findViewById(R.id.tv_account_name);
            TextView tvPhone = findViewById(R.id.tv_account_phone);
            ImageView ivAvatar = findViewById(R.id.iv_account_avatar);

            String savedName = sessionManager.getUserName();
            String savedPhone = sessionManager.getUserPhone();
            String savedAvatar = sessionManager.getUserAvatar();

            if (tvName != null) tvName.setText(savedName);
            if (tvPhone != null) tvPhone.setText(savedPhone);

            // Load ảnh và bo tròn tự động
            if (ivAvatar != null) {
                if (!savedAvatar.isEmpty()) {
                    Glide.with(this)
                            .load(savedAvatar)
                            .circleCrop()
                            .into(ivAvatar);
                } else {
                    Glide.with(this)
                            .load(R.drawable.yumyum_demo_logo)
                            .circleCrop()
                            .into(ivAvatar);
                }
            }

            // Gọi API lấy dữ liệu mới nhất
            fetchUserProfileApi();
        } else
        {
            isLogin=false;
            rlLoginProfile.setVisibility(View.VISIBLE);
            llUserInfo.setVisibility(View.GONE);
            View cvLogout = findViewById(R.id.cv_logout);
            if (cvLogout != null) cvLogout.setVisibility(View.GONE);
        }
    }
    void fetchUserProfileApi() {
        UserRepository repo = new UserRepository();
        repo.getProfile(new com.example.uitpayapp.network.ApiCallback<UserResponseDTO>() {
            @Override
            public void onSuccess(UserResponseDTO data) {
                if (data != null && isLogin) {
                    TextView tvName = findViewById(R.id.tv_account_name);
                    TextView tvPhone = findViewById(R.id.tv_account_phone);
                    ImageView ivAvatar = findViewById(R.id.iv_account_avatar);

                    if (tvName != null) tvName.setText(data.getFullName());
                    if (tvPhone != null) tvPhone.setText(data.getPhoneNumber());

                    if (ivAvatar != null && data.getAvatarUrl() != null && !data.getAvatarUrl().isEmpty()) {
                        Glide.with(ProfileActivity.this)
                                .load(data.getAvatarUrl())
                                .circleCrop()
                                .into(ivAvatar);
                    }

                    // Đồng bộ với local cache
                    SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    sharedPreferences.edit()
                            .putString("FULL_NAME", data.getFullName())
                            .putString("PHONE_NUMBER", data.getPhoneNumber())
                            .putString("AVATAR_URL", data.getAvatarUrl() != null ? data.getAvatarUrl() : "")
                            .putString("REFERRAL_CODE", data.getReferralCode() != null ? data.getReferralCode() : "")
                            .apply();

                    SessionManager sessionManager = SessionManager.getInstance(ProfileActivity.this);
                    sessionManager.updateProfileSession(
                            data.getFullName(),
                            data.getAvatarUrl(),
                            data.getEmail()
                    );
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Fail silently
            }
        });
    }
    void setListener()
    {
        findViewById(R.id.profile_show_account_info).setOnClickListener(v->
        {
            Intent intentAccount=new Intent(this, AccountDetailActivity.class);
            startActivity(intentAccount);
        });
        findViewById(R.id.btn_logout).setOnClickListener(v->{
            SessionManager sessionManager = SessionManager.getInstance(this);
            String fcmToken = sessionManager.getFcmToken();
            if (fcmToken != null) {
                new com.example.uitpayapp.modules.notification.NotificationRepository().deregisterFcmToken(fcmToken, new com.example.uitpayapp.network.ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        performLogout();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        performLogout();
                    }
                });
            } else {
                performLogout();
            }
        });
        findViewById(R.id.btn_login_profile).setOnClickListener(v->
        {
            Intent intentLogin=new Intent(this, SignInActivity.class);
            startActivity(intentLogin);
        });
        badgeUpdateReceiver = com.example.uitpayapp.utils.NotificationBadgeHelper.registerBadgeReceiver(this, () -> {
            updateNotificationBadge();
        });
    }

    private void performLogout() {
        SessionManager.getInstance(this).clearSession();
        SharedPreferences.Editor editor = getSharedPreferences("UserPrefs", MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
        checkLoginStatus();
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, com.example.uitpayapp.home.HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    void SetDataMainMenu(RecyclerView mainMenu) {
        if (mainMenu == null) return;
        mainMenu.setLayoutManager(new LinearLayoutManager(this));
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        SessionManager session = SessionManager.getInstance(this);
        List<MenuItemData> ListItems_uudai = new ArrayList<>();
        ListItems_uudai.add(new MenuItemData("YumYum Priority", currentRankName, R.drawable.ic_priority_yumyum, false));
        if (session.isLoggedIn()) {
            ListItems_uudai.add(new MenuItemData("Ví Voucher", currentVouchersCount + " ưu đãi", R.drawable.ic_my_gift,false));
        }
        ListItems_uudai.add(new MenuItemData("Xu tích lũy", currentCoins + " xu", R.drawable.ic_my_coin,false));
        ListGroupItem.add(new GroupItemData("Ưu đãi", ListItems_uudai));
        //Nhóm 2: Quản lý tài chính (Mục đặc chứa thành phần đặc biệt)
        List<MenuItemData> ListItems_finance = new ArrayList<>();
        ListItems_finance.add(new MenuItemData("Nguồn tiền", "", R.drawable.ic_account_card_payment,true));
        ListItems_finance.add(new MenuItemData("Vị trí", "", R.drawable.ic_location,false));
        ListGroupItem.add(new GroupItemData("Quản lý thông tin đơn hàng", ListItems_finance));
        //Nhóm 3: Tiện ích
        List<MenuItemData> ListItems_tienich = new ArrayList<>();
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String refCode = userPrefs.getString("REFERRAL_CODE", "");
        String inviteSubtitle = refCode.isEmpty() ? "" : "Mã mời: " + refCode;
        ListItems_tienich.add(new MenuItemData("Mời bạn bè", inviteSubtitle, R.drawable.ic_invite_friend,false));
        if (session.isLoggedIn()) {
            ListItems_tienich.add(new MenuItemData("Cửa hàng", "", R.drawable.ic_my_store,false));
        }
        if (session.isLoggedIn() && "ADMIN".equalsIgnoreCase(session.getUserRole())) {
            MenuItemData adminMenuItem = new MenuItemData("Quản lý duyệt", "Đang tải...", R.drawable.list_alt_24px,false);
            ListItems_tienich.add(adminMenuItem);
            
            // Fetch pending requests to show count
            com.example.uitpayapp.modules.merchant.MerchantRepository merchantRepo = new com.example.uitpayapp.modules.merchant.MerchantRepository();
            merchantRepo.getPendingRequests(new com.example.uitpayapp.network.ApiCallback<java.util.List<com.example.uitpayapp.modules.merchant.models.responses.MerchantRequestResponseDTO>>() {
                @Override
                public void onSuccess(java.util.List<com.example.uitpayapp.modules.merchant.models.responses.MerchantRequestResponseDTO> data) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        int count = data != null ? data.size() : 0;
                        adminMenuItem.setSubtitle(count + " đơn chờ duyệt");
                        if (mainMenu.getAdapter() != null) {
                            mainMenu.getAdapter().notifyDataSetChanged();
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                        adminMenuItem.setSubtitle("chờ duyệt");
                        if (mainMenu.getAdapter() != null) {
                            mainMenu.getAdapter().notifyDataSetChanged();
                        }
                    });
                }
            });
        }
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
        
        if (item.getTitle().equals("Quản lý duyệt")) {
            SessionManager session = SessionManager.getInstance(this);
            if (!session.isLoggedIn() || !"ADMIN".equalsIgnoreCase(session.getUserRole())) {
                Toast.makeText(this, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intentAdmin = new Intent(this, com.example.uitpayapp.admin.AdminApprovalActivity.class);
            startActivity(intentAdmin);
            return;
        }

        if (!isLogin)
        {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems = new ArrayList<>();
        if (item.IsSpecialItem)
        {
            return;
        }
        switch (item.getTitle()) {
            case "YumYum Priority":
                Intent intentPriority=new Intent(this, PriorityYumYumActivity.class);
                startActivity(intentPriority);
                break;
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
            case "Mời bạn bè":
                ListItems.add(new MenuItemData("Gửi qua SMS","",R.drawable.ic_bold_check,false));
                ListItems.add(new MenuItemData("Gửi qua Email","",R.drawable.ic_bold_check,false));
                ListGroupItem.add(new GroupItemData("",ListItems));
                ShowBottomSheet("Mời bạn bè",ListGroupItem);
                break;
            case "Vị trí":
                Intent intentLocation=new Intent(this, com.example.uitpayapp.deliveryaddressorder.AddressOrderActivity.class);
                startActivity(intentLocation);
                break;
            case "Cửa hàng":
                showStoreOptionsBottomSheet();
                break;
        }
    }

    private void showStoreOptionsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_add_menu_selection, null);
        dialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        TextView btnAddCategory = view.findViewById(R.id.btn_add_category);
        TextView btnAddItem = view.findViewById(R.id.btn_add_item);
        TextView btnCancel = view.findViewById(R.id.btn_cancel_sheet);

        tvTitle.setText("Cửa hàng");
        btnAddCategory.setText("Tạo cửa hàng mới");
        btnAddItem.setText("Cửa hàng của bạn");

        btnAddCategory.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(ProfileActivity.this, RegisterStoreActivity.class);
            startActivity(intent);
        });

        btnAddItem.setOnClickListener(v -> {
            dialog.dismiss();
            showStoreSelectionDialog();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
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
        SharedPreferences userPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String refCode = userPrefs.getString("REFERRAL_CODE", "");
        String message="YumYum ứng dụng đặt thức ăn online siêu tiện lợi. Hãy tham gia YumYum ngay để nhận nhiều ưu đãi hấp dẫn.";
        if (!refCode.isEmpty()) {
            message += " Nhập mã mời " + refCode + " để nhận quà nhé!";
        }
        switch (item.getTitle()) {
            case "Thông tin ứng dụng":
                Intent intentInfo=new Intent(this,InfoApplication.class);
                startActivity(intentInfo);
                break;
            case "Cài đặt thông báo":
                Intent intentNotification=new Intent(this, NotificationSettings.class);
                startActivity(intentNotification);
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
        }
    }
    private void setupBottomNavigation() {
        android.widget.LinearLayout navHome = findViewById(R.id.navHome);
        android.widget.LinearLayout navHistory = findViewById(R.id.navHistory);
        android.widget.LinearLayout navFavorite = findViewById(R.id.navFavorite);
        android.widget.LinearLayout navNotification = findViewById(R.id.navNotification);
        android.widget.LinearLayout navAccount = findViewById(R.id.navAccount);

        navHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.home.HomeActivity.class);
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

        navFavorite.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.favorite.FavoriteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        navNotification.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.uitpayapp.notification.NotificationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        
        // Hiện tại đang ở Account (Profile), không cần chuyển
    }
    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
        updateNotificationBadge();
        fetchLoyaltyData();
        fetchVouchersData();

        SharedPreferences paymentPrefs = getSharedPreferences("PaymentPrefs", MODE_PRIVATE);
        boolean isChecking = paymentPrefs.getBoolean("IS_CHECKING_TOP_UP", false);
        if (isChecking) {
            String prevBalanceStr = paymentPrefs.getString("PREVIOUS_BALANCE", "0");
            java.math.BigDecimal prevBalance = new java.math.BigDecimal(prevBalanceStr);
            checkTransactionResult(prevBalance);
        }
    }

    private void fetchVouchersData() {
        if (!isLogin) {
            currentVouchersCount = 0;
            return;
        }
        new com.example.uitpayapp.voucher.VoucherRepository().getActiveVouchers(new com.example.uitpayapp.network.ApiCallback<java.util.List<com.example.uitpayapp.voucher.VoucherResponseDTO>>() {
            @Override
            public void onSuccess(java.util.List<com.example.uitpayapp.voucher.VoucherResponseDTO> data) {
                if (data != null) {
                    runOnUiThread(() -> {
                        currentVouchersCount = data.size();
                        SetDataMainMenu(mainMenu);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Fail silently
            }
        });
    }

    private void fetchLoyaltyData() {
        if (!isLogin) {
            currentCoins = 0;
            currentRankName = "Thành viên";
            return;
        }
        new com.example.uitpayapp.modules.loyalty.LoyaltyRepository().getMyLoyaltyInfo(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.loyalty.models.LoyaltyResponseDTO data) {
                if (data != null) {
                    runOnUiThread(() -> {
                        if (data.getCurrentPoints() != null) {
                            currentCoins = data.getCurrentPoints();
                        }
                        if (data.getRankName() != null) {
                            switch (data.getRankName()) {
                                case "DIAMOND": currentRankName = "Hạng Kim Cương"; break;
                                case "GOLD": currentRankName = "Hạng Vàng"; break;
                                case "SILVER": currentRankName = "Hạng Bạc"; break;
                                default: currentRankName = "Hạng Thành Viên"; break;
                            }
                        }
                        SetDataMainMenu(mainMenu);
                    });
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Fail silently
            }
        });
    }

    private void updateNotificationBadge() {
        com.example.uitpayapp.utils.NotificationBadgeHelper.updateBadge(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (badgeUpdateReceiver != null) {
            unregisterReceiver(badgeUpdateReceiver);
        }
    }

    public void showTopUpDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nạp tiền vào ví qua VNPay");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Nhập số tiền cần nạp (tối thiểu 10,000 VNĐ)");
        builder.setView(input);

        builder.setPositiveButton("Nạp VNPay", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                long amount = Long.parseLong(amountStr);
                if (amount < 10000) {
                    Toast.makeText(this, "Số tiền tối thiểu là 10,000 VNĐ", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
                
                // Lấy số dư hiện tại trước khi gọi API nạp
                walletRepo.getBalance(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse>() {
                    @Override
                    public void onSuccess(com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse balanceData) {
                        if (balanceData != null && balanceData.getBalance() != null) {
                            previousBalance = balanceData.getBalance();
                        }
                        
                        // Tiếp tục gọi API tạo giao dịch nạp tiền
                        walletRepo.createVNPayTopUp(amount, new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Object>>() {
                            @Override
                            public void onSuccess(java.util.Map<String, Object> data) {
                                runOnUiThread(() -> {
                                    if (data != null && data.containsKey("paymentUrl")) {
                                        String paymentUrl = (String) data.get("paymentUrl");
                                        
                                        // Lưu thông tin trước khi nạp để check tự động khi onResume
                                        getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                                             .putString("PREVIOUS_BALANCE", previousBalance.toString())
                                             .putBoolean("IS_CHECKING_TOP_UP", true)
                                             .apply();

                                        // Mở trang thanh toán VNPay
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(paymentUrl));
                                        startActivity(browserIntent);
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Không lấy được link thanh toán VNPay", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onError(String errorMessage) {
                                runOnUiThread(() -> {
                                    Toast.makeText(ProfileActivity.this, "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Nếu lấy số dư lỗi, vẫn cho phép tạo link nạp tiền
                        walletRepo.createVNPayTopUp(amount, new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Object>>() {
                            @Override
                            public void onSuccess(java.util.Map<String, Object> data) {
                                runOnUiThread(() -> {
                                    if (data != null && data.containsKey("paymentUrl")) {
                                        String paymentUrl = (String) data.get("paymentUrl");
                                        
                                        // Lưu thông tin trước khi nạp để check tự động khi onResume
                                        getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                                             .putString("PREVIOUS_BALANCE", previousBalance.toString())
                                             .putBoolean("IS_CHECKING_TOP_UP", true)
                                             .apply();

                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(paymentUrl));
                                        startActivity(browserIntent);
                                    } else {
                                        Toast.makeText(ProfileActivity.this, "Không lấy được link thanh toán VNPay", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onError(String err) {
                                runOnUiThread(() -> {
                                    Toast.makeText(ProfileActivity.this, "Lỗi: " + err, Toast.LENGTH_SHORT).show();
                                });
                            }
                        });
                    }
                });
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void checkTransactionResult(java.math.BigDecimal prevBalance) {
        if (statusDialog != null && statusDialog.isShowing()) {
            return;
        }

        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_transaction_status, null);
        statusDialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (statusDialog.getWindow() != null) {
            statusDialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        android.widget.ImageView ivStatusIcon = dialogView.findViewById(R.id.iv_status_icon);
        android.widget.ProgressBar pbStatusLoading = dialogView.findViewById(R.id.pb_status_loading);
        android.widget.TextView tvStatusTitle = dialogView.findViewById(R.id.tv_status_title);
        android.widget.TextView tvStatusMessage = dialogView.findViewById(R.id.tv_status_message);
        android.widget.TextView btnStatusClose = dialogView.findViewById(R.id.btn_status_close);
        android.widget.TextView btnStatusAction = dialogView.findViewById(R.id.btn_status_action);

        // Show loading state initially
        pbStatusLoading.setVisibility(android.view.View.VISIBLE);
        ivStatusIcon.setVisibility(android.view.View.GONE);
        tvStatusTitle.setText("Đang kiểm tra kết quả");
        tvStatusMessage.setText("Hệ thống đang kiểm tra trạng thái giao dịch nạp tiền, vui lòng chờ trong giây lát...");
        btnStatusClose.setVisibility(android.view.View.GONE);
        btnStatusAction.setVisibility(android.view.View.GONE);

        statusDialog.show();

        // Perform balance API call
        com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
        walletRepo.getBalance(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse>() {
            @Override
            public void onSuccess(com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse balanceData) {
                runOnUiThread(() -> {
                    if (balanceData != null && balanceData.getBalance() != null) {
                        java.math.BigDecimal newBalance = balanceData.getBalance();
                        if (newBalance.compareTo(prevBalance) > 0) {
                            // Success!
                            pbStatusLoading.setVisibility(android.view.View.GONE);
                            ivStatusIcon.setImageResource(R.drawable.ic_circle_check);
                            ivStatusIcon.setColorFilter(android.graphics.Color.parseColor("#4CAF50"));
                            ivStatusIcon.setVisibility(android.view.View.VISIBLE);
                            
                            tvStatusTitle.setText("Giao dịch thành công!");
                            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                            tvStatusMessage.setText("Nạp tiền vào ví thành công.\nSố dư mới: " + format.format(newBalance) + "đ");
                            
                            btnStatusAction.setText("Đồng ý");
                            btnStatusAction.setVisibility(android.view.View.VISIBLE);
                            btnStatusAction.setOnClickListener(v -> {
                                getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                                     .putBoolean("IS_CHECKING_TOP_UP", false)
                                     .apply();
                                statusDialog.dismiss();
                                recreate();
                            });
                        } else {
                            // Failure (balance hasn't increased yet)
                            pbStatusLoading.setVisibility(android.view.View.GONE);
                            ivStatusIcon.setImageResource(R.drawable.ic_bold_close);
                            ivStatusIcon.setColorFilter(android.graphics.Color.parseColor("#F44336"));
                            ivStatusIcon.setVisibility(android.view.View.VISIBLE);
                            
                            tvStatusTitle.setText("Giao dịch thất bại");
                            tvStatusMessage.setText("Không tìm thấy giao dịch thành công. Vui lòng thanh toán và thử lại.");
                            
                            btnStatusAction.setText("Kiểm tra lại");
                            btnStatusAction.setVisibility(android.view.View.VISIBLE);
                            btnStatusAction.setOnClickListener(v -> {
                                statusDialog.dismiss();
                                checkTransactionResult(prevBalance);
                            });

                            btnStatusClose.setText("Hủy bỏ");
                            btnStatusClose.setVisibility(android.view.View.VISIBLE);
                            btnStatusClose.setOnClickListener(v -> {
                                getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                                     .putBoolean("IS_CHECKING_TOP_UP", false)
                                     .apply();
                                statusDialog.dismiss();
                            });
                        }
                    } else {
                        showError("Không lấy được thông tin số dư mới.");
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showError("Lỗi kết nối: " + errorMessage);
                });
            }

            private void showError(String errorMsg) {
                pbStatusLoading.setVisibility(android.view.View.GONE);
                ivStatusIcon.setImageResource(R.drawable.ic_bold_close);
                ivStatusIcon.setColorFilter(android.graphics.Color.parseColor("#F44336"));
                ivStatusIcon.setVisibility(android.view.View.VISIBLE);
                
                tvStatusTitle.setText("Lỗi kiểm tra");
                tvStatusMessage.setText(errorMsg);
                
                btnStatusAction.setText("Thử lại");
                btnStatusAction.setVisibility(android.view.View.VISIBLE);
                btnStatusAction.setOnClickListener(v -> {
                    statusDialog.dismiss();
                    checkTransactionResult(prevBalance);
                });

                btnStatusClose.setText("Hủy");
                btnStatusClose.setVisibility(android.view.View.VISIBLE);
                btnStatusClose.setOnClickListener(v -> {
                    getSharedPreferences("PaymentPrefs", MODE_PRIVATE).edit()
                         .putBoolean("IS_CHECKING_TOP_UP", false)
                         .apply();
                    statusDialog.dismiss();
                });
            }
        });
    }

    private void showCheckStatusDialog(String appTransId) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Đang chờ thanh toán");
        builder.setMessage("Sau khi thanh toán xong trên ZaloPay, hãy bấm nút dưới đây để kiểm tra trạng thái nạp tiền nhé!");
        builder.setCancelable(false);

        builder.setPositiveButton("Kiểm tra trạng thái", (dialog, which) -> {
            com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
            walletRepo.queryZaloPayOrderStatus(appTransId, new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Object>>() {
                @Override
                public void onSuccess(java.util.Map<String, Object> data) {
                    runOnUiThread(() -> {
                        // return_code = 1 là thanh toán thành công
                        if (data != null && data.containsKey("return_code")) {
                            int returnCode = ((Number) data.get("return_code")).intValue();
                            if (returnCode == 1) {
                                Toast.makeText(ProfileActivity.this, "Thanh toán ZaloPay thành công!", Toast.LENGTH_SHORT).show();
                                SetDataMainMenu(mainMenu); // Refresh ví
                            } else {
                                Toast.makeText(ProfileActivity.this, "Chưa thanh toán hoặc thất bại! (" + data.get("return_message") + ")", Toast.LENGTH_SHORT).show();
                                // Hiện lại dialog để người dùng có thể kiểm tra tiếp
                                showCheckStatusDialog(appTransId);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Chưa thanh toán hoặc lỗi mạng", Toast.LENGTH_SHORT).show();
                            showCheckStatusDialog(appTransId);
                        }
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        Toast.makeText(ProfileActivity.this, "Lỗi kiểm tra trạng thái: " + errorMessage, Toast.LENGTH_SHORT).show();
                        showCheckStatusDialog(appTransId);
                    });
                }
            });
        });
        
        builder.setNegativeButton("Đóng", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showStoreSelectionDialog() {
        com.example.uitpayapp.modules.restaurant.RestaurantRepository restaurantRepository = new com.example.uitpayapp.modules.restaurant.RestaurantRepository();
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Đang tải danh sách cửa hàng...");
        progressDialog.show();

        // Lấy ID của user hiện tại để lọc chỉ quán của họ
        Long currentUserId = com.example.uitpayapp.network.SessionManager.getInstance(ProfileActivity.this).getUserId();

        restaurantRepository.getAllRestaurants(new com.example.uitpayapp.network.ApiCallback<java.util.List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO>>() {
            @Override
            public void onSuccess(java.util.List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO> data) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();

                    // Chỉ lấy các quán thuộc user hiện tại
                    java.util.List<com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO> myRestaurants = new java.util.ArrayList<>();
                    if (data != null) {
                        for (com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO r : data) {
                            if (r.getMerchantId() != null && r.getMerchantId().equals(currentUserId)) {
                                myRestaurants.add(r);
                            }
                        }
                    }

                    if (myRestaurants.isEmpty()) {
                        Toast.makeText(ProfileActivity.this, "Bạn chưa quản lý cửa hàng nào", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    
                    String[] storeNames = new String[myRestaurants.size()];
                    for (int i = 0; i < myRestaurants.size(); i++) {
                        storeNames[i] = myRestaurants.get(i).getName() != null ? myRestaurants.get(i).getName() : "Cửa hàng chưa có tên";
                    }

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ProfileActivity.this);
                    builder.setTitle("Chọn cửa hàng");
                    builder.setItems(storeNames, (dialog, which) -> {
                        com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO selectedStore = myRestaurants.get(which);
                        Intent intentStore = new Intent(ProfileActivity.this, SellerHomeActivity.class);
                        if (selectedStore.getId() != null) {
                            intentStore.putExtra("store_id", selectedStore.getId());
                        }
                        intentStore.putExtra("store_name", selectedStore.getName());
                        
                        // Cập nhật SharedPreferences để các màn hình khác trong Seller có thể dùng
                        SharedPreferences.Editor editor = getSharedPreferences("SellerPrefs", MODE_PRIVATE).edit();
                        if (selectedStore.getId() != null) {
                            editor.putLong("current_store_id", selectedStore.getId());
                        }
                        editor.putString("current_store_name", selectedStore.getName());
                        editor.putString("current_store_address", selectedStore.getAddress());
                        if (selectedStore.getImageUrl() != null) {
                            editor.putString("current_store_image_url", selectedStore.getImageUrl());
                        } else {
                            editor.remove("current_store_image_url");
                        }
                        editor.apply();

                        startActivity(intentStore);
                    });
                    builder.setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());
                    builder.show();
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this, "Lỗi API: " + errorMessage + ". Đang dùng dữ liệu cửa hàng giả lập (ID=2)...", Toast.LENGTH_LONG).show();
                    
                    // Giả lập dữ liệu cửa hàng ID = 2
                    Intent intentStore = new Intent(ProfileActivity.this, SellerHomeActivity.class);
                    intentStore.putExtra("store_id", 2L);
                    intentStore.putExtra("store_name", "Cửa hàng Test (ID: 2)");
                    
                    SharedPreferences.Editor editor = getSharedPreferences("SellerPrefs", MODE_PRIVATE).edit();
                    editor.putLong("current_store_id", 2L);
                    editor.putString("current_store_name", "Cửa hàng Test (ID: 2)");
                    editor.putString("current_store_address", "Địa chỉ giả lập (Test)");
                    editor.apply();

                    startActivity(intentStore);
                });
            }
        });
    }
}
