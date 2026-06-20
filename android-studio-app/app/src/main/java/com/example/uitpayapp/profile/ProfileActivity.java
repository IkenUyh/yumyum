package com.example.uitpayapp.profile;

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
import com.example.uitpayapp.profile.accountPaymentManage.AccountManagementActivity;
import com.example.uitpayapp.voucher.VoucherActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.example.uitpayapp.network.SessionManager;
import com.example.uitpayapp.modules.user.UserRepository;
import com.example.uitpayapp.modules.user.models.responses.UserResponseDTO;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    RecyclerView mainMenu;
    View rlLoginProfile,llUserInfo;
    boolean isLogin = false;

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
        });
        findViewById(R.id.btn_login_profile).setOnClickListener(v->
        {
            Intent intentLogin=new Intent(this, SignInActivity.class);
            startActivity(intentLogin);
        });
    }

    void SetDataMainMenu(RecyclerView mainMenu) {
        if (mainMenu == null) return;
        mainMenu.setLayoutManager(new LinearLayoutManager(this));
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        //Nhóm 1: Ưu đãi
        List<MenuItemData> ListItems_uudai = new ArrayList<>();
        ListItems_uudai.add(new MenuItemData("YumYum Priority","Thành viên",R.drawable.ic_priority_yumyum,false));
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
        ListItems_tienich.add(new MenuItemData("Quản lý duyệt", "5 chờ duyệt", R.drawable.list_alt_24px,false));
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
            Intent intent=new Intent(this, AccountManagementActivity.class);
            startActivity(intent);
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
                showStoreSelectionDialog();
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
    }

    private void updateNotificationBadge() {
        final TextView tvNotificationBadge = findViewById(R.id.tv_notification_badge);
        if (tvNotificationBadge == null) return;
        
        com.example.uitpayapp.modules.notification.NotificationRepository repo = 
                new com.example.uitpayapp.modules.notification.NotificationRepository();
        repo.getUnreadCount(new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Long>>() {
            @Override
            public void onSuccess(java.util.Map<String, Long> countData) {
                long unreadCount = countData != null && countData.containsKey("unreadCount") ? countData.get("unreadCount") : 0;
                if (unreadCount > 0) {
                    tvNotificationBadge.setText(String.valueOf(unreadCount));
                    tvNotificationBadge.setVisibility(View.VISIBLE);
                } else {
                    tvNotificationBadge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                // Fail silently
            }
        });
    }

    public void showTopUpDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Nạp tiền vào ví qua ZaloPay");

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Nhập số tiền cần nạp (tối thiểu 10,000 VNĐ)");
        builder.setView(input);

        builder.setPositiveButton("Nạp ZaloPay", (dialog, which) -> {
            String amountStr = input.getText().toString();
            if (!amountStr.isEmpty()) {
                long amount = Long.parseLong(amountStr);
                if (amount < 10000) {
                    Toast.makeText(this, "Số tiền tối thiểu là 10,000 VNĐ", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
                walletRepo.createZaloPayTopUp(amount, new com.example.uitpayapp.network.ApiCallback<java.util.Map<String, Object>>() {
                    @Override
                    public void onSuccess(java.util.Map<String, Object> data) {
                        runOnUiThread(() -> {
                            if (data != null && data.containsKey("order_url")) {
                                String orderUrl = (String) data.get("order_url");
                                String appTransId = (String) data.get("app_trans_id");
                                
                                // Mở trang thanh toán ZaloPay
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(orderUrl));
                                startActivity(browserIntent);
                                
                                // Hiện dialog để kiểm tra trạng thái
                                showCheckStatusDialog(appTransId);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Không lấy được link thanh toán", Toast.LENGTH_SHORT).show();
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
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.cancel());
        builder.show();
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
