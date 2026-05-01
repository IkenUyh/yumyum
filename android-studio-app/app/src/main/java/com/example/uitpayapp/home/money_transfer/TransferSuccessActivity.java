package com.example.uitpayapp.home.money_transfer;

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
import java.util.Random;

public class TransferSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_success);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        findViewById(R.id.btn_close_action).setOnClickListener(v -> returnToHome());

        int avatarId = getIntent().getIntExtra("KEY_AVATAR", R.drawable.img_usagi);
        String name = getIntent().getStringExtra("KEY_NAME");
        String amount = getIntent().getStringExtra("KEY_AMOUNT");
        String type = getIntent().getStringExtra("KEY_TYPE"); // LẤY TÍN HIỆU LOẠI GIAO DỊCH

        ImageView ivSuccessAvatar = findViewById(R.id.iv_recipient_avatar_success);
        TextView tvSuccessName = findViewById(R.id.tv_recipient_name_success);
        TextView tvSuccessAmount = findViewById(R.id.tv_success_amount);
        TextView tvTransactionId = findViewById(R.id.tv_transaction_id);
        TextView tvTransactionTime = findViewById(R.id.tv_transaction_time);

        TextView tvLabelRecipient = findViewById(R.id.tv_label_recipient_success);

        if (tvLabelRecipient != null) {
            if ("DEPOSIT".equals(type)) {
                tvLabelRecipient.setText("ĐÃ NẠP TIỀN TỪ");
            } else if ("WITHDRAW".equals(type)) {
                tvLabelRecipient.setText("ĐÃ RÚT TIỀN VỀ");
            } else {
                tvLabelRecipient.setText("GỬI ĐẾN");
            }
        }

        SimpleDateFormat idFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        String timePrefix = idFormat.format(new Date());

        Random random = new Random();
        int randomSuffix = random.nextInt(900) + 100;

        String prefixText = "Mã giao dịch: ";
        String codeText = "#" + timePrefix + randomSuffix;
        String fullTransactionText = prefixText + codeText;

        SpannableString spannableTransactionId = new SpannableString(fullTransactionText);
        spannableTransactionId.setSpan(
                new ForegroundColorSpan(Color.parseColor("#0A46A6")),
                prefixText.length(),
                fullTransactionText.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvTransactionId.setText(spannableTransactionId);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        tvTransactionTime.setText("Thời gian: " + currentTime);

        ivSuccessAvatar.setImageResource(avatarId);
        if (name != null) {
            tvSuccessName.setText(name.toUpperCase());
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

                tvSuccessAmount.setText(spannableString);

            } catch (Exception e) {
                tvSuccessAmount.setText(amount + "đ");
            }
        }
    }

    private void returnToHome() {
        Intent intent = new Intent(TransferSuccessActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}