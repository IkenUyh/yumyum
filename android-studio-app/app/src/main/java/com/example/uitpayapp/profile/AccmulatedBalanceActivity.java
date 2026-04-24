package com.example.uitpayapp.profile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.ArrayList;
import java.util.List;

public class AccmulatedBalanceActivity extends AppCompatActivity {
    LinearLayout detail_accmulated_balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accmulated_balance);
        View topBar = findViewById(R.id.top_bar_accmulated_balance);
        View mainContainer = findViewById(R.id.accmulated_balance_screen_main_data);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom+10;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        detail_accmulated_balance=findViewById(R.id.detail_accmulated_balance);
        findViewById(R.id.btn_close_accmulated).setOnClickListener(v -> this.finish());
        SetAccmulatedChoice();
        findViewById(R.id.show_detail_accmulated_balance).setOnClickListener(v->ShowDetailAccmulated());
    }
    private void SetAccmulatedChoice()
    {
        List<GroupItemData> ListGroupItem = new ArrayList<>();
        List<MenuItemData> ListItems1 = new ArrayList<>();
        ListItems1.add(new MenuItemData("Nhận tiền chuyển tới từ bạn bè, QR cá nhân vào Số dư sinh lời","",R.drawable.ic_accmulated_balance,false));
        ListItems1.add(new MenuItemData("Ưu tiên thanh toán bằng nguồn tiền số dư sinh lời","",R.drawable.ic_sort_payment,false));
        ListGroupItem.add(new GroupItemData("Ưu tiên sử dụng số dư sinh lời",ListItems1));
        List<MenuItemData> ListItems2 = new ArrayList<>();
        ListItems2.add(new MenuItemData("Quyền lợi tài khoản","",R.drawable.ic_security_user,false));
        ListItems2.add(new MenuItemData("Mức sinh lời","",R.drawable.ic_profile_caculator,false));
        ListItems2.add(new MenuItemData("Nạp tiền tự động","",R.drawable.ic_payment,false));
        ListItems2.add(new MenuItemData("Hợp đồng đầu tư","",R.drawable.ic_contract_insurance,false));
        ListGroupItem.add(new GroupItemData("Quản lý tài khoản",ListItems2));
        RecyclerView accmulated_choice_container=findViewById(R.id.accmulated_choice_container);
        accmulated_choice_container.setAdapter(new ProfileMenuAdapter(this,ListGroupItem,ItemClick->HanleItemClick(ItemClick)));
    }
    public void HanleItemClick(MenuItemData item) {

    }
    private void ShowDetailAccmulated()
    {
        if (detail_accmulated_balance.getVisibility()==View.GONE) {
            detail_accmulated_balance.setVisibility(View.VISIBLE);
            return;
        }
        detail_accmulated_balance.setVisibility(View.GONE);
    }
}