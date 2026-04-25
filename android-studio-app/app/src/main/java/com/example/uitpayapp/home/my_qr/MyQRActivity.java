package com.example.uitpayapp.home.my_qr;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;

public class MyQRActivity extends AppCompatActivity {

    private String currentAmount = "0";
    private String tempAmount = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_qr);

        findViewById(R.id.btn_close).setOnClickListener(v -> finish());

        findViewById(R.id.btn_add_amount).setOnClickListener(v -> {
            if (currentAmount.equals("0")) {
                showAmountBottomSheet();
            } else {
                showManageAmountDialog();
            }
        });

        findViewById(R.id.btn_download_qr).setOnClickListener(v -> downloadQRImage());

        findViewById(R.id.btn_share_qr).setOnClickListener(v -> shareQRImage());
    }

    private void shareQRImage() {
        LinearLayout saveArea = findViewById(R.id.layout_save_area);
        TextView tvAmount = findViewById(R.id.btn_add_amount);

        if (currentAmount.equals("0")) {
            tvAmount.setVisibility(View.INVISIBLE);
        } else {
            android.graphics.drawable.Drawable editIcon = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_edit);
            if (editIcon != null) {
                android.graphics.drawable.Drawable leftInvisible = editIcon.getConstantState().newDrawable().mutate();
                android.graphics.drawable.Drawable rightInvisible = editIcon.getConstantState().newDrawable().mutate();
                leftInvisible.setAlpha(0);
                rightInvisible.setAlpha(0);
                tvAmount.setCompoundDrawablesWithIntrinsicBounds(leftInvisible, null, rightInvisible, null);
            }
        }

        saveArea.measure(View.MeasureSpec.makeMeasureSpec(saveArea.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(saveArea.getHeight(), View.MeasureSpec.EXACTLY));
        saveArea.layout(saveArea.getLeft(), saveArea.getTop(), saveArea.getRight(), saveArea.getBottom());

        Bitmap bitmap = Bitmap.createBitmap(saveArea.getWidth(), saveArea.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        saveArea.draw(canvas);

        if (currentAmount.equals("0")) {
            tvAmount.setVisibility(View.VISIBLE);
        } else {
            android.graphics.drawable.Drawable editIcon = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_edit);
            if (editIcon != null) {
                android.graphics.drawable.Drawable leftTransparent = editIcon.getConstantState().newDrawable().mutate();
                leftTransparent.setAlpha(0);
                tvAmount.setCompoundDrawablesWithIntrinsicBounds(leftTransparent, null, editIcon, null);
            }
        }

        try {
            File cachePath = new File(getCacheDir(), "images");
            cachePath.mkdirs();
            File file = new File(cachePath, "qr_share.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();

            Uri contentUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", file);

            if (contentUri != null) {
                android.content.Intent shareIntent = new android.content.Intent();
                shareIntent.setAction(android.content.Intent.ACTION_SEND);
                shareIntent.addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION);
                shareIntent.setType("image/png");
                shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, contentUri);
                startActivity(android.content.Intent.createChooser(shareIntent, "Chia sẻ mã QR qua"));
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi chia sẻ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadQRImage() {
        LinearLayout saveArea = findViewById(R.id.layout_save_area);
        TextView tvAmount = findViewById(R.id.btn_add_amount);

        if (currentAmount.equals("0")) {
            tvAmount.setVisibility(View.INVISIBLE);
        } else {
            android.graphics.drawable.Drawable editIcon = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_edit);
            if (editIcon != null) {
                android.graphics.drawable.Drawable leftInvisible = editIcon.getConstantState().newDrawable().mutate();
                android.graphics.drawable.Drawable rightInvisible = editIcon.getConstantState().newDrawable().mutate();
                leftInvisible.setAlpha(0);
                rightInvisible.setAlpha(0);

                tvAmount.setCompoundDrawablesWithIntrinsicBounds(leftInvisible, null, rightInvisible, null);
            }
        }

        saveArea.measure(View.MeasureSpec.makeMeasureSpec(saveArea.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(saveArea.getHeight(), View.MeasureSpec.EXACTLY));
        saveArea.layout(saveArea.getLeft(), saveArea.getTop(), saveArea.getRight(), saveArea.getBottom());

        Bitmap bitmap = Bitmap.createBitmap(saveArea.getWidth(), saveArea.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        saveArea.draw(canvas);

        if (currentAmount.equals("0")) {
            tvAmount.setVisibility(View.VISIBLE);
        } else {
            android.graphics.drawable.Drawable editIcon = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_edit);
            if (editIcon != null) {
                android.graphics.drawable.Drawable leftTransparent = editIcon.getConstantState().newDrawable().mutate();
                leftTransparent.setAlpha(0);
                tvAmount.setCompoundDrawablesWithIntrinsicBounds(leftTransparent, null, editIcon, null);
            }
        }

        saveBitmapToGallery(bitmap);
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        String fileName = "UITPay_QR_" + System.currentTimeMillis() + ".jpg";
        OutputStream fos;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                fos = getContentResolver().openOutputStream(imageUri);
            } else {
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
                java.io.File image = new java.io.File(imagesDir, fileName);
                fos = new java.io.FileOutputStream(image);
            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                Toast.makeText(this, "Đã lưu mã QR vào thư viện!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi lưu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showManageAmountDialog() {
        BottomSheetDialog manageDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog);
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_manage_amount, null);
        manageDialog.setContentView(view);

        View bottomSheet = manageDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        TextView tvManageAmount = view.findViewById(R.id.tv_manage_amount);
        tvManageAmount.setText(getFormattedAmountSpannable(currentAmount, true));

        int length = currentAmount.length();
        if (length <= 6) {
            tvManageAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 44f);
        } else if (length <= 9) {
            tvManageAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
        } else {
            tvManageAmount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f);
        }

        view.findViewById(R.id.btn_action_change).setOnClickListener(v -> {
            manageDialog.dismiss();
            showAmountBottomSheet();
        });

        view.findViewById(R.id.btn_action_delete).setOnClickListener(v -> {
            currentAmount = "0";
            TextView mainAddAmountLabel = findViewById(R.id.btn_add_amount);
            mainAddAmountLabel.setText("+ Thêm số tiền");
            mainAddAmountLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
            mainAddAmountLabel.setTypeface(null, android.graphics.Typeface.NORMAL);
            mainAddAmountLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            manageDialog.dismiss();
        });

        manageDialog.show();
    }

    private void showAmountBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_BottomSheetDialog);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_amount, null);
        bottomSheetDialog.setContentView(view);

        View bottomSheet = bottomSheetDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundResource(android.R.color.transparent);
        }

        TextView tvSheetAmount = view.findViewById(R.id.tv_bs_amount);
        TextView btnClose = view.findViewById(R.id.btn_close);
        Button btnApply = view.findViewById(R.id.btn_apply);

        tempAmount = currentAmount;

        updateSheetDisplay(tvSheetAmount);
        setupKeypadListeners(view, tvSheetAmount);

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        btnApply.setOnClickListener(v -> {
            currentAmount = tempAmount;

            TextView mainAddAmountLabel = findViewById(R.id.btn_add_amount);
            if (!currentAmount.equals("0")) {
                mainAddAmountLabel.setText(getFormattedAmountSpannable(currentAmount, false));
                mainAddAmountLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24f);
                mainAddAmountLabel.setTypeface(null, android.graphics.Typeface.BOLD);
                mainAddAmountLabel.setTextColor(Color.parseColor("#0A46A6"));

                android.graphics.drawable.Drawable editIcon = androidx.core.content.ContextCompat.getDrawable(this, R.drawable.ic_edit);
                if (editIcon != null) {
                    android.graphics.drawable.Drawable transparentIcon = editIcon.getConstantState().newDrawable().mutate();
                    transparentIcon.setAlpha(0);
                    mainAddAmountLabel.setCompoundDrawablesWithIntrinsicBounds(transparentIcon, null, editIcon, null);
                    mainAddAmountLabel.setCompoundDrawablePadding(12);
                }
            } else {
                mainAddAmountLabel.setText("+ Thêm số tiền");
                mainAddAmountLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f);
                mainAddAmountLabel.setTypeface(null, android.graphics.Typeface.NORMAL);
                mainAddAmountLabel.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void setupKeypadListeners(View view, TextView tvDisplay) {
        int[] numberIds = {
                R.id.btn_0, R.id.btn_1, R.id.btn_2, R.id.btn_3, R.id.btn_4,
                R.id.btn_5, R.id.btn_6, R.id.btn_7, R.id.btn_8, R.id.btn_9, R.id.btn_000
        };
        for (int id : numberIds) {
            view.findViewById(id).setOnClickListener(v -> handleNumberPress(((TextView) v).getText().toString(), tvDisplay));
        }
        view.findViewById(R.id.btn_delete).setOnClickListener(v -> handleDeletePress(tvDisplay));
    }

    private void handleNumberPress(String number, TextView tvDisplay) {
        if (tempAmount.equals("0")) {
            if (number.equals("000")) return;
            tempAmount = number;
        } else {
            int maxLength = 12;
            int spaceLeft = maxLength - tempAmount.length();

            if (spaceLeft <= 0) return;

            if (number.length() > spaceLeft) {
                number = number.substring(0, spaceLeft);
            }
            tempAmount += number;
        }

        updateSheetDisplay(tvDisplay);
    }

    private void handleDeletePress(TextView tvDisplay) {
        if (tempAmount.length() > 1) {
            tempAmount = tempAmount.substring(0, tempAmount.length() - 1);
        } else {
            tempAmount = "0";
        }
        updateSheetDisplay(tvDisplay);
    }

    private void updateSheetDisplay(TextView tvDisplay) {
        tvDisplay.setText(getFormattedAmountSpannable(tempAmount, true));
        int length = tempAmount.length();
        if (length <= 6) tvDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 48f);
        else if (length <= 9) tvDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36f);
        else tvDisplay.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f);
    }

    private SpannableString getFormattedAmountSpannable(String amountStr, boolean isGrayCurrency) {
        String displayString = amountStr;
        try {
            long amount = Long.parseLong(amountStr);
            displayString = NumberFormat.getInstance(new Locale("vi", "VN")).format(amount);
        } catch (NumberFormatException ignored) {}

        String finalString = displayString + "đ";
        SpannableString spannable = new SpannableString(finalString);
        int startIndex = displayString.length();
        int endIndex = finalString.length();

        if (isGrayCurrency) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#BDBDBD")), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        spannable.setSpan(new RelativeSizeSpan(0.5f), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }
}