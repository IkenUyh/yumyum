package com.example.uitpayapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.HomeActivity;
import com.example.uitpayapp.modules.user.UserRepository;
import com.example.uitpayapp.modules.user.models.responses.UserResponseDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.utils.KeypadManager;

public class RegisterPinActivity extends AppCompatActivity implements KeypadManager.KeypadListener {

    private KeypadManager keypadManager;
    private TextView[] dotViews;
    private TextView tvInstruction;
    private TextView tvErrorMessage;
    private View btnClose;
    private android.app.Dialog loadingDialog;

    private String phoneNumber;
    private String fullName;
    private String referralCode;
    private String email;

    private String firstPin = null;

    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_pin);

        userRepository = new UserRepository();

        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        fullName = getIntent().getStringExtra("FULL_NAME");
        referralCode = getIntent().getStringExtra("REFERRAL_CODE");
        email = getIntent().getStringExtra("EMAIL");

        tvInstruction = findViewById(R.id.tv_instruction);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        btnClose = findViewById(R.id.btn_close);
        
        loadingDialog = new android.app.Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dotViews = new TextView[]{
                findViewById(R.id.tv_dot_1),
                findViewById(R.id.tv_dot_2),
                findViewById(R.id.tv_dot_3),
                findViewById(R.id.tv_dot_4),
                findViewById(R.id.tv_dot_5),
                findViewById(R.id.dot_6) // dot_6 doesn't have tv_ prefix in XML
        };

        btnClose.setOnClickListener(v -> finish());

        keypadManager = new KeypadManager(this, 6, this);
    }

    @Override
    public void onPasscodeChange(String currentPasscode) {
        if (tvErrorMessage != null && currentPasscode.length() > 0) {
            tvErrorMessage.setVisibility(View.INVISIBLE);
        }
        int length = currentPasscode.length();
        for (int i = 0; i < 6; i++) {
            if (i < length) {
                dotViews[i].setText("●");
                dotViews[i].setTextColor(android.graphics.Color.parseColor("#FF5722"));
            } else {
                dotViews[i].setText("○");
                dotViews[i].setTextColor(android.graphics.Color.parseColor("#BDBDBD"));
            }
        }
    }

    @Override
    public void onPasscodeComplete(String passcode) {
        keypadManager.lock();

        if (firstPin == null) {
            // Lần 1: Thiết lập PIN
            firstPin = passcode;
            tvInstruction.setText("Xác nhận lại mã PIN");
            
            // Delay một chút để người dùng thấy chấm tròn cuối cùng rồi mới reset
            tvInstruction.postDelayed(() -> {
                keypadManager.unlock();
                keypadManager.clear();
            }, 300);
        } else {
            // Lần 2: Xác nhận PIN
            if (firstPin.equals(passcode)) {
                // Khớp -> Gọi API đăng ký
                callRegisterApi(passcode);
            } else {
                // Không khớp
                if (tvErrorMessage != null) {
                    tvErrorMessage.setText("Mã PIN không khớp");
                    tvErrorMessage.setVisibility(View.VISIBLE);
                }
                firstPin = null;
                tvInstruction.setText("Thiết lập mã PIN");
                tvInstruction.postDelayed(() -> {
                    keypadManager.unlock();
                    keypadManager.clear();
                }, 300);
            }
        }
    }

    private void callRegisterApi(String pin) {
        if (loadingDialog != null && !isFinishing()) loadingDialog.show();
        keypadManager.lock();

        userRepository.register(phoneNumber, fullName, email != null ? email : "", pin, referralCode, new ApiCallback<UserResponseDTO>() {
            @Override
            public void onSuccess(UserResponseDTO data) {
                if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
                new androidx.appcompat.app.AlertDialog.Builder(RegisterPinActivity.this)
                        .setTitle("Đăng ký thành công")
                        .setMessage("Tài khoản của bạn đã được tạo thành công. Vui lòng đăng nhập để tiếp tục.")
                        .setPositiveButton("Đăng nhập ngay", (dialog, which) -> {
                            Intent intent = new Intent(RegisterPinActivity.this, SignInActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onError(String error) {
                if (loadingDialog != null && loadingDialog.isShowing()) loadingDialog.dismiss();
                keypadManager.unlock();
                keypadManager.clear();
                firstPin = null;
                tvInstruction.setText("Thiết lập mã PIN");
                Toast.makeText(RegisterPinActivity.this, error != null ? error : "Đăng ký thất bại!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
