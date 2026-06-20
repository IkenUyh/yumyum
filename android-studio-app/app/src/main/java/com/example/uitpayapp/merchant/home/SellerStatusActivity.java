package com.example.uitpayapp.merchant.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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
import com.example.uitpayapp.modules.restaurant.models.RestaurantSettingsDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.Calendar;

public class SellerStatusActivity extends AppCompatActivity {

    private TextView tvShopName;
    private TextView tvStatusText;
    private TextView tvOpeningHours;
    private long restaurantId;
    private RestaurantRepository restaurantRepository;
    private RestaurantResponseDTO currentRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_status);

        restaurantRepository = new RestaurantRepository();

        // Lấy restaurantId từ SharedPreferences
        SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        restaurantId = prefs.getLong("current_store_id", -1L);

        tvShopName = findViewById(R.id.tv_shop_name);
        tvStatusText = findViewById(R.id.tv_status_text);
        tvOpeningHours = findViewById(R.id.tv_opening_hours);

        tvStatusText.setOnClickListener(v -> {
            if (currentRestaurant != null && currentRestaurant.getIsAcceptingOrders() != null && !currentRestaurant.getIsAcceptingOrders()) {
                updateAcceptingOrders(true);
            }
        });

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_regular_schedule).setOnClickListener(v -> {
            startActivity(new Intent(this, SellerScheduleActivity.class));
        });

        findViewById(R.id.btn_busy_settings).setOnClickListener(v -> showBusySettingsBottomSheet());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seller_status_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRestaurantData();
    }

    private void loadRestaurantData() {
        if (restaurantId == -1L) {
            Toast.makeText(this, "Không tìm thấy thông tin cửa hàng", Toast.LENGTH_SHORT).show();
            return;
        }
        restaurantRepository.getRestaurantById(restaurantId, new ApiCallback<RestaurantResponseDTO>() {
            @Override
            public void onSuccess(RestaurantResponseDTO data) {
                currentRestaurant = data;
                runOnUiThread(() -> updateUI());
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(SellerStatusActivity.this, "Lỗi tải thông tin: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void updateUI() {
        if (currentRestaurant == null) return;

        tvShopName.setText(currentRestaurant.getName());
        tvOpeningHours.setText("Giờ hoạt động: " + currentRestaurant.getOpenTime() + " - " + currentRestaurant.getCloseTime());

        boolean isAccepting = currentRestaurant.getIsAcceptingOrders() != null && currentRestaurant.getIsAcceptingOrders();
        if (!isAccepting) {
            tvStatusText.setText("Tạm đóng cửa (Khẩn cấp/Bận)");
            tvStatusText.setTextColor(Color.RED);
        } else {
            boolean inHours = isCurrentTimeInOpenHours(currentRestaurant.getOpenTime(), currentRestaurant.getCloseTime());
            if (inHours) {
                tvStatusText.setText("Mở cửa");
                tvStatusText.setTextColor(Color.parseColor("#4CAF50")); // Green
            } else {
                tvStatusText.setText("Đóng cửa (Ngoài giờ hoạt động)");
                tvStatusText.setTextColor(Color.RED);
            }
        }
    }

    private boolean isCurrentTimeInOpenHours(String openTime, String closeTime) {
        if (openTime == null || closeTime == null || openTime.isEmpty() || closeTime.isEmpty()) {
            return false;
        }
        try {
            String[] openParts = openTime.split(":");
            String[] closeParts = closeTime.split(":");
            if (openParts.length < 2 || closeParts.length < 2) return false;

            int openHour = Integer.parseInt(openParts[0]);
            int openMin = Integer.parseInt(openParts[1]);
            int closeHour = Integer.parseInt(closeParts[0]);
            int closeMin = Integer.parseInt(closeParts[1]);

            Calendar now = Calendar.getInstance();
            int nowHour = now.get(Calendar.HOUR_OF_DAY);
            int nowMin = now.get(Calendar.MINUTE);

            int nowVal = nowHour * 60 + nowMin;
            int openVal = openHour * 60 + openMin;
            int closeVal = closeHour * 60 + closeMin;

            if (closeVal < openVal) {
                // Trường hợp mở cửa qua đêm (vd: 22:00 -> 02:00 sáng hôm sau)
                return nowVal >= openVal || nowVal <= closeVal;
            } else {
                return nowVal >= openVal && nowVal <= closeVal;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateAcceptingOrders(boolean accept) {
        if (restaurantId == -1L) return;
        RestaurantSettingsDTO dto = new RestaurantSettingsDTO(accept, null);
        restaurantRepository.updateRestaurantSettings(restaurantId, dto, new ApiCallback<RestaurantResponseDTO>() {
            @Override
            public void onSuccess(RestaurantResponseDTO data) {
                currentRestaurant = data;
                runOnUiThread(() -> {
                    updateUI();
                    String msg = accept ? "Quán đã mở cửa trở lại" : "Quán đã tạm đóng cửa";
                    Toast.makeText(SellerStatusActivity.this, msg, Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(SellerStatusActivity.this, "Lỗi cập nhật trạng thái: " + message, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void showBusySettingsBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_busy_settings, null);
        dialog.setContentView(view);

        view.findViewById(R.id.layout_emergency_close).setOnClickListener(v -> {
            showDurationOptions("Đóng quán khẩn cấp");
            dialog.dismiss();
        });

        view.findViewById(R.id.layout_overload_close).setOnClickListener(v -> {
            showDurationOptions("Đóng quán (Quá tải)");
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showDurationOptions(String reason) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_busy_duration, null);
        dialog.setContentView(view);

        ((TextView) view.findViewById(R.id.tv_title)).setText(reason);

        View.OnClickListener durationListener = v -> {
            updateAcceptingOrders(false);
            dialog.dismiss();
        };

        view.findViewById(R.id.tv_15m).setOnClickListener(durationListener);
        view.findViewById(R.id.tv_30m).setOnClickListener(durationListener);
        view.findViewById(R.id.tv_1h).setOnClickListener(durationListener);
        view.findViewById(R.id.tv_until_reopen).setOnClickListener(durationListener);

        dialog.show();
    }
}
