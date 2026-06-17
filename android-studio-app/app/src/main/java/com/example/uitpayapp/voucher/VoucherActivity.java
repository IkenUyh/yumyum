package com.example.uitpayapp.voucher;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VoucherActivity extends AppCompatActivity {
    private List<VoucherModel> allVouchers = new ArrayList<>();
    private List<VoucherModel> filteredVouchers = new ArrayList<>();
    private VoucherAdapter adapter;
    EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_my_voucher);

        initViews();
        setupData();
        setupTabs();
    }

    private void initViews() {
        View topBar = findViewById(R.id.top_bar_voucher);
        View mainContainer = findViewById(R.id.voucher_screen_container);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top,systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });

        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Ví Voucher");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());


        etSearch = findViewById(R.id.et_search_gift);
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                TabLayout tabLayout = findViewById(R.id.tab_vouchers);
                filterVouchers(tabLayout.getSelectedTabPosition(), etSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void setupData() {
        allVouchers.add(new VoucherModel(VoucherModel.VoucherType.FOOD_DISCOUNT, "Giảm 40.000đ đơn từ 0đ", "Quán chọn lọc", "HSD: 25/05/2026"));
        allVouchers.add(new VoucherModel(VoucherModel.VoucherType.FOOD_DISCOUNT, "Giảm 20.000đ đơn từ 20.000đ", "Quán đối tác", "HSD: 31/05/2026"));
        allVouchers.add(new VoucherModel(VoucherModel.VoucherType.SHIPPING_FEE, "Freeship tối đa 15.000đ", "Đơn từ 0đ", "HSD: 25/05/2026"));
        allVouchers.add(new VoucherModel(VoucherModel.VoucherType.SHIPPING_FEE, "Giảm 20.000đ phí vận chuyển", "Đơn từ 100.000đ", "HSD: 31/05/2026"));
        allVouchers.add(new VoucherModel(VoucherModel.VoucherType.FOOD_DISCOUNT, "Mã giảm 50% tối đa 30.000đ", "Đơn từ 0đ", "HSD: 15/06/2026"));
        filteredVouchers.addAll(allVouchers);
        RecyclerView rvVoucher = findViewById(R.id.rv_voucher);
        adapter = new VoucherAdapter(filteredVouchers, voucher -> HanleVoucherClick(voucher));
        rvVoucher.setAdapter(adapter);
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tab_vouchers);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterVouchers(tab.getPosition(),etSearch.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void filterVouchers(int position, String keyword) {
        filteredVouchers.clear();
        keyword=(keyword!=null&&!keyword.isEmpty())?keyword.toLowerCase().trim():"";
        for (VoucherModel voucher : allVouchers) {
            boolean tabCondition = false, keywordCondition = false;
            if (position==0)
                tabCondition=true;
            else if (position==1&&voucher.getVoucherType().name().equals("FOOD_DISCOUNT"))
                tabCondition=true;
            else if (position==2&&voucher.getVoucherType().name().equals("SHIPPING_FEE"))
                tabCondition=true;
            if (keyword.isEmpty()||voucher.getMainTitle().toLowerCase().contains(keyword))
                keywordCondition=true;
            if (tabCondition&&keywordCondition) {
                filteredVouchers.add(voucher);
            }
        }
        adapter.notifyDataSetChanged();
    }
    private void HanleVoucherClick(VoucherModel voucher) {
    }
}
