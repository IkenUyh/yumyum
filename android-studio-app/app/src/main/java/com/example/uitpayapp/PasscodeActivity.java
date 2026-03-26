package com.example.uitpayapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

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
        int[] dotIds = {R.id.dot_1, R.id.dot_2, R.id.dot_3, R.id.dot_4, R.id.dot_5, R.id.dot_6};
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

    // Hiển thị lỗi và auto-reset sau 1.5s
    private void handlePasscodeComplete() {
        isChecking = true;
        tvErrorMessage.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            passcode = "";
            updateDots();
            tvErrorMessage.setVisibility(View.INVISIBLE);
            isChecking = false;
        }, RESET_DELAY_MS);
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