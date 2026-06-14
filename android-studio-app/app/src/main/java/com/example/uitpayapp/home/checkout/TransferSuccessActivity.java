package com.example.uitpayapp.home.checkout;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.uitpayapp.R;

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

        findViewById(R.id.btn_transaction_details).setOnClickListener(v -> {
            boolean isFoodOrderLocal = getIntent().getBooleanExtra("KEY_IS_FOOD_ORDER", false);
            if (isFoodOrderLocal) {
                Intent detailsIntent = new Intent(this, com.example.uitpayapp.history.OrderDetailActivity.class);
                detailsIntent.putExtra("ORDER_STATUS", "DELIVERING");
                detailsIntent.putExtra("KEY_AMOUNT", getIntent().getStringExtra("KEY_AMOUNT"));
                detailsIntent.putExtra("KEY_DELIVERY_FEE", getIntent().getLongExtra("KEY_DELIVERY_FEE", 15000));
                detailsIntent.putExtra("KEY_DISCOUNT", getIntent().getLongExtra("KEY_DISCOUNT", 0));
                startActivity(detailsIntent);
            } else {
                Toast.makeText(this, "Tính năng xem chi tiết đang phát triển", Toast.LENGTH_SHORT).show();
            }
        });

        int avatarId = getIntent().getIntExtra("KEY_AVATAR", R.drawable.img_usagi);
        String name = getIntent().getStringExtra("KEY_NAME");
        String amount = getIntent().getStringExtra("KEY_AMOUNT");
        String type = getIntent().getStringExtra("KEY_TYPE");

        boolean isRecharge = getIntent().getBooleanExtra("KEY_IS_RECHARGE", false);
        boolean isBuyCard = getIntent().getBooleanExtra("KEY_IS_BUY_CARD", false);
        boolean isData = getIntent().getBooleanExtra("KEY_IS_DATA", false);
        boolean isFoodOrder = getIntent().getBooleanExtra("KEY_IS_FOOD_ORDER", false);
        String foodProducts = getIntent().getStringExtra("KEY_FOOD_PRODUCTS");

        ImageView ivSuccessAvatar = findViewById(R.id.iv_recipient_avatar_success);
        TextView tvSuccessName = findViewById(R.id.tv_recipient_name_success);
        TextView tvSuccessAmount = findViewById(R.id.tv_success_amount);
        TextView tvTransactionId = findViewById(R.id.tv_transaction_id);
        TextView tvTransactionTime = findViewById(R.id.tv_transaction_time);
        TextView tvLabelRecipient = findViewById(R.id.tv_label_recipient_success);
        TextView tvTransactionNote = findViewById(R.id.tv_transaction_note);

        ImageView ivCopySeri = findViewById(R.id.iv_copy_seri);
        ImageView ivCopyPin = findViewById(R.id.iv_copy_pin);

        Random random = new Random();

        if (isBuyCard) {
            String seri = "1000" + (1000000000L + (long)(random.nextDouble() * 8999999999L));
            String pin = "456" + (10000000000L + (long)(random.nextDouble() * 89999999999L));

            if (isData) {
                if (tvLabelRecipient != null) {
                    tvLabelRecipient.setVisibility(View.GONE);
                }
                if (tvSuccessName != null) {
                    tvSuccessName.setText("Mã thẻ: " + pin);
                }
                if (ivCopySeri != null) {
                    ivCopySeri.setVisibility(View.GONE);
                }
                if (ivCopyPin != null) {
                    ivCopyPin.setVisibility(View.VISIBLE);
                    ivCopyPin.setOnClickListener(v -> copyToClipboard("Mã thẻ", pin));
                }
            } else {
                if (tvLabelRecipient != null) {
                    tvLabelRecipient.setVisibility(View.VISIBLE);
                    tvLabelRecipient.setText("Số Seri: " + seri);
                }
                if (tvSuccessName != null) {
                    tvSuccessName.setText("Mã thẻ: " + pin);
                }
                if (ivCopySeri != null) {
                    ivCopySeri.setVisibility(View.VISIBLE);
                    ivCopySeri.setOnClickListener(v -> copyToClipboard("Số Seri", seri));
                }
                if (ivCopyPin != null) {
                    ivCopyPin.setVisibility(View.VISIBLE);
                    ivCopyPin.setOnClickListener(v -> copyToClipboard("Mã thẻ", pin));
                }
            }

            if (tvSuccessName != null) {
                tvSuccessName.setTextColor(Color.parseColor("#0A46A6"));
                tvSuccessName.setTextSize(18f);
            }

            if (ivSuccessAvatar != null && ivSuccessAvatar.getParent() != null) {
                ((View) ivSuccessAvatar.getParent()).setVisibility(View.GONE);
            }

            View layoutTransactionNote = findViewById(R.id.layout_transaction_note);
            if (layoutTransactionNote != null) {
                layoutTransactionNote.setVisibility(View.GONE);
            }
        } else {
            if (ivCopySeri != null) ivCopySeri.setVisibility(View.GONE);
            if (ivCopyPin != null) ivCopyPin.setVisibility(View.GONE);

            if (tvLabelRecipient != null) {
                tvLabelRecipient.setVisibility(View.VISIBLE);
                if (isFoodOrder) {
                    tvLabelRecipient.setText("THANH TOÁN CHO");
                } else if (isRecharge) {
                    if (isData) {
                        tvLabelRecipient.setText("ĐÃ NẠP DATA CHO");
                    } else {
                        tvLabelRecipient.setText("ĐÃ NẠP ĐIỆN THOẠI CHO");
                    }
                } else if ("DEPOSIT".equals(type)) {
                    tvLabelRecipient.setText("ĐÃ NẠP TIỀN TỪ");
                } else if ("WITHDRAW".equals(type)) {
                    tvLabelRecipient.setText("ĐÃ RÚT TIỀN VỀ");
                } else {
                    tvLabelRecipient.setText("GỬI ĐẾN");
                }
            }

            if (tvSuccessName != null) {
                if (isFoodOrder) {
                    tvSuccessName.setText("UIT FOOD");
                } else if (name != null) {
                    tvSuccessName.setText(name.toUpperCase());
                }
            }
            if (ivSuccessAvatar != null) {
                if (isFoodOrder) {
                    ivSuccessAvatar.setImageResource(R.drawable.ic_food);
                    ivSuccessAvatar.setPadding(10, 10, 10, 10);
                    ivSuccessAvatar.setColorFilter(android.graphics.Color.parseColor("#F57C00"));
                } else {
                    ivSuccessAvatar.setImageResource(avatarId);
                }
            }
            View layoutRecipientInfo = findViewById(R.id.layout_recipient_info);
            if (layoutRecipientInfo != null) {
                if (isFoodOrder) {
                    layoutRecipientInfo.setVisibility(View.GONE);
                } else {
                    layoutRecipientInfo.setVisibility(View.VISIBLE);
                }
            }

            View layoutTransactionNote = findViewById(R.id.layout_transaction_note);
            TextView tvTransactionNoteLabel = findViewById(R.id.tv_transaction_note_label);

            if (layoutTransactionNote != null && tvTransactionNote != null && tvTransactionNoteLabel != null) {
                String destination = getIntent().getStringExtra("KEY_DESTINATION");
                if (isFoodOrder) {
                    layoutTransactionNote.setVisibility(View.VISIBLE);
                    tvTransactionNoteLabel.setText("Sản phẩm");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        tvTransactionNote.setText(android.text.Html.fromHtml((foodProducts != null ? foodProducts : ""), android.text.Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        tvTransactionNote.setText(android.text.Html.fromHtml((foodProducts != null ? foodProducts : "")));
                    }
                } else if ("DEPOSIT".equals(type)) {
                    if (destination != null && !destination.isEmpty()) {
                        layoutTransactionNote.setVisibility(View.VISIBLE);
                        tvTransactionNoteLabel.setText("Nạp vào");
                        tvTransactionNote.setText(destination);
                    } else {
                        layoutTransactionNote.setVisibility(View.GONE);
                    }
                } else if ("WITHDRAW".equals(type)) {
                    if (destination != null && !destination.isEmpty()) {
                        layoutTransactionNote.setVisibility(View.VISIBLE);
                        tvTransactionNoteLabel.setText("Rút từ");
                        tvTransactionNote.setText(destination);
                    } else {
                        layoutTransactionNote.setVisibility(View.GONE);
                    }
                } else {
                    layoutTransactionNote.setVisibility(View.VISIBLE);
                    tvTransactionNoteLabel.setText("Ghi chú");
                    tvTransactionNote.setText("Chuyển tiền thành công");
                }
            }
        }

        SimpleDateFormat idFormat = new SimpleDateFormat("yyMMddHHmmss", Locale.getDefault());
        String timePrefix = idFormat.format(new Date());
        int randomSuffix = random.nextInt(900) + 100;
        String codeText = "#" + timePrefix + randomSuffix;

        if (tvTransactionId != null) tvTransactionId.setText(codeText);

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        if (tvTransactionTime != null) tvTransactionTime.setText(currentTime);

        if (amount != null && tvSuccessAmount != null) {
            try {
                long amountLong = Long.parseLong(amount);
                NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
                String formattedNumber = formatter.format(amountLong);
                String fullText = formattedNumber + "đ";

                SpannableString spannableString = new SpannableString(fullText);
                int startIndex = fullText.length() - 1;
                int endIndex = fullText.length();
                spannableString.setSpan(new RelativeSizeSpan(0.5f), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#BDBDBD")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                tvSuccessAmount.setText(spannableString);
            } catch (Exception e) {
                tvSuccessAmount.setText(amount + "đ");
            }
        }
        
        setupDeliveryProgress();
    }

    private void setupDeliveryProgress() {
        View layoutDeliveryProgress = findViewById(R.id.layout_delivery_progress);
        if (layoutDeliveryProgress != null) {
            boolean isFoodOrder = getIntent().getBooleanExtra("KEY_IS_FOOD_ORDER", false);
            if (isFoodOrder) {
                layoutDeliveryProgress.setVisibility(View.VISIBLE);
            } else {
                layoutDeliveryProgress.setVisibility(View.GONE);
            }
        }
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Đã sao chép " + label, Toast.LENGTH_SHORT).show();
        }
    }

    private void returnToHome() {
        Intent intent = new Intent(this, com.example.uitpayapp.home.HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}