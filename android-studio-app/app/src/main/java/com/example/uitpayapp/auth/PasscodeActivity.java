package com.example.uitpayapp.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.profile.ContactSupportActivity;
import com.example.uitpayapp.home.HomeActivity;
import com.example.uitpayapp.R;

public class PasscodeActivity extends AppCompatActivity {

    // --- Cấu hình hằng số (Constants) ---
    private static final int MAX_LENGTH = 6;
    private static final int RESET_DELAY_MS = 1500;
    private static final int COLOR_ACTIVE = Color.parseColor("#0052CC"); // Chấm xanh
    private static final int COLOR_INACTIVE = Color.parseColor("#BDBDBD"); // Chấm xám

    private final TextView[] dots = new TextView[MAX_LENGTH];
    private String passcode = "";
    private TextView tvErrorMessage;
    private boolean isChecking = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_passcode);

        initViews();
        setupListeners();
    }

    // Ánh xạ giao diện
    private void initViews() {
        int[] dotIds = {R.id.tv_dot_1, R.id.tv_dot_2, R.id.tv_dot_3, R.id.tv_dot_4, R.id.tv_dot_5, R.id.dot_6};
        for (int i = 0; i < MAX_LENGTH; i++) {
            dots[i] = findViewById(dotIds[i]);
        }
        tvErrorMessage = findViewById(R.id.tv_error_message);
    }

    // Cài đặt sự kiện click cho bàn phím
    private void setupListeners() {
        int[] buttonIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};

        for (int i = 0; i < buttonIds.length; i++) {
            final String number = String.valueOf(i);
            findViewById(buttonIds[i]).setOnClickListener(v -> onNumberClick(number));
        }

        findViewById(R.id.btn_delete).setOnClickListener(v -> onDeleteClick());

        //Tìm text quên mật khẩu
        TextView btnForgotPass = findViewById(R.id.btn_forgot_pass);
        btnForgotPass.setOnClickListener(v -> {
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                    new com.google.android.material.bottomsheet.BottomSheetDialog(PasscodeActivity.this);
            View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_forgot, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            TextView btnCloseX = bottomSheetView.findViewById(R.id.btn_close_sheet);
            btnCloseX.setOnClickListener(v1 -> {
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        });

        //Tìm text Đây không phải tài khoản của tôi
        TextView btnNotMyAccount = findViewById(R.id.btn_not_my_account);
        btnNotMyAccount.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(PasscodeActivity.this, ContactSupportActivity.class);

            startActivity(intent);
        });
    }

    private void onNumberClick(String number) {
        // Chặn nhập thêm nếu đang chờ reset hoặc đã nhập đủ
        if (isChecking || passcode.length() >= MAX_LENGTH) return;

        passcode += number;
        updateDots();

        // Xử lý khi nhập đủ 6 số
        if (passcode.length() == MAX_LENGTH) {
            handlePasscodeComplete();
        }
    }

    private void onDeleteClick() {
        // Chặn xóa nếu đang chờ reset hoặc chưa nhập gì
        if (isChecking || passcode.isEmpty()) return;

        passcode = passcode.substring(0, passcode.length() - 1);
        updateDots();
        tvErrorMessage.setVisibility(View.INVISIBLE);
    }

    // Chuyển sang HomeActivity khi nhập đủ 6 số
    private void handlePasscodeComplete() {
        isChecking = true;
        
        // Tạo Intent để chuyển sang HomeActivity
        Intent intent = new Intent(PasscodeActivity.this, HomeActivity.class);
        startActivity(intent);
        
        // Kết thúc PasscodeActivity để người dùng không quay lại được bằng nút Back
        finish();
    }

    // Cập nhật giao diện 6 dấu chấm
    private void updateDots() {
        for (int i = 0; i < MAX_LENGTH; i++) {
            if (i < passcode.length()) {
                dots[i].setText("●");
                dots[i].setTextColor(COLOR_ACTIVE);
            } else {
                dots[i].setText("○");
                dots[i].setTextColor(COLOR_INACTIVE);
            }
        }
    }
}