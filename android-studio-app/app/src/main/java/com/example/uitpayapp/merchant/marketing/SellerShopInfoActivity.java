package com.example.uitpayapp.merchant.marketing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;
import com.example.uitpayapp.profile.ProfileActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SellerShopInfoActivity extends AppCompatActivity {

    private ImageView ivShopAvatar;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.yumyum_demo_logo)
                            .circleCrop()
                            .into(ivShopAvatar);

                    Toast.makeText(this, "Đã cập nhật ảnh đại diện", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_shop_info);

        initViews();
        loadShopData();
    }


    private void initViews() {
        ivShopAvatar = findViewById(R.id.iv_shop_avatar);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        findViewById(R.id.btn_edit_avatar).setOnClickListener(v ->
                pickMedia.launch(new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build())
        );

        findViewById(R.id.update_secondary_info).setOnClickListener(v -> handleUpdateShopInfo());
        
        View mainContainer = findViewById(R.id.seller_shop_info_container);
        if (mainContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainContainer, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, v.getPaddingTop(), systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }


    private void loadShopData() {
        setRowData(R.id.row_shop_name, "Tên cửa hàng", "Cửa hàng A - Cơm văn phòng");
        setRowData(R.id.row_shop_type, "Loại hình", "Cơm văn phòng");
        setRowData(R.id.row_address, "Địa chỉ", "Điện Biên, Xã Nánh Cầu");
        setRowData(R.id.row_opening_hours, "Giờ mở cửa", "07:00 - 22:00");
        setRowData(R.id.row_phone, "Số điện thoại", "0356213228");
        setRowData(R.id.row_email, "Email", "cuahang@example.com");
        setRowData(R.id.row_description, "Mô tả cửa hàng", "Chuyên các món cơm văn phòng, phục vụ nhanh...");

        TextView tvHeader = findViewById(R.id.tv_shop_name_header);
        if (tvHeader != null) tvHeader.setText("Cửa hàng A - Cơm văn phòng");
    }


    private void setRowData(int viewId, String label, String value) {
        View row = findViewById(viewId);

        if (row != null) {
            TextView tvLabel = row.findViewById(R.id.tv_label);
            TextView tvValue = row.findViewById(R.id.tv_value);

            if (tvLabel != null) tvLabel.setText(label);
            if (tvValue != null) tvValue.setText(value);
        }
    }


    private String getRowValue(int viewId) {
        View row = findViewById(viewId);

        if (row != null) {
            TextView tvValue = row.findViewById(R.id.tv_value);
            if (tvValue != null) return tvValue.getText().toString();
        }

        return "";
    }


    private void handleUpdateShopInfo() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_update_shop_info, null);
        dialog.setContentView(view);

        EditText etName = view.findViewById(R.id.et_shop_name);
        EditText etAddress = view.findViewById(R.id.et_shop_address);
        EditText etPhone = view.findViewById(R.id.et_shop_phone);
        EditText etEmail = view.findViewById(R.id.et_shop_email);
        EditText etDescription = view.findViewById(R.id.et_shop_description);

        if (etName != null) etName.setText(getRowValue(R.id.row_shop_name));
        if (etAddress != null) etAddress.setText(getRowValue(R.id.row_address));
        if (etPhone != null) etPhone.setText(getRowValue(R.id.row_phone));
        if (etEmail != null) etEmail.setText(getRowValue(R.id.row_email));
        if (etDescription != null) etDescription.setText(getRowValue(R.id.row_description));


        View btnClose = view.findViewById(R.id.btn_close_update_shop_info);
        if (btnClose != null) btnClose.setOnClickListener(v -> dialog.dismiss());


        View btnSave = view.findViewById(R.id.btn_save_shop_update);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> {
                setRowData(R.id.row_shop_name, "Tên cửa hàng", etName.getText().toString());
                setRowData(R.id.row_address, "Địa chỉ", etAddress.getText().toString());
                setRowData(R.id.row_phone, "Số điện thoại", etPhone.getText().toString());
                setRowData(R.id.row_email, "Email", etEmail.getText().toString());
                setRowData(R.id.row_description, "Mô tả cửa hàng", etDescription.getText().toString());

                TextView tvHeader = findViewById(R.id.tv_shop_name_header);
                if (tvHeader != null) tvHeader.setText(etName.getText().toString());

                Toast.makeText(this, "Đã cập nhật thông tin cửa hàng", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            });
        }

        dialog.show();
    }
}
