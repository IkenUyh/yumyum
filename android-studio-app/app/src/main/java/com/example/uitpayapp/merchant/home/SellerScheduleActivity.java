package com.example.uitpayapp.merchant.home;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;

import java.util.Calendar;
import java.util.Locale;

public class SellerScheduleActivity extends AppCompatActivity {

    private TextView tvOpenTime, tvCloseTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_schedule);

        tvOpenTime = findViewById(R.id.tv_open_time);
        tvCloseTime = findViewById(R.id.tv_close_time);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        tvOpenTime.setOnClickListener(v -> showTimePicker(true));
        tvCloseTime.setOnClickListener(v -> showTimePicker(false));

        findViewById(R.id.btn_save).setOnClickListener(v -> {
            Toast.makeText(this, "Đã lưu lịch hoạt động mới", Toast.LENGTH_SHORT).show();
            finish();
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.seller_schedule_container), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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
