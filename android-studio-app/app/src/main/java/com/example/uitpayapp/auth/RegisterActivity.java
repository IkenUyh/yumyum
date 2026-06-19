package com.example.uitpayapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.user.UserRepository;
import com.example.uitpayapp.modules.user.models.responses.AuthResponseDTO;
import com.example.uitpayapp.network.ApiCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtPhoneNumber;
    private EditText edtFullName;
    private EditText edtReferralCode;
    private TextView tvToggleReferral;
    private TextView tvErrorPhone;
    private TextView tvErrorName;
    private View btnContinue;
    private View btnBack;
    private android.app.Dialog loadingDialog;

    private UserRepository userRepository;
    private boolean isCheckingPhone = false;
    private Boolean isPhoneValid = null;
    private String lastCheckedPhone = "";
    private boolean pendingContinue = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtPhoneNumber = findViewById(R.id.edt_phone_number);
        edtFullName = findViewById(R.id.edt_full_name);
        edtReferralCode = findViewById(R.id.edt_referral_code);
        tvToggleReferral = findViewById(R.id.tv_toggle_referral);
        tvErrorPhone = findViewById(R.id.tv_error_phone);
        tvErrorName = findViewById(R.id.tv_error_name);
        btnContinue = findViewById(R.id.btn_continue);
        btnBack = findViewById(R.id.btn_back);
        
        loadingDialog = new android.app.Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        userRepository = new UserRepository();

        edtPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String phone = edtPhoneNumber.getText().toString().trim();
                if (!phone.isEmpty() && !phone.equals(lastCheckedPhone)) {
                    checkPhoneNumber(phone);
                }
            }
        });

        btnBack.setOnClickListener(v -> finish());

        tvToggleReferral.setOnClickListener(v -> {
            if (edtReferralCode.getVisibility() == View.GONE) {
                edtReferralCode.setVisibility(View.VISIBLE);
                tvToggleReferral.setText("Ẩn mã giới thiệu");
            } else {
                edtReferralCode.setVisibility(View.GONE);
                edtReferralCode.setText("");
                tvToggleReferral.setText("Tôi có mã giới thiệu");
            }
        });

        btnContinue.setOnClickListener(v -> {
            String phone = edtPhoneNumber.getText().toString().trim();
            String name = edtFullName.getText().toString().trim();
            String referral = edtReferralCode.getText().toString().trim();

            boolean isValid = true;

            if (phone.isEmpty()) {
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_error);
                tvErrorPhone.setText("Vui lòng nhập số điện thoại");
                tvErrorPhone.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_rounded);
                tvErrorPhone.setVisibility(View.GONE);
            }

            if (name.isEmpty()) {
                edtFullName.setBackgroundResource(R.drawable.bg_edittext_error);
                tvErrorName.setVisibility(View.VISIBLE);
                isValid = false;
            } else {
                edtFullName.setBackgroundResource(R.drawable.bg_edittext_rounded);
                tvErrorName.setVisibility(View.GONE);
            }

            if (!isValid) return;

            if (!phone.equals(lastCheckedPhone)) {
                pendingContinue = true;
                checkPhoneNumber(phone);
                return;
            }

            if (isCheckingPhone) {
                pendingContinue = true;
                if (loadingDialog != null && !isFinishing()) loadingDialog.show();
                return;
            }

            if (Boolean.FALSE.equals(isPhoneValid)) {
                edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_error);
                tvErrorPhone.setText("Số điện thoại này đã được đăng ký.");
                tvErrorPhone.setVisibility(View.VISIBLE);
                return;
            }

            proceedToNextStep();
        });
    }

    private void checkPhoneNumber(String phone) {
        isCheckingPhone = true;
        lastCheckedPhone = phone;
        if (pendingContinue && loadingDialog != null && !isFinishing()) {
            loadingDialog.show();
        }

        new android.os.Handler().postDelayed(() -> {
            isPhoneValid = true; // Always valid for mock
            handleCheckResult();
        }, 500);
    }

    private void handleCheckResult() {
        isCheckingPhone = false;
        if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();

        if (Boolean.FALSE.equals(isPhoneValid)) {
            edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_error);
            tvErrorPhone.setText("Số điện thoại này đã được đăng ký.");
            tvErrorPhone.setVisibility(View.VISIBLE);
            pendingContinue = false;
        } else {
            edtPhoneNumber.setBackgroundResource(R.drawable.bg_edittext_rounded);
            tvErrorPhone.setVisibility(View.GONE);
            if (pendingContinue) {
                pendingContinue = false;
                String name = edtFullName.getText().toString().trim();
                if (name.isEmpty()) {
                    edtFullName.setBackgroundResource(R.drawable.bg_edittext_error);
                    tvErrorName.setVisibility(View.VISIBLE);
                    return;
                }
                proceedToNextStep();
            }
        }
    }

    private void proceedToNextStep() {
        String phone = edtPhoneNumber.getText().toString().trim();
        String name = edtFullName.getText().toString().trim();
        String referral = edtReferralCode.getText().toString().trim();
        
        EditText edtEmail = findViewById(R.id.edt_email);
        String email = edtEmail != null ? edtEmail.getText().toString().trim() : "";

        Intent intent = new Intent(this, RegisterPinActivity.class);
        intent.putExtra("PHONE_NUMBER", phone);
        intent.putExtra("FULL_NAME", name);
        intent.putExtra("REFERRAL_CODE", referral);
        intent.putExtra("EMAIL", email);
        startActivity(intent);
    }
}
