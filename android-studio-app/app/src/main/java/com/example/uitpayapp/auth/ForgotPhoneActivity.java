package com.example.uitpayapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.R;

public class ForgotPhoneActivity extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private TextView tvErrorPhone;
    private View btnContinue;
    private View btnBack;
    private android.app.Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_phone);

        edtPhoneNumber = findViewById(R.id.edt_phone_number);
        tvErrorPhone = findViewById(R.id.tv_error_phone);
        btnContinue = findViewById(R.id.btn_continue);
        btnBack = findViewById(R.id.btn_back);

        String phone = getIntent().getStringExtra("PHONE_NUMBER");
        if (phone != null) {
            edtPhoneNumber.setText(phone);
        }

        loadingDialog = new android.app.Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnBack.setOnClickListener(v -> finish());

        edtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_rounded);
                tvErrorPhone.setVisibility(View.GONE);
            }
        });

        btnContinue.setOnClickListener(v -> {
            String inputPhone = edtPhoneNumber.getText().toString().trim();

            if (inputPhone.isEmpty()) {
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_error);
                tvErrorPhone.setText("Vui lòng nhập số điện thoại");
                tvErrorPhone.setVisibility(View.VISIBLE);
                return;
            }

            if (loadingDialog != null && !isFinishing()) loadingDialog.show();

            // Mocking API call delay
            new Handler().postDelayed(() -> {
                if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
                Intent intent = new Intent(ForgotPhoneActivity.this, ForgotOtpActivity.class);
                intent.putExtra("PHONE_NUMBER", inputPhone);
                startActivity(intent);
            }, 500);
        });
    }
}
