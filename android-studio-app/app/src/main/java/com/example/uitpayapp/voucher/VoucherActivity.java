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
        RecyclerView rvVoucher = findViewById(R.id.rv_voucher);
        adapter = new VoucherAdapter(filteredVouchers, voucher -> HanleVoucherClick(voucher));
        rvVoucher.setAdapter(adapter);

        new VoucherRepository().getActiveVouchers(new com.example.uitpayapp.network.ApiCallback<List<VoucherResponseDTO>>() {
            @Override
            public void onSuccess(List<VoucherResponseDTO> data) {
                allVouchers.clear();
                for (VoucherResponseDTO dto : data) {
                    VoucherModel.VoucherType modelType = VoucherModel.VoucherType.FOOD_DISCOUNT;
                    if ("SHIPPING_DISCOUNT".equalsIgnoreCase(dto.getType())) {
                        modelType = VoucherModel.VoucherType.SHIPPING_FEE;
                    }

                    int discount = dto.getDiscountPercent() != null ? dto.getDiscountPercent() : 0;
                    int maxDisc = dto.getMaxDiscount() != null ? dto.getMaxDiscount().intValue() : 0;
                    int minVal = dto.getMinOrderValue() != null ? dto.getMinOrderValue().intValue() : 0;

                    String mainTitle = "Mã: " + dto.getCode();
                    String subTitle = "Giảm " + discount + "% tối đa " + maxDisc + "đ (Đơn từ " + minVal + "đ)";
                    String exp = formatDateTime(dto.getEndDate());

                    allVouchers.add(new VoucherModel(
                            modelType,
                            mainTitle,
                            subTitle,
                            exp
                    ));
                }
                runOnUiThread(() -> {
                    filteredVouchers.clear();
                    filteredVouchers.addAll(allVouchers);
                    // Lọc lại theo tab hiện tại
                    TabLayout tabLayout = findViewById(R.id.tab_vouchers);
                    if (tabLayout != null) {
                        filterVouchers(tabLayout.getSelectedTabPosition(), etSearch.getText().toString());
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(VoucherActivity.this, "Lỗi tải voucher: " + errorMessage, android.widget.Toast.LENGTH_SHORT).show();
                });
            }
        });
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

    private String formatDateTime(String isoString) {
        if (isoString == null) return "HSD: Không thời hạn";
        try {
            String cleanStr = isoString;
            if (cleanStr.contains(".")) {
                int dotIdx = cleanStr.indexOf(".");
                int tIdx = cleanStr.indexOf("+");
                if (tIdx == -1) tIdx = cleanStr.indexOf("-", dotIdx);
                if (tIdx == -1) tIdx = cleanStr.indexOf("Z", dotIdx);
                if (tIdx != -1) {
                    cleanStr = cleanStr.substring(0, dotIdx) + cleanStr.substring(tIdx);
                } else {
                    cleanStr = cleanStr.substring(0, dotIdx);
                }
            }
            java.text.SimpleDateFormat inputFormat;
            if (cleanStr.endsWith("Z")) {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            } else if (cleanStr.contains("+") || (cleanStr.lastIndexOf("-") > 10)) {
                try {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", java.util.Locale.getDefault());
                    java.util.Date date = inputFormat.parse(cleanStr);
                    java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                    return "HSD: " + outputFormat.format(date);
                } catch (Exception ex) {
                    inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
                }
            } else {
                inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            }
            java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(cleanStr);
            return "HSD: " + outputFormat.format(date);
        } catch (Exception e) {
            return "HSD: " + isoString;
        }
    }
}
