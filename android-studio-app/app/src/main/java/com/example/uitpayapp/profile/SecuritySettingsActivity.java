package com.example.uitpayapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.List;

public class SecuritySettingsActivity extends AppCompatActivity {
    RecyclerView rv_security_settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_settings);
        View topBar = findViewById(R.id.top_bar_security);
        View mainContainer = findViewById(R.id.security_setting_main_data);
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
        rv_security_settings = findViewById(R.id.rv_security_settings);
        findViewById(R.id.auto_block_app_setting).setOnClickListener(v -> HanleAutoBlockApp());
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView)topBar.findViewById(R.id.top_bar_title)).setText("Bảo mật tài khoản");
        SetDataSecuritySettings();
    }
    private void SetDataSecuritySettings() {
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems1 = new ArrayList<>();
        ListItems1.add(new MenuItemData("Đổi mật khẩu", "",-1, false));
        ListItems1.add(new MenuItemData("Quên mật khẩu", "",-1, false));
        ListItems1.add(new MenuItemData("Thay đổi số điện thoại","",-1,false));
        ListGroupItem.add(new GroupItemData("Mật khẩu và số điện thoại", ListItems1));
        List<MenuItemData> ListItems2 = new ArrayList<>();
        ListItems2.add(new MenuItemData("Điều khoản sử dụng", "",-1, false));
        ListItems2.add(new MenuItemData("Điều khoản quyền riêng tư", "",-1, false));
        ListItems2.add(new MenuItemData("Đóng tài khoản", "",-1, false));
        ListGroupItem.add(new GroupItemData("Khác", ListItems2));
        rv_security_settings.setAdapter(new ProfileMenuAdapter(this, ListGroupItem,ItemClick->HanleItemClick(ItemClick)));
    }
    private void HanleItemClick(MenuItemData item) {
        Intent intent;
        switch (item.getTitle()) {
            case "Điều khoản sử dụng":
                intent=new Intent(this,ProfileWebView.class);
                intent.putExtra("URL-KEY","https://zalopay.vn/quy-dinh/thoa-thuan-su-dung-zalopay");
                startActivity(intent);
                break;
            case "Điều khoản quyền riêng tư":
                intent=new Intent(this,ProfileWebView.class);
                intent.putExtra("URL-KEY","https://zalopay.vn/quy-dinh/chinh-sach-bao-ve-quyen-rieng-tu");
                break;
        }
    }
    private void HanleAutoBlockApp() {

    }
}
