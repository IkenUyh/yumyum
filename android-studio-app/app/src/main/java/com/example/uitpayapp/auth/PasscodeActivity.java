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
    private static final int COLOR_ACTIVE = Color.parseColor("#FF5722"); // Chấm cam
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

        // === LOAD AVATAR VA THONG TIN USER ===
        android.widget.ImageView ivAvatar = findViewById(R.id.iv_avatar);
        TextView tvPhoneNumber = findViewById(R.id.tv_phone_number);

        // Uu tien lay tu Intent truoc (do SignInActivity truyen sang)
        String avatarUrl = getIntent().getStringExtra("AVATAR_URL");
        String fullName = getIntent().getStringExtra("FULL_NAME");

        // Neu khong co thi fallback lay tu SharedPreferences (cho truong hop tu dang ky qua hay quen mat khau)
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            android.content.SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String savedPhone = prefs.getString("PHONE_NUMBER", "");
            if (phoneNumber.equals(savedPhone)) {
                avatarUrl = prefs.getString("AVATAR_URL", "");
            }
        }

        if (avatarUrl != null && !avatarUrl.isEmpty() && !avatarUrl.equals("null") && ivAvatar != null) {
            String finalAvatarUrl = avatarUrl;
            if (!finalAvatarUrl.startsWith("http")) {
                if (finalAvatarUrl.startsWith("/")) {
                    finalAvatarUrl = finalAvatarUrl.substring(1);
                }
                // Nếu backend trả về domain không có http
                if (!finalAvatarUrl.contains("kienhuy-dev.name.vn")) {
                    finalAvatarUrl = com.example.uitpayapp.network.RetrofitClient.getBaseUrl() + finalAvatarUrl;
                } else {
                    finalAvatarUrl = "https://" + finalAvatarUrl;
                }
            }
            com.bumptech.glide.Glide.with(this)
                    .load(finalAvatarUrl)
                    .placeholder(R.drawable.bg_circle_gray)
                    .error(R.drawable.yumyum_demo_logo)
                    .circleCrop()
                    .into(ivAvatar);
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
            Intent intent = new Intent(PasscodeActivity.this, ForgotPhoneActivity.class);
            intent.putExtra("PHONE_NUMBER", phoneNumber);
            startActivity(intent);
        });

        // Nút X (Đóng) quay lại màn hình nhập số điện thoại
        findViewById(R.id.btn_close).setOnClickListener(v -> finish());
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

        userRepository.login(phoneNumber, passcode, new ApiCallback<AuthResponseDTO>() {
            @Override
            public void onSuccess(AuthResponseDTO data) {
                isChecking = false;
                if (data != null && data.getUser() != null) {
                    SessionManager sessionManager = SessionManager.getInstance(PasscodeActivity.this);
                    sessionManager.createLoginSession(
                            data.getUser().getId(),
                            data.getToken(),
                            data.getUser().getFullName(),
                            data.getUser().getPhoneNumber(),
                            data.getUser().getAvatarUrl(),
                            data.getUser().getEmail(),
                            data.getUser().getRole()
                    );
 
                    // Sync FCM Token
                    syncFcmToken(sessionManager);

                    Toast.makeText(PasscodeActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PasscodeActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    showLoginError("Dữ liệu đăng nhập phản hồi không hợp lệ!");
                }
            }

            @Override
            public void onError(String error) {
                isChecking = false;
                if (error != null && error.contains("Bad credentials")) {
                    showLoginError("Mật khẩu không đúng");
                } else {
                    showLoginError(error != null ? error : "Mật khẩu không đúng");
                }
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

    private void syncFcmToken(SessionManager sessionManager) {
        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String token = task.getResult();
                sessionManager.saveFcmToken(token);

                com.example.uitpayapp.modules.notification.NotificationRepository repo = new com.example.uitpayapp.modules.notification.NotificationRepository();
                repo.registerFcmToken(token, new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String data) {
                        sessionManager.setFcmTokenSynced(true);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        sessionManager.setFcmTokenSynced(false);
                    }
                });
            }
        });
    }
}