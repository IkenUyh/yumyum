package com.example.uitpayapp.paymentorder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.money_transfer.TransferConfirmationActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

public class ElectricBillActivity extends AppCompatActivity {

    private View layoutInput, layoutResult;
    private EditText etCustomerCode;
    private TextView tvDisplayCode, tvMainResult, tvCustomerName, tvCustomerAddress;
    private AppCompatButton btnMainAction;
    private View btnShowInstruction;
    private long currentAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electric_bill);
        initViews();
        setupListeners();
        showInputMode();
    }

    private void initViews() {
        layoutInput = findViewById(R.id.layoutInput);
        layoutResult = findViewById(R.id.electric_result);
        etCustomerCode = findViewById(R.id.etCustomerCode);
        tvDisplayCode = layoutResult.findViewById(R.id.tvDisplayCode);
        tvMainResult = layoutResult.findViewById(R.id.tvMainResult);
        tvCustomerName = layoutResult.findViewById(R.id.tvCustomerName);
        tvCustomerAddress = layoutResult.findViewById(R.id.tvCustomerAddress);
        btnMainAction = findViewById(R.id.btnMainAction);
        btnShowInstruction = findViewById(R.id.btnShowInstruction);
        View topBar = findViewById(R.id.topbar_electric_bill);
        View mainContainer = findViewById(R.id.electric_bill_container);

        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom + 10;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });

        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        View btnBack = topBar.findViewById(R.id.top_bar_back_btn);
        if (tvTitle != null) tvTitle.setText("Hoá đơn điện");
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupListeners() {
        btnShowInstruction.setOnClickListener(v -> showInstructionBottomSheet());
        btnMainAction.setOnClickListener(v -> {
            if (layoutInput.getVisibility() == View.VISIBLE) {
                String code = etCustomerCode.getText().toString().trim();
                if (!code.isEmpty()) {
                    showResultMode(code);
                } else {
                    etCustomerCode.setError("Vui lòng nhập mã KH");
                }
            } else {
                if (currentAmount > 0) {
                    //Xac nhan thanh toan
                    Intent intent = new Intent(this, TransferConfirmationActivity.class);
                    intent.putExtra("KEY_AMOUNT", String.valueOf(currentAmount));
                    intent.putExtra("KEY_NAME", etCustomerCode.getText().toString().trim());
                    intent.putExtra("KEY_AVATAR", R.drawable.ic_evn);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
        });
        tvDisplayCode.setOnClickListener(v -> showInputMode());
    }

    private void showInputMode() {
        layoutInput.setVisibility(View.VISIBLE);
        layoutResult.setVisibility(View.GONE);
        btnMainAction.setText("Tiếp tục");
        currentAmount = 0;
    }

    private void showResultMode(String code) {
        layoutInput.setVisibility(View.GONE);
        layoutResult.setVisibility(View.VISIBLE);
        tvDisplayCode.setText(code.toUpperCase());

        Random random = new Random();
        if (random.nextBoolean()) {
            // chua toi han
            currentAmount = 0;
            tvMainResult.setText("Chưa đến kì thanh toán tiếp theo");
            btnMainAction.setText("Đóng");
        } else {
            //random so tien
            currentAmount = (100 + random.nextInt(1900)) * 1000L;
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            tvMainResult.setText(formatter.format(currentAmount) + "đ");
            btnMainAction.setText("Thanh toán");
        }
        //phu tro random ten cho sinh dong
        String[] names = {"NGUYỄN VĂN A", "TRẦN THỊ B", "LÊ VĂN C"};
        String[] addresses = {"48 Phó Cơ Điều", "123 Cách Mạng Tháng 8", "456 Võ Văn Tần"};
        tvCustomerName.setText(names[random.nextInt(names.length)]);
        tvCustomerAddress.setText(addresses[random.nextInt(addresses.length)]);
    }

    private void showInstructionBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_electric_instruction, null);
        bottomSheetDialog.setContentView(view);
        view.findViewById(R.id.btn_close).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }
}
