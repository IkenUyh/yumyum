package com.example.uitpayapp.paymentorder;

import static android.view.View.GONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.money_transfer.TransferConfirmationActivity;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class WaterBillInputActivity extends AppCompatActivity {

    private String providerName;
    private View layoutInput, layoutResult;
    private EditText etCustomerCode;
    private TextView tvDisplayCode, tvAmountResult, tvSubProvider;
    private AppCompatButton btnAction;
    private long currentAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_bill_input);

        providerName = getIntent().getStringExtra("PROVIDER_NAME");
        if (providerName == null) providerName = "Nước Hồ Chí Minh";

        initViews();
        showInputMode();
    }

    private void initViews() {
        View topBar = findViewById(R.id.top_bar_water_input);
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Nhập thông tin hoá đơn");
        View mainContainer=findViewById(R.id.water_bill_input_container);
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

        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        layoutInput = findViewById(R.id.layout_water_input);
        layoutResult = findViewById(R.id.water_result);
        
        TextView tvTitleInput = findViewById(R.id.tv_provider_name_input);
        tvTitleInput.setText(providerName);

        etCustomerCode = findViewById(R.id.et_water_customer_code);
        tvSubProvider = findViewById(R.id.tv_select_sub_provider);
        tvDisplayCode = layoutResult.findViewById(R.id.tvDisplayCode);
        tvAmountResult = layoutResult.findViewById(R.id.tvMainResult);
        layoutResult.findViewById(R.id.customer_service_info_container).setVisibility(GONE);
        btnAction = findViewById(R.id.btn_water_action);

        tvSubProvider.setOnClickListener(v -> showSubProviderMenu());

        btnAction.setOnClickListener(v -> {
            if (layoutInput.getVisibility() == View.VISIBLE) {
                String code = etCustomerCode.getText().toString().trim();
                if (code.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập mã khách hàng", Toast.LENGTH_SHORT).show();
                    return;
                }
                showResultMode(code);
            } else {
                if (currentAmount > 0) {
                    Intent intent = new Intent(this, TransferConfirmationActivity.class);
                    intent.putExtra("KEY_AMOUNT", String.valueOf(currentAmount));
                    intent.putExtra("KEY_NAME",tvDisplayCode.getText().toString());
                    intent.putExtra("KEY_AVATAR",R.drawable.ic_receiptscreen_water);
                    startActivity(intent);
                } else {
                    showInputMode();
                }
            }
        });
    }

    private void showInputMode() {
        layoutInput.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(GONE);
        btnAction.setText("Tiếp tục");
        currentAmount = 0;
    }

    private void showResultMode(String code) {
        layoutInput.setVisibility(GONE);
        layoutResult.setVisibility(View.VISIBLE);
        tvDisplayCode.setText(code.toUpperCase());

        Random random = new Random();
        if (random.nextBoolean()) {
            currentAmount = 0;
            tvAmountResult.setText("Chưa đến kì thanh toán tiếp theo");
            btnAction.setText("Đóng");
        } else {
            currentAmount = (30 + random.nextInt(500)) * 1000L;
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvAmountResult.setText(formatter.format(currentAmount) + "đ");
            btnAction.setText("Thanh toán");
        }
    }

    private void showSubProviderMenu() {
        PopupMenu popup = new PopupMenu(this, tvSubProvider);
        String[] subs = {"Cấp nước Chợ Lớn", "Cấp nước Bến Thành", "Cấp nước Phú Hòa Tân", "Cấp nước Tân Hòa"};
        for (String s : subs) popup.getMenu().add(s);
        
        popup.setOnMenuItemClickListener(item -> {
            tvSubProvider.setText(item.getTitle());
            return true;
        });
        popup.show();
    }
}
