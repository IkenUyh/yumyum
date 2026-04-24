package com.example.uitpayapp.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.ServiceAdapter;
import com.example.uitpayapp.home.ServiceItem;

import java.util.ArrayList;
import java.util.List;

public class AutoPaymentActivity extends AppCompatActivity {
    RecyclerView rvAutopayService;
    View tabSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_payment);
        View topBar = findViewById(R.id.top_bar_auto_pay);
        View mainContainer = findViewById(R.id.autopay_container);
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
        findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView)findViewById(R.id.top_bar_title)).setText("Thanh toán tự động");
        rvAutopayService = findViewById(R.id.rv_autopay_service);
        //
        tabSelected = findViewById(R.id.tab_bill_autopay);
        HanleTabBillData(findViewById(R.id.tab_bill_autopay));
        //
        findViewById(R.id.tab_bill_autopay).setOnClickListener(v -> HanleTabBillData(v));
        findViewById(R.id.tab_internet_autopay).setOnClickListener(v -> HanleInternetData(v));
        findViewById(R.id.tab_insurance_autopay).setOnClickListener(v -> HanleInsuranceData(v));
        findViewById(R.id.tab_all_autopay).setOnClickListener(v -> HanleAllData(v));
    }

    private void HanleTabBillData(View view) {
        updateTabUI(view);
        List<ServiceItem> listReceiptService = new ArrayList<>();
        listReceiptService.add(new ServiceItem("Điện", R.drawable.ic_receiptscreen_electric, ""));
        listReceiptService.add(new ServiceItem("Nước", R.drawable.ic_receiptscreen_water, ""));
        listReceiptService.add(new ServiceItem("Internet", R.drawable.ic_internet, "")); // Kiểm tra lại ID icon internet
        listReceiptService.add(new ServiceItem("Truyền hình", R.drawable.ic_receiptscreen_tv, ""));
        listReceiptService.add(new ServiceItem("Trả khoản vay", R.drawable.ic_receiptscreen_loan, ""));
        listReceiptService.add(new ServiceItem("Giáo dục", R.drawable.ic_receiptscreen_education, ""));
        rvAutopayService.setAdapter(new ServiceAdapter(listReceiptService, R.layout.item_service));
    }

    private void HanleInternetData(View view) {
        updateTabUI(view);
        List<ServiceItem> listPhoneService = new ArrayList<>();
        listPhoneService.add(new ServiceItem("Nạp ĐT", R.drawable.ic_autopay_credit, ""));
        listPhoneService.add(new ServiceItem("ĐT trả sau", R.drawable.ic_autopay_paylater, ""));
        listPhoneService.add(new ServiceItem("Nạp 4G/5G", R.drawable.ic_sanqua_4g, ""));
        rvAutopayService.setAdapter(new ServiceAdapter(listPhoneService, R.layout.item_service));
    }

    private void HanleAllData(View view) {
        updateTabUI(view);
        // Load tất cả dịch vụ hoặc logic tương ứng
    }

    private void HanleInsuranceData(View view) {
        updateTabUI(view);
        // Load dịch vụ bảo hiểm
    }
    private void updateTabUI(View view) {
        tabSelected.setBackgroundResource(R.drawable.bg_autopay_tab_unselected);
        tabSelected = view;
        tabSelected.setBackgroundResource(R.drawable.bg_autopay_tab_selected);
        if (rvAutopayService.getAdapter()!=null)
            rvAutopayService.setAdapter(null);
    }
}
