package com.example.uitpayapp.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.HomeActivity;
import com.example.uitpayapp.utils.KeypadManager;
import com.google.android.material.textfield.TextInputEditText; // Nhớ import thư viện này

public class RegisterOTP extends AppCompatActivity implements KeypadManager.KeypadListener {

    private KeypadManager keypadManager;
    private TextInputEditText editTextOTP; // 1. Khai báo biến

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 2. Ánh xạ view từ file XML
        editTextOTP = findViewById(R.id.editTextOTP);

        // --- CẤU HÌNH UX CHO CUSTOM KEYPAD ---
        // Chặn bàn phím mặc định của Android bật lên khi chạm vào ô input
        editTextOTP.setShowSoftInputOnFocus(false);
        // Chặn con trỏ chuột (cursor) nhấp nháy và vô hiệu hóa focus để ép dùng bàn phím custom
        editTextOTP.setFocusable(false);
        editTextOTP.setClickable(false);

        // Khởi tạo bàn phím, giới hạn 6 số
        keypadManager = new KeypadManager(this, 6, this);
    }

    @Override
    public void onPasscodeChange(String currentPasscode) {
        // 3. Cập nhật mã OTP lên giao diện mỗi khi có số thêm vào hoặc xóa đi
        if (editTextOTP != null) {
            editTextOTP.setText(currentPasscode);
        }
    }

    @Override
    public void onPasscodeComplete(String passcode) {
        // Hàm này tự động gọi khi người dùng gõ đủ 6 số
        handlePasscodeComplete(passcode);
    }

    private void handlePasscodeComplete(String passcode) {
        keypadManager.lock(); // Khóa bàn phím không cho nhập thêm

        // ... Thực hiện gọi API Xác thực OTP tại đây ...

        Toast.makeText(RegisterOTP.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterOTP.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showLoginError(String message) {
        // Tùy chỉnh hiển thị lỗi (ví dụ: dùng Toast hoặc đổi màu viền ô nhập)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        keypadManager.unlock(); // Mở khóa lại bàn phím
        keypadManager.clear();  // Xóa trắng dữ liệu mã cũ
        // Lưu ý: Hàm clear() bên trong KeypadManager sẽ tự động trigger onPasscodeChange(""),
        // nên editTextOTP cũng sẽ tự động được xóa trắng chữ trên màn hình.
    }
}