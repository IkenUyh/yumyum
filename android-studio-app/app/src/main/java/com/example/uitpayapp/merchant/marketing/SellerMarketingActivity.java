package com.example.uitpayapp.merchant.marketing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_adapters.FoodCategoryAdapter;
import com.example.uitpayapp.home.home_models.FoodCategory;
import com.example.uitpayapp.merchant.home.SellerHomeActivity;
import com.example.uitpayapp.merchant.notification.SellerNotificationActivity;
import com.example.uitpayapp.merchant.shop.SellerShopActivity;
import com.example.uitpayapp.profile.GroupItemData;
import com.example.uitpayapp.profile.InfoApplication;
import com.example.uitpayapp.profile.MenuItemData;
import com.example.uitpayapp.profile.NotificationSettings;
import com.example.uitpayapp.profile.ProfileActivity;
import com.example.uitpayapp.profile.ProfileMenuAdapter;
import com.example.uitpayapp.profile.ProfileWebView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.ArrayList;
import java.util.List;

public class SellerMarketingActivity extends AppCompatActivity {

    private RecyclerView rvServices;
    private RecyclerView rvSettingsMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_marketing);

        initViews();
        setupServicesGrid();
        setupSettingsMenu();
        setupBottomMenu();
    }

    private void initViews() {
        rvServices = findViewById(R.id.rv_seller_services);
        rvSettingsMenu = findViewById(R.id.rv_settings_menu);
        View mainContainer = findViewById(R.id.seller_marketing_container);
        if (mainContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }

    private void setupServicesGrid() {
        List<FoodCategory> serviceList = new ArrayList<>();
        int iconColor = Color.parseColor("#F24405");
        
        serviceList.add(new FoodCategory("Thông tin cửa hàng", R.drawable.ic_my_store, Color.parseColor("#E65100")));
        serviceList.add(new FoodCategory("Cài đặt thông báo", R.drawable.ic_notification, iconColor));
        serviceList.add(new FoodCategory("Cài đặt máy in", R.drawable.ic_print, iconColor));
        serviceList.add(new FoodCategory("Cập nhật mã PIN", R.drawable.ic_security_user, iconColor));
        serviceList.add(new FoodCategory("Lịch sử chỉnh sửa", R.drawable.ic_history_24px, iconColor));
        serviceList.add(new FoodCategory("Trung tâm Trợ giúp", R.drawable.ic_contact_support_ver2, iconColor));

        FoodCategoryAdapter adapter = new FoodCategoryAdapter(serviceList, this::handleServiceClick);

        rvServices.setLayoutManager(new GridLayoutManager(this, 4));
        rvServices.setAdapter(adapter);
    }

    private void setupSettingsMenu() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedName = sharedPreferences.getString("FULL_NAME", "Người dùng ZaloPay");

        List<GroupItemData> groupList = new ArrayList<>();

        List<MenuItemData> accountItems = new ArrayList<>();
        accountItems.add(new MenuItemData("Tài khoản của tôi", "Quản lý: " + savedName, -1, false));
        groupList.add(new GroupItemData("", accountItems));

        List<MenuItemData> otherItems = new ArrayList<>();
        otherItems.add(new MenuItemData("Thanh toán", "", -1, false));
        otherItems.add(new MenuItemData("Thống kê", "", -1, false));
        otherItems.add(new MenuItemData("Ngôn ngữ", "", -1, false));
        otherItems.add(new MenuItemData("Chính sách", "", -1, false));
        otherItems.add(new MenuItemData("Thông tin ứng dụng", "", -1, false));
        groupList.add(new GroupItemData("", otherItems));

        ProfileMenuAdapter adapter = new ProfileMenuAdapter(this, groupList, this::HanleItemClick);

        rvSettingsMenu.setLayoutManager(new LinearLayoutManager(this));
        rvSettingsMenu.setAdapter(adapter);
    }

    public void handleServiceClick(FoodCategory item) {
        if (item == null) return;
        switch (item.getName()) {
            case "Trung tâm Trợ giúp":
                Intent intentSupport = new Intent(this, ProfileWebView.class);
                intentSupport.putExtra("URL_KEY", "https://help.cs.shopeefood.vn/portal/103");
                startActivity(intentSupport);
                break;
            case "Thông tin cửa hàng":
                Intent intentShopInfo = new Intent(this, SellerShopInfoActivity.class);
                startActivity(intentShopInfo);
                break;
            case "Cài đặt thông báo":
                Intent intentNotification = new Intent(this, NotificationSettings.class);
                startActivity(intentNotification);
                break;
            default:
                Toast.makeText(this, "Tính năng " + item.getName() + " đang phát triển", Toast.LENGTH_SHORT).show();
        }
    }

    public void HanleItemClick(MenuItemData item) {
        if (item == null) return;
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems = new ArrayList<>();

        switch (item.getTitle()) {
            case "Tài khoản của tôi":
                Toast.makeText(this, "Quản lý bởi: " + item.getSubtitle(), Toast.LENGTH_SHORT).show();
                Intent intentAccount = new Intent(this, ProfileActivity.class);
                intentAccount.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intentAccount);
                break;
            case "Chính sách":
                Intent intentSecurity = new Intent(this, ProfileWebView.class);
                intentSecurity.putExtra("URL_KEY", "https://merchant.shopeefood.vn/edu/course/chinh-sach-va-quy-dinh-danh-cho-doi-tac-quan-shopeefood");
                startActivity(intentSecurity);
                break;
            case "Ngôn ngữ":
                ListItems.add(new MenuItemData("Tiếng Việt", "", -1, false));
                ListItems.add(new MenuItemData("English", "", -1, false));
                ListGroupItem.add(new GroupItemData("Chọn ngôn ngữ", ListItems));
                ShowBottomSheet("Ngôn ngữ", ListGroupItem);
                break;
            case "Thống kê":
                Intent intentStats = new Intent(this, SellerStatisticsActivity.class);
                startActivity(intentStats);
                break;
            case "Thanh toán":
                Intent intentWallet = new Intent(this, SellerWalletActivity.class);
                startActivity(intentWallet);
                break;
            case "Thông tin ứng dụng":
                Intent intentAppInfo = new Intent(this, InfoApplication.class);
                startActivity(intentAppInfo);
                break;
            default:
                Toast.makeText(this, "Bạn đã chọn: " + item.getTitle(), Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void ShowBottomSheet(String SheetTilte, List<GroupItemData> ListGroupItem) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View SheetView = getLayoutInflater().inflate(R.layout.layout_dynamic_bottom_sheet, null);
        ((ImageView) SheetView.findViewById(R.id.btn_close)).setOnClickListener(v -> bottomSheetDialog.dismiss());
        ((TextView) SheetView.findViewById(R.id.sheet_title)).setText(SheetTilte);
        LinearLayout container = SheetView.findViewById(R.id.sheet_container);
        
        RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        recyclerView.setBackgroundColor(Color.parseColor("#f1f5ff"));
        recyclerView.setPadding(16, 16, 16, 16);
        container.addView(recyclerView);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ProfileMenuAdapter(this, ListGroupItem, item -> {
            Toast.makeText(this, "Đã chọn: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            bottomSheetDialog.dismiss();
        }));
        
        bottomSheetDialog.setContentView(SheetView);
        bottomSheetDialog.show();
    }

    private void setupBottomMenu() {
        View navOrders = findViewById(R.id.navOrders);
        View navNotification = findViewById(R.id.navNotification);
        View navShop = findViewById(R.id.navShop);
        ImageView ivMarketing = findViewById(R.id.iv_nav_marketing);
        TextView tvMarketing = findViewById(R.id.tv_nav_marketing);
        if (ivMarketing != null) ivMarketing.setColorFilter(Color.parseColor("#F24405"));
        if (tvMarketing != null) tvMarketing.setTextColor(Color.parseColor("#F24405"));

        if (navOrders != null) {
            navOrders.setOnClickListener(v -> {
                Intent intent = new Intent(this, SellerHomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }

        if (navNotification != null) {
            navNotification.setOnClickListener(v -> {
                Intent intent = new Intent(this, SellerNotificationActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
        
        if (navShop != null) {
            navShop.setOnClickListener(v -> {
                Intent intent = new Intent(this, SellerShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            });
        }
    }
}
