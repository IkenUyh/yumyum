package com.example.uitpayapp.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.R;

public class ForgotOtpActivity extends AppCompatActivity {

    private static final int MAX_LENGTH = 6;
    private static final int COLOR_ACTIVE = Color.parseColor("#FF5722"); // Chấm cam
    private static final int COLOR_INACTIVE = Color.parseColor("#BDBDBD"); // Chấm xám

    private final TextView[] dots = new TextView[MAX_LENGTH];
    private String passcode = "";
    private TextView tvErrorMessage;
    private boolean isChecking = false;
    private String phoneNumber;
    private android.app.Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_otp);

        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        if (phoneNumber == null) phoneNumber = "";

        initViews();
        setupListeners();
    }

    private void initViews() {
        int[] dotIds = {R.id.tv_dot_1, R.id.tv_dot_2, R.id.tv_dot_3, R.id.tv_dot_4, R.id.tv_dot_5, R.id.dot_6};
        for (int i = 0; i < MAX_LENGTH; i++) {
            dots[i] = findViewById(dotIds[i]);
        }
        tvErrorMessage = findViewById(R.id.tv_error_message);

        loadingDialog = new android.app.Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void setupListeners() {
        int[] buttonIds = {R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9};

        for (int i = 0; i < buttonIds.length; i++) {
            final String number = String.valueOf(i);
            findViewById(buttonIds[i]).setOnClickListener(v -> onNumberClick(number));
        }

        findViewById(R.id.btn_delete).setOnClickListener(v -> onDeleteClick());

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
    }

    private void onNumberClick(String number) {
        if (isChecking || passcode.length() >= MAX_LENGTH) return;

        passcode += number;
        updateDots();
        tvErrorMessage.setVisibility(View.INVISIBLE);

        if (passcode.length() == MAX_LENGTH) {
            verifyOtp();
        }
    }

    private void onDeleteClick() {
        if (isChecking || passcode.length() == 0) return;

        passcode = passcode.substring(0, passcode.length() - 1);
        updateDots();
    }

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

    private void verifyOtp() {
        isChecking = true;
        
        // Chuyển sang màn hình nhập PIN mới và truyền OTP theo
        Intent intent = new Intent(ForgotOtpActivity.this, ForgotPinActivity.class);
        intent.putExtra("PHONE_NUMBER", phoneNumber); // Thực chất là email
        intent.putExtra("OTP", passcode);
        startActivity(intent);
        
        finish();
    }
}
