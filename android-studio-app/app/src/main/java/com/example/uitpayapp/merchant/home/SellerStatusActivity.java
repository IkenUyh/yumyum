package com.example.uitpayapp.merchant.home;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SellerStatusActivity extends AppCompatActivity {

    private TextView tvStatusText;
    private boolean isStatusOpen;
    private View viewStatusDot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_status);
        isStatusOpen = true;
        tvStatusText = findViewById(R.id.tv_status_text);
        tvStatusText.setOnClickListener(v->{
            if (!isStatusOpen) {
                updateStatusToOpen();
                isStatusOpen = true;
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
            String duration = ((TextView) v).getText().toString();
            Toast.makeText(this, reason + " trong " + duration, Toast.LENGTH_SHORT).show();
            updateStatusToClosed();
            dialog.dismiss();
        };

        view.findViewById(R.id.tv_15m).setOnClickListener(durationListener);
        view.findViewById(R.id.tv_30m).setOnClickListener(durationListener);
        view.findViewById(R.id.tv_1h).setOnClickListener(durationListener);
        view.findViewById(R.id.tv_until_reopen).setOnClickListener(durationListener);

        dialog.show();
    }

    private void updateStatusToClosed() {
        isStatusOpen = false;
        tvStatusText.setText("Đang đóng cửa");
        tvStatusText.setTextColor(Color.RED);
    }
    private void updateStatusToOpen() {
        isStatusOpen =true;
        Toast.makeText(this, "Quán đã mở cửa trở lại", Toast.LENGTH_SHORT).show();
        tvStatusText.setText("Mở cửa");
        tvStatusText.setTextColor(Color.GREEN);
    }
}
