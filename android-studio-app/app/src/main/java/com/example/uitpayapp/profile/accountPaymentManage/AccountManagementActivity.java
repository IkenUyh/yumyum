package com.example.uitpayapp.profile.accountPaymentManage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.giftexchange.GiftExchangeActivity;
import com.example.uitpayapp.voucher.VoucherActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class AccountManagementActivity extends AppCompatActivity {
    RecyclerView rvAccountCards;
    List<AccountCardModel> cardList;
    TextView tabSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_management);

        initView();
        setRvAccountCards();
        setAnimation();
        rvAccountCards.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> HanleScrollCardChange());
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_account_manage);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        View mainContainer = findViewById(R.id.account_manage_container);
        rvAccountCards = findViewById(R.id.rv_account_cards);
        tabSelected=findViewById(R.id.account_manage_type_bank);
        tvTitle.setText("Chọn vị trí giao hàng");
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
        findViewById(R.id.tv_account_manage_collect_coin).setOnClickListener(v -> {
            Intent intent = new Intent(this, GiftExchangeActivity.class);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.tv_account_manage_voucher_hot).setOnClickListener(v -> {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivity(intent);
            finish();
        });
        findViewById(R.id.btn_account_manage_openpacket).setOnClickListener(v -> showAddAccountBottomSheet());

    }
    private void setRvAccountCards() {
        cardList = new ArrayList<>();
        cardList.add(new AccountCardModel("Vietcombank", "19001010", "Đã mở", AccountCardModel.AccountType.NGAN_HANG));
        cardList.add(new AccountCardModel("Sacombank", "18001010", "Đã mở", AccountCardModel.AccountType.NGAN_HANG));
        cardList.add(new AccountCardModel("Ví UITpay", "","Đã mở", AccountCardModel.AccountType.SO_DU));
        AccountCardAdapter adapter = new AccountCardAdapter(cardList);
        rvAccountCards.setAdapter(adapter);
    }
    private void HanleScrollCardChange() {
        GridLayoutManager layoutManager = (GridLayoutManager) rvAccountCards.getLayoutManager();
        int position=layoutManager.findFirstCompletelyVisibleItemPosition();
        if (position==-1)
        {
            position=layoutManager.findFirstVisibleItemPosition();
        }
        AccountCardModel.AccountType type=cardList.get(position).getType();
        switch(type)
        {
            case NGAN_HANG:
                tabSelected.setBackgroundResource(R.drawable.bg_tab_unselected);
                tabSelected.setTextColor(Color.parseColor("#ffffff"));
                tabSelected=findViewById(R.id.account_manage_type_bank);
                tabSelected.setTextColor(Color.parseColor("#f24405"));
                tabSelected.setBackgroundResource(R.drawable.bg_tab_selected);
                break;
            case SO_DU:
                tabSelected.setBackgroundResource(R.drawable.bg_tab_unselected);
                tabSelected.setTextColor(Color.parseColor("#ffffff"));
                tabSelected=findViewById(R.id.account_manage_type_wallet);
                tabSelected.setTextColor(Color.parseColor("#f24405"));
                tabSelected.setBackgroundResource(R.drawable.bg_tab_selected);
                break;
        }

    }
    private void setAnimation() {
        ImageView ivGift = findViewById(R.id.iv_account_manage_gift);
        Animation utils = AnimationUtils.loadAnimation(this, R.anim.anim_shake_gift);
        ivGift.startAnimation(utils);
    }
    private void showAddAccountBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_add_account_payment, null);
        //mot so ngan hang
        AutoCompleteTextView dropdown = view.findViewById(R.id.dropdown);
        String[] providers = {"Vietcombank", "Sacombank", "Agribank", "BIDV", "Viettinbank"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, providers);
        dropdown.setAdapter(adapter);
        view.findViewById(R.id.btn_close_add_bill).setOnClickListener(v -> bottomSheetDialog.dismiss());

        view.findViewById(R.id.btn_confirm_add_bill).setOnClickListener(v -> {
            String code = ((android.widget.EditText) view.findViewById(R.id.et_customer_code)).getText().toString();
            String provider = dropdown.getText().toString();

            if (!code.isEmpty() && !provider.isEmpty()) {
                android.widget.Toast.makeText(this, "Đã gửi yêu cầu thêm: " + provider, android.widget.Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            } else {
                android.widget.Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}
