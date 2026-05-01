package com.example.uitpayapp.insurance;

import static android.graphics.Typeface.BOLD;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.home.ServiceAdapter;
import com.example.uitpayapp.home.ServiceItem;
import java.util.ArrayList;
import java.util.List;

public class InsuranceActivity extends AppCompatActivity {

    NestedScrollView mainContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insurance);

        initView();
        setupQuickActions();
        setupInsuranceCategories();
        setupRuleInsurance();
        findViewById(R.id.btn_insurance_info).setOnClickListener(v->
        {
            mainContainer.smoothScrollTo(0,findViewById(R.id.insurance_rule_container).getTop());
        });
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_insurance);
        mainContainer = findViewById(R.id.insurance_container);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        if (tvTitle != null) tvTitle.setText("Bảo hiểm");

        View btnBack = topBar.findViewById(R.id.top_bar_back_btn);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupQuickActions() {
        setupHeaderServiceItem(R.id.btn_insurance_manage, "Quản lý\nhợp đồng", R.drawable.ic_contract_insurance);
        setupHeaderServiceItem(R.id.btn_priority_pay, "Ưu đãi\nthanh toán", R.drawable.ic_gift_24px);
        setupHeaderServiceItem(R.id.btn_auto_payment, "Thanh toán\nbảo hiểm", R.drawable.ic_receipt);
        setupHeaderServiceItem(R.id.btn_insurance_info, "Tìm hiểu\nbảo hiểm", R.drawable.ic_security_transaction_insurance);
    }
    private void setupRuleInsurance() {
        LinearLayout container = findViewById(R.id.insurance_rule_container);
        if (container == null) return;
        addRowRuleInsurance(container, "Dễ dàng thanh toán và mua các loại bảo hiểm",true);
        addRowRuleInsurance(container, "Lựa chọn đa dạng với các nhà bảo hiểm uy tín",false);
        addRowRuleInsurance(container,"Thanh toán nhanh, thủ tục đơn giản",false);
        addRowRuleInsurance(container,"Đa dạng lựa chọn dịch vụ bảo hiểm",true);
        addRowRuleInsurance(container,"Thanh toán phí bảo hiểm nhân thọ",false);
        addRowRuleInsurance(container,"Bảo hiểm sức khỏe, bảo hiểm phương tiện và đa dạng các loại bảo hiểm khác",false);
        addRowRuleInsurance(container,"Dịch vụ bảo hiểm an tâm & bảo mật",true);
        addRowRuleInsurance(container,"Vì điện tử Zalopay vừa đạt chứng nhận ISO 27001, tiêu chuẩn quốc tế về bảo mật thông tin, dữ liệu của Hệ thống quản lý an toàn thông tin (ISMS)",false);
        addRowRuleInsurance(container,"Dịch vụ hỗ trợ khách hàng tận tâm và hoàn toàn miễn phí dịch vụ khi đóng phí bảo hiểm\nLưu ý: Ngoại trừ khi dùng nguồn tiền từ thẻ tín dụng",false);
    }

    private void setupHeaderServiceItem(int includeId, String name, int iconRes) {
        View layout = findViewById(includeId);
        if (layout == null) return;
        TextView tvName = layout.findViewById(R.id.tvServiceName);
        ImageView ivIcon = layout.findViewById(R.id.ivServiceIcon);
        if (tvName != null) {
            tvName.setText(name);
            tvName.setTextColor(Color.WHITE);
        }
        if (ivIcon != null) {
            ivIcon.setImageResource(iconRes);
            ivIcon.setBackgroundResource(R.drawable.bg_white_circle);
            ivIcon.setBackgroundTintList(ColorStateList.valueOf(0x40FFFFFF));
            ivIcon.setImageTintList(ColorStateList.valueOf(Color.WHITE));
        }
    }

    private void setupInsuranceCategories() {
        RecyclerView rvCategories = findViewById(R.id.rvInsuranceCategories);
        if (rvCategories == null) return;

        List<ServiceItem> items = new ArrayList<>();
        items.add(new ServiceItem("Xe máy", R.drawable.ic_bus_ticket, "40K/năm"));
        items.add(new ServiceItem("Ô tô", R.drawable.ic_transfer, "-40%"));
        items.add(new ServiceItem("Điện gia dụng", R.drawable.ic_receiptscreen_electric, ""));
        items.add(new ServiceItem("Nhà", R.drawable.ic_home_24px, ""));
        items.add(new ServiceItem("Sức khỏe", R.drawable.ic_receiptscreen_heath, ""));
        items.add(new ServiceItem("Tai nạn", R.drawable.ic_security_user, ""));
        items.add(new ServiceItem("Điện tử", R.drawable.ic_autopay_credit, ""));

        ServiceAdapter adapter = new ServiceAdapter(items, R.layout.item_service);
        rvCategories.setLayoutManager(new GridLayoutManager(this, 4));
        rvCategories.setAdapter(adapter);
    }
    private void addRowRuleInsurance(LinearLayout container, String label, Boolean isHeader)
    {
        TextView tvLabel=new TextView(this);
        tvLabel.setText(label);
        if (isHeader)
        {
            tvLabel.setTextColor(Color.BLACK);
            tvLabel.setTextSize(12);
            tvLabel.setTypeface(null,BOLD);
            tvLabel.setPadding(0,16,0,16);
        } else
        {
            tvLabel.setTextColor(Color.parseColor("#757575"));
            tvLabel.setTextSize(10);
            tvLabel.setPadding(24,8,0,8);
        }
        container.addView(tvLabel);
    }
}