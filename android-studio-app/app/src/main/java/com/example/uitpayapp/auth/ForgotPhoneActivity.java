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
        // Không tự động điền số điện thoại vào ô Email nữa
        // if (phone != null) {
        //     edtPhoneNumber.setText(phone);
        // }

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
                tvErrorPhone.setText("Vui lòng nhập email");
                tvErrorPhone.setVisibility(View.VISIBLE);
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(inputPhone).matches()) {
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_error);
                tvErrorPhone.setText("Email không hợp lệ");
                tvErrorPhone.setVisibility(View.VISIBLE);
                return;
            }

            if (loadingDialog != null && !isFinishing()) loadingDialog.show();

            com.example.uitpayapp.modules.user.UserRepository userRepository = new com.example.uitpayapp.modules.user.UserRepository();
            userRepository.forgotPasswordRequest(inputPhone, new com.example.uitpayapp.network.ApiCallback<String>() {
                @Override
                public void onSuccess(String data) {
                    if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
                    android.widget.Toast.makeText(ForgotPhoneActivity.this, "Mã xác nhận đã được gửi vào email của bạn", android.widget.Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgotPhoneActivity.this, ForgotOtpActivity.class);
                    // Giữ key PHONE_NUMBER vì các màn sau vẫn đang lấy key này
                    intent.putExtra("PHONE_NUMBER", inputPhone); 
                    startActivity(intent);
                }

                @Override
                public void onError(String errorMessage) {
                    if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
                    edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_error);
                    tvErrorPhone.setText(errorMessage != null ? errorMessage : "Có lỗi xảy ra, vui lòng thử lại");
                    tvErrorPhone.setVisibility(View.VISIBLE);
                }
            });
        });
    }
}
