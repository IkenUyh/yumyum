package com.example.uitpayapp.auth;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uitpayapp.modules.user.models.responses.AuthResponseDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.SessionManager;
import com.example.uitpayapp.profile.ContactSupportActivity;
import com.example.uitpayapp.home.HomeActivity;
import com.example.uitpayapp.R;

// Thêm các thư viện import của DTO và Retrofit
import com.example.uitpayapp.modules.user.models.responses.UserResponseDTO;
import com.example.uitpayapp.modules.user.UserRepository;

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

        // === LOAD AVATAR VA THONG TIN USER TU CACHE ===
        android.widget.ImageView ivAvatar = findViewById(R.id.iv_avatar);
        TextView tvUsername = findViewById(R.id.tv_username);
        TextView tvPhoneNumber = findViewById(R.id.tv_phone_number);

        android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedPhone = prefs.getString("PHONE_NUMBER", "");
        String savedName = prefs.getString("FULL_NAME", "Username");
        String avatarUrl = prefs.getString("AVATAR_URL", "");

        // Kiem tra: Neu khop so dien thoai cu thi hien avatar
        if (phoneNumber.equals(savedPhone)) {
            if (!avatarUrl.isEmpty() && ivAvatar != null) {
                com.bumptech.glide.Glide.with(this)
                        .load(avatarUrl)
                        .circleCrop()
                        .into(ivAvatar);
            }
        }
        else {
            // Neu khong khop thi chi de chu "Username" co dinh
            tvUsername.setText("Username");
        }

        // Hien thi so dien thoai da duoc che (Masked) voi ma vung
        tvPhoneNumber.setText(formatMaskedPhone(phoneNumber));
    }

    // === HAM XU LY CHE SO DIEN THOAI VA MA VUNG ===
    private String formatMaskedPhone(String phone) {
        if (phone == null || phone.length() < 7) return phone;

        // Mac dinh +84 cho Viet Nam.
        // Co the ket hop TelephonyManager de lay dong theo quoc gia hien tai.
        String countryCode = "+84";

        String cleanPhone = phone;
        if (phone.startsWith("0")) {
            cleanPhone = phone.substring(1); // Cat so 0 o dau
        }

        // Lay 1 so dau (sau so 0) va 3 so cuoi, o giua la 4 dau sao
        if (cleanPhone.length() >= 4) {
            String firstDigit = cleanPhone.substring(0, 1);
            String lastThreeDigits = cleanPhone.substring(cleanPhone.length() - 3);
            return countryCode + firstDigit + "****" + lastThreeDigits;
        }

        return countryCode + cleanPhone;
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

    // Xử lý gọi API khi nhập đủ 6 số
// Khai báo UserRepository ở cấp độ Class của Activity để tái sử dụng
    private final UserRepository userRepository = new UserRepository();

    private void handlePasscodeComplete() {
        isChecking = true;

        // Gọi hàm qua lớp cầu nối UserRepository theo cấu trúc mới
        userRepository.login(phoneNumber, passcode, new ApiCallback<AuthResponseDTO>() {
            @Override
            public void onSuccess(AuthResponseDTO result) {
                isChecking = false; // Mở khóa trạng thái

                // Nhận dữ liệu user từ gói AuthResponseDTO do backend trả về
                UserResponseDTO user = result.getUser();
                String token = result.getToken();

                // === THAY ĐỔI Ở ĐÂY: Sử dụng SessionManager ===
                SessionManager sessionManager = SessionManager.getInstance(PasscodeActivity.this);

                // Cách 1: Nếu bạn giữ nguyên SessionManager cũ (chỉ lưu token và tên)
                // sessionManager.createLoginSession(token, user.getFullName());

                // Cách 2: Sử dụng SessionManager đã nâng cấp (Khuyên dùng - xem cấu hình ở Bước 2)
                sessionManager.createLoginSession(token, user.getFullName(), user.getPhoneNumber(), user.getAvatarUrl());
                // =============================================

                // Chuyển màn hình sang HomeActivity
                Toast.makeText(PasscodeActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PasscodeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String errorMessage) {
                isChecking = false;
                // errorMessage ở đây đã được Repository xử lý sạch
                showLoginError(errorMessage);
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