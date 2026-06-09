package com.example.uitpayapp.home.checkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uitpayapp.R;
import com.example.uitpayapp.home.HomeActivity;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransferFailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_failed);

        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        findViewById(R.id.btn_close_action).setOnClickListener(v -> returnToHome());
        findViewById(R.id.btn_try_again).setOnClickListener(v -> finish());

        int avatarId = getIntent().getIntExtra("KEY_AVATAR", R.drawable.img_usagi);
        String name = getIntent().getStringExtra("KEY_NAME");
        String amount = getIntent().getStringExtra("KEY_AMOUNT");

        ImageView ivFailedAvatar = findViewById(R.id.iv_recipient_avatar_failed);
        TextView tvFailedName = findViewById(R.id.tv_recipient_name_failed);
        TextView tvFailedAmount = findViewById(R.id.tv_failed_amount);
        TextView tvTransactionTime = findViewById(R.id.tv_transaction_time);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        tvTransactionTime.setText("Thời gian: " + currentTime);

        boolean isFoodOrder = getIntent().getBooleanExtra("KEY_IS_FOOD_ORDER", false);

        if (isFoodOrder) {
            ivFailedAvatar.setImageResource(R.drawable.ic_food);
            ivFailedAvatar.setPadding(10, 10, 10, 10);
            ivFailedAvatar.setColorFilter(android.graphics.Color.parseColor("#F57C00"));
            tvFailedName.setText("UIT FOOD");
        } else {
            ivFailedAvatar.setImageResource(avatarId);
            if (name != null) {
                tvFailedName.setText(name.toUpperCase());
            }
        }

        if (amount != null) {
            try {
                long amountLong = Long.parseLong(amount);
                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));

                String formattedNumber = formatter.format(amountLong);
                String fullText = formattedNumber + "đ";

                SpannableString spannableString = new SpannableString(fullText);

                int startIndex = fullText.length() - 1;
                int endIndex = fullText.length();

                spannableString.setSpan(
                        new RelativeSizeSpan(0.5f),
                        startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannableString.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#BDBDBD")),
                        startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                tvFailedAmount.setText(spannableString);

            } catch (Exception e) {
                tvFailedAmount.setText(amount + "đ");
            }
        }
    }

    private void returnToHome() {
        Intent intent = new Intent(TransferFailedActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}