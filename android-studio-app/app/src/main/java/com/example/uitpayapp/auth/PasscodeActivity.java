package com.example.uitpayapp.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.profile.ContactSupportActivity;
import com.example.uitpayapp.home.HomeActivity;
import com.example.uitpayapp.R;

// Thêm các thư viện import của DTO và Retrofit
import com.example.uitpayapp.models.ApiResponse;
import com.example.uitpayapp.models.LoginRequestDTO;
import com.example.uitpayapp.models.UserResponseDTO;
import com.example.uitpayapp.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    // Thêm biến để hứng số điện thoại từ màn hình trước
    private String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        // Bắt lấy số điện thoại được truyền sang
        phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        if (phoneNumber == null) phoneNumber = "";

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
        tvErrorMessage.setVisibility(View.INVISIBLE); // Ẩn lỗi nếu đang nhập lại

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

    // XỬ LÝ GỌI API KHI NHẬP ĐỦ 6 SỐ
    private void handlePasscodeComplete() {
        isChecking = true;

        // Tạo gói dữ liệu chuẩn bị gửi đi
        LoginRequestDTO request = new LoginRequestDTO(phoneNumber, passcode);

        RetrofitClient.getApiService().login(request).enqueue(new Callback<ApiResponse<UserResponseDTO>>() {
            @Override
            public void onResponse(Call<ApiResponse<UserResponseDTO>> call, Response<ApiResponse<UserResponseDTO>> response) {
                isChecking = false; // Mở khóa cho nhập tiếp nếu lỡ sai

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<UserResponseDTO> apiResponse = response.body();

                    if (apiResponse.getCode() == 200) {
                        // Thành công: Chuyển sang HomeActivity
                        Toast.makeText(PasscodeActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PasscodeActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sai pass hoặc lỗi backend trả về
                        showLoginError(apiResponse.getMessage());
                    }
                } else {
                    showLoginError("Hệ thống đang bận, thử lại sau!");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<UserResponseDTO>> call, Throwable t) {
                isChecking = false;
                Log.e("API_ERROR", "Lỗi: " + t.getMessage());
                showLoginError("Mất kết nối máy chủ!");
            }
        });
    }

    // Hàm phụ trợ để báo lỗi và reset bàn phím
    private void showLoginError(String message) {
        tvErrorMessage.setText(message);
        tvErrorMessage.setVisibility(View.VISIBLE);
        // Xóa mã PIN và reset giao diện dấu chấm để người dùng nhập lại
        passcode = "";
        updateDots();
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