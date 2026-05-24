package com.example.uitpayapp.home.checkout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.NumberFormat;
import java.util.Locale;

public class TransferConfirmationActivity extends AppCompatActivity {
    private CardView cardWallet, cardSaving;
    private int receivedAvatarId;
    private ImageView ivWallet, ivSaving;
    private TextView tvWalletTitle, tvSavingTitle;
    private Button btnConfirm;

    private String currentPin = "";
    private View[] pinDots = new View[6];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Trả lại đúng giao diện của màn Xác nhận
        setContentView(R.layout.activity_transfer_confirmation);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        TextView tvConfirmName = findViewById(R.id.tv_confirm_recipient_name);
        TextView tvConfirmAmount = findViewById(R.id.tv_confirm_amount);
        ImageView ivHeaderIcon = findViewById(R.id.iv_header_icon);
        findViewById(R.id.btn_close_confirm).setOnClickListener(v -> finish());

        String amount = getIntent().getStringExtra("KEY_AMOUNT");
        String name = getIntent().getStringExtra("KEY_NAME");
        receivedAvatarId = getIntent().getIntExtra("KEY_AVATAR", R.drawable.img_usagi);

        // Lấy cờ để nhận diện luồng
        boolean isRecharge = getIntent().getBooleanExtra("KEY_IS_RECHARGE", false);
        boolean isBuyCard = getIntent().getBooleanExtra("KEY_IS_BUY_CARD", false);
        boolean isData = getIntent().getBooleanExtra("KEY_IS_DATA", false);
        boolean isFoodOrder = getIntent().getBooleanExtra("KEY_IS_FOOD_ORDER", false);

        if (name != null || isFoodOrder) {
            if (isFoodOrder) {
                tvConfirmName.setText("Thanh toán dịch vụ đặt đồ ăn");
                if (ivHeaderIcon != null) {
                    ivHeaderIcon.setImageResource(R.drawable.ic_home_24px); // or some food icon
                }
            } else if (isRecharge) {
                if (isData) {
                    tvConfirmName.setText("Nạp data ");
                } else {
                    tvConfirmName.setText("Nạp điện thoại ");
                }

                if (ivHeaderIcon != null) {
                    ivHeaderIcon.setImageResource(R.drawable.ic_phone);
                }
            } else if (isBuyCard) {
                tvConfirmName.setText("Thanh toán dịch vụ");
                if (ivHeaderIcon != null) {
                    ivHeaderIcon.setImageResource(R.drawable.ic_phone);
                }
            } else {
                tvConfirmName.setText("Chuyển tiền đến " + name);
                if (ivHeaderIcon != null) {
                    ivHeaderIcon.setImageResource(R.drawable.ic_transfer);
                }
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
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannableString.setSpan(
                        new ForegroundColorSpan(Color.parseColor("#BDBDBD")),
                        startIndex,
                        endIndex,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );

                tvConfirmAmount.setText(spannableString);

            } catch (Exception e) {
                tvConfirmAmount.setText(amount + "đ");
            }
        }
        cardWallet = findViewById(R.id.card_source_wallet);
        cardSaving = findViewById(R.id.card_source_saving);
        ivWallet = findViewById(R.id.iv_source_wallet);
        ivSaving = findViewById(R.id.iv_source_saving);
        tvWalletTitle = findViewById(R.id.tv_source_wallet_title);
        tvSavingTitle = findViewById(R.id.tv_source_saving_title);

        btnConfirm = findViewById(R.id.btn_confirm_transfer);

        cardWallet.setOnClickListener(v -> selectSource(true));
        cardSaving.setOnClickListener(v -> selectSource(false));

        selectSource(true);
        setButtonState(true);

        btnConfirm.setOnClickListener(v -> showPasscodeBottomSheet());
    }

    private void selectSource(boolean isWallet) {
        int colorBlue = android.graphics.Color.parseColor("#0A46A6");
        int colorGray = android.graphics.Color.parseColor("#757575");
        int bgBlue = android.graphics.Color.parseColor("#E8F0FE");
        int bgGray = android.graphics.Color.parseColor("#F5F5F5");

        if (isWallet) {
            cardWallet.setCardBackgroundColor(bgBlue);
            ivWallet.setColorFilter(colorBlue);
            tvWalletTitle.setTextColor(colorBlue);

            cardSaving.setCardBackgroundColor(bgGray);
            ivSaving.setColorFilter(colorGray);
            tvSavingTitle.setTextColor(colorGray);
        } else {
            cardSaving.setCardBackgroundColor(bgBlue);
            ivSaving.setColorFilter(colorBlue);
            tvSavingTitle.setTextColor(colorBlue);

            cardWallet.setCardBackgroundColor(bgGray);
            ivWallet.setColorFilter(colorGray);
            tvWalletTitle.setTextColor(colorGray);
        }
    }

    private void setButtonState(boolean isEnabled) {
        if (isEnabled) {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#0bc36a")
            ));
        } else {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#BDBDBD")
            ));
        }
    }

    private void showPasscodeBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View sheetView = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_passcode, null);
        bottomSheetDialog.setContentView(sheetView);

        pinDots[0] = sheetView.findViewById(R.id.dot_1);
        pinDots[1] = sheetView.findViewById(R.id.dot_2);
        pinDots[2] = sheetView.findViewById(R.id.dot_3);
        pinDots[3] = sheetView.findViewById(R.id.dot_4);
        pinDots[4] = sheetView.findViewById(R.id.dot_5);
        pinDots[5] = sheetView.findViewById(R.id.dot_6);

        sheetView.findViewById(R.id.btn_close_passcode).setOnClickListener(v -> bottomSheetDialog.dismiss());

        int[] numberButtonIds = {
                R.id.btn_pin_0, R.id.btn_pin_1, R.id.btn_pin_2, R.id.btn_pin_3, R.id.btn_pin_4,
                R.id.btn_pin_5, R.id.btn_pin_6, R.id.btn_pin_7, R.id.btn_pin_8, R.id.btn_pin_9
        };

        for (int id : numberButtonIds) {
            sheetView.findViewById(id).setOnClickListener(v -> {
                if (currentPin.length() < 6) {
                    String pressedNumber = v.getTag().toString();
                    currentPin += pressedNumber;

                    updatePinDots();

                    if (currentPin.length() == 6) {
                        new Handler().postDelayed(() -> {
                            bottomSheetDialog.dismiss();
                            navigateToSuccessScreen();
                        }, 300);
                    }
                }
            });
        }

        sheetView.findViewById(R.id.btn_pin_delete).setOnClickListener(v -> {
            if (currentPin.length() > 0) {
                currentPin = currentPin.substring(0, currentPin.length() - 1);
                updatePinDots();
            }
        });

        currentPin = "";
        updatePinDots();
        bottomSheetDialog.show();
    }

    private void updatePinDots() {
        int colorBlue = android.graphics.Color.parseColor("#0A46A6");
        int colorGray = android.graphics.Color.parseColor("#E0E0E0");

        for (int i = 0; i < 6; i++) {
            if (i < currentPin.length()) {
                pinDots[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorBlue));
            } else {
                pinDots[i].setBackgroundTintList(android.content.res.ColorStateList.valueOf(colorGray));
            }
        }
    }

    private void navigateToSuccessScreen() {
        Intent intent = new Intent(TransferConfirmationActivity.this, TransferSuccessActivity.class);

        boolean isRecharge = getIntent().getBooleanExtra("KEY_IS_RECHARGE", false);
        boolean isBuyCard = getIntent().getBooleanExtra("KEY_IS_BUY_CARD", false);
        boolean isData = getIntent().getBooleanExtra("KEY_IS_DATA", false);
        boolean isFoodOrder = getIntent().getBooleanExtra("KEY_IS_FOOD_ORDER", false);

        intent.putExtra("KEY_AMOUNT", getIntent().getStringExtra("KEY_AMOUNT"));
        intent.putExtra("KEY_NAME", getIntent().getStringExtra("KEY_NAME"));
        intent.putExtra("KEY_AVATAR", receivedAvatarId);
        intent.putExtra("KEY_IS_RECHARGE", isRecharge);
        intent.putExtra("KEY_IS_BUY_CARD", isBuyCard);
        intent.putExtra("KEY_IS_DATA", isData); 
        intent.putExtra("KEY_IS_FOOD_ORDER", isFoodOrder);
        intent.putExtra("KEY_FOOD_PRODUCTS", getIntent().getStringExtra("KEY_FOOD_PRODUCTS"));

        startActivity(intent);
        finish();
    }
}