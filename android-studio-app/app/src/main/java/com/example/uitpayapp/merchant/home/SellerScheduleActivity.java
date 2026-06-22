package com.example.uitpayapp.merchant.home;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.restaurant.RestaurantRepository;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;
import com.example.uitpayapp.modules.restaurant.models.UpdateRestaurantInfoDTO;
import com.example.uitpayapp.network.ApiCallback;

import java.util.Calendar;
import java.util.Locale;

public class SellerScheduleActivity extends AppCompatActivity {

    private TextView tvOpenTime, tvCloseTime;
    private long restaurantId;
    private RestaurantRepository restaurantRepository;
    private String currentName = "";
    private String currentAddress = "";
    private String currentImageUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_schedule);

        restaurantRepository = new RestaurantRepository();

        // Lấy restaurantId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        restaurantId = prefs.getLong("current_store_id", -1L);

        tvOpenTime = findViewById(R.id.tv_open_time);
        tvCloseTime = findViewById(R.id.tv_close_time);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tvOpenTime.setOnClickListener(v -> showTimePicker(true));
        tvCloseTime.setOnClickListener(v -> showTimePicker(false));

        findViewById(R.id.btn_save).setOnClickListener(v -> saveSchedule());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seller_schedule_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadCurrentSchedule();
    }

    private void loadCurrentSchedule() {
        if (restaurantId == -1L) {
            Toast.makeText(this, "Không tìm thấy thông tin cửa hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        restaurantRepository.getRestaurantById(restaurantId, new ApiCallback<RestaurantResponseDTO>() {
            @Override
            public void onSuccess(RestaurantResponseDTO data) {
                runOnUiThread(() -> {
                    if (data.getName() != null) currentName = data.getName();
                    if (data.getAddress() != null) currentAddress = data.getAddress();
                    if (data.getImageUrl() != null) currentImageUrl = data.getImageUrl();

                    if (data.getOpenTime() != null) {
                        String open = data.getOpenTime();
                        if (open.length() >= 5) {
                            tvOpenTime.setText(open.substring(0, 5));
                        }
                    }
                    if (data.getCloseTime() != null) {
                        String close = data.getCloseTime();
                        if (close.length() >= 5) {
                            tvCloseTime.setText(close.substring(0, 5));
                        }
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(SellerScheduleActivity.this, "Lỗi tải lịch hoạt động: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void saveSchedule() {
        if (restaurantId == -1L) return;
        String open = tvOpenTime.getText().toString().trim();
        String close = tvCloseTime.getText().toString().trim();

        if (open.length() == 5) open += ":00";
        if (close.length() == 5) close += ":00";

        UpdateRestaurantInfoDTO dto = new UpdateRestaurantInfoDTO(currentName, currentAddress, open, close, currentImageUrl);
        restaurantRepository.updateRestaurantInfo(restaurantId, dto, new ApiCallback<RestaurantResponseDTO>() {
            @Override
            public void onSuccess(RestaurantResponseDTO data) {
                runOnUiThread(() -> {
                    Toast.makeText(SellerScheduleActivity.this, "Đã lưu lịch hoạt động mới", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(SellerScheduleActivity.this, "Lỗi lưu thay đổi: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showTimePicker(boolean isOpenTime) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
            String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
            if (isOpenTime) {
                tvOpenTime.setText(time);
            } else {
                tvCloseTime.setText(time);
            }
        }, hour, minute, true);

        timePickerDialog.show();
    }
}
