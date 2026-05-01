package com.example.uitpayapp.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.example.uitpayapp.R;

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
        rvAccountCards.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> HanleScrollCardChange());
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_account_manage);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        View mainContainer = findViewById(R.id.account_manage_container);
        rvAccountCards = findViewById(R.id.rv_account_cards);
        tabSelected=findViewById(R.id.account_manage_type_bank);
        tvTitle.setText("Quản lý thẻ/tài khoản");
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

    }
    private void setRvAccountCards() {
        cardList = new ArrayList<>();
        cardList.add(new AccountCardModel("Gửi tiết kiệm (CIMB)", "Chưa mở", "Mở TK có quà", AccountCardModel.AccountType.TAI_CHINH));
        cardList.add(new AccountCardModel("Gửi tiết kiệm (Cake)", "Chưa mở", "Mở TK có quà", AccountCardModel.AccountType.TAI_CHINH));
        cardList.add(new AccountCardModel("Số dư sinh lời", "Đã mở","", AccountCardModel.AccountType.SO_DU));
        cardList.add(new AccountCardModel("Ví Zalopay", "Đã mở","", AccountCardModel.AccountType.SO_DU));
        AccountCardAdapter adapter = new AccountCardAdapter(cardList);
        rvAccountCards.setAdapter(adapter);
        new PagerSnapHelper().attachToRecyclerView(rvAccountCards);
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
            case TAI_CHINH:
                tabSelected.setBackgroundResource(R.drawable.bg_autopay_tab_unselected);
                tabSelected=findViewById(R.id.account_manage_type_finance);
                tabSelected.setBackgroundResource(R.drawable.bg_autopay_tab_selected);
                break;
            case SO_DU:
                tabSelected.setBackgroundResource(R.drawable.bg_autopay_tab_unselected);
                tabSelected=findViewById(R.id.account_manage_type_coin);
                tabSelected.setBackgroundResource(R.drawable.bg_autopay_tab_selected);
                break;
        }

    }
}
