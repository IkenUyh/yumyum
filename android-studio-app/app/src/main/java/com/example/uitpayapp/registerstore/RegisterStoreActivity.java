package com.example.uitpayapp.registerstore;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.uitpayapp.R;
import com.example.uitpayapp.deliveryaddressorder.MapPickerActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class RegisterStoreActivity extends AppCompatActivity {

    private String storeName = "Chưa cập nhật";
    private String storePhone = "Chưa cập nhật";
    private String storeAddress = "Chưa cập nhật";
    private String storeType = "Chưa cập nhật";

    private TextView tvBottomSheetAddressRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_store);

        initView();
        setupInitialData();
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_register_store);
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        ((TextView) topBar.findViewById(R.id.top_bar_title)).setText("Đăng ký đối tác bán hàng");
        View mainContainer = findViewById(R.id.register_store_container);

        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBar=insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int safeTopPadding = Math.max(cutout.top,systemBar.top) + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom;
            if (mainContainer != null) {
                mainContainer.setPadding(mainContainer.getPaddingLeft(), mainContainer.getPaddingTop(), mainContainer.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });

        View.OnClickListener openSheetListener = v -> showRegisterStoreInfoBottomSheet();
        findViewById(R.id.row_store_name).setOnClickListener(openSheetListener);
        findViewById(R.id.row_store_phone).setOnClickListener(openSheetListener);
        findViewById(R.id.row_store_address).setOnClickListener(openSheetListener);
        findViewById(R.id.row_store_type).setOnClickListener(openSheetListener);
        findViewById(R.id.tv_register_store_edit_store_info).setOnClickListener(openSheetListener);

        findViewById(R.id.btn_register_store_submit).setOnClickListener(v -> {
            if (storeName.equals("Chưa cập nhật") || storeAddress.equals("Chưa cập nhật")) {
                Toast.makeText(this, "Vui lòng cập nhật đầy đủ thông tin cửa hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Yêu cầu của bạn đã được gửi và đang chờ duyệt!", Toast.LENGTH_LONG).show();
            finish();
        });
    }

    private void setupInitialData() {
        setRowData(R.id.row_owner_name, "Họ và tên chủ quán", "Nguyễn Văn A");
        setRowData(R.id.row_owner_id, "Số CCCD/MST", "0123456789");
        setRowData(R.id.row_owner_permanent_address, "Địa chỉ thường trú", "Thủ Đức, TP.HCM");
        updateStoreRowsUI();
    }

    private void updateStoreRowsUI() {
        setRowData(R.id.row_store_name, "Tên cửa hàng", storeName);
        setRowData(R.id.row_store_phone, "Số điện thoại quán", storePhone);
        setRowData(R.id.row_store_address, "Địa chỉ quán", storeAddress);
        setRowData(R.id.row_store_type, "Loại hình kinh doanh", storeType);
    }

    private void setRowData(int viewId, String label, String value) {
        View row = findViewById(viewId);
        if (row != null) {
            ((TextView) row.findViewById(R.id.tv_label)).setText(label);
            TextView tvValue = row.findViewById(R.id.tv_value);
            tvValue.setText(value);
        }
    }

    private void showRegisterStoreInfoBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_register_store_info, null);
        bottomSheetDialog.setContentView(view);
        EditText etName = view.findViewById(R.id.et_store_name);
        EditText etPhone = view.findViewById(R.id.et_store_phone);
        TextView tvAddress = view.findViewById(R.id.tv_selected_store_address);
        TextView tvType = view.findViewById(R.id.tv_selected_store_type);

        if (!storeName.equals("Chưa cập nhật")) etName.setText(storeName);
        if (!storePhone.equals("Chưa cập nhật")) etPhone.setText(storePhone);
        if (!storeAddress.equals("Chưa cập nhật")) {
            tvAddress.setText(storeAddress);
            tvAddress.setTextColor(Color.BLACK);
        }
        if (!storeType.equals("Chưa cập nhật")) {
            tvType.setText(storeType);
            tvType.setTextColor(Color.BLACK);
        }
        tvBottomSheetAddressRef = tvAddress;
        view.findViewById(R.id.layout_select_store_address).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickerActivity.class);
            startActivityForResult(intent, 100);
        });
        view.findViewById(R.id.layout_select_store_type).setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            String[] types = {"Đồ ăn", "Thức uống", "Tiệm bánh", "Siêu thị/Tạp hóa", "Hoa & Quà tặng"};
            for (String type : types) popupMenu.getMenu().add(type);
            popupMenu.setOnMenuItemClickListener(item -> {
                tvType.setText(item.getTitle());
                tvType.setTextColor(Color.BLACK);
                return true;
            });
            popupMenu.show();
        });
        view.findViewById(R.id.btn_save_store_info).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = tvAddress.getText().toString();
            String type = tvType.getText().toString();

            if (name.isEmpty() || address.equals("Chọn địa chỉ từ bản đồ")) {
                Toast.makeText(this, "Vui lòng nhập tên và chọn địa chỉ quán", Toast.LENGTH_SHORT).show();
                return;
            }

            storeName = name;
            storePhone = phone.isEmpty() ? "Chưa cập nhật" : phone;
            storeAddress = address;
            storeType = type.equals("Chọn loại hình quán") ? "Chưa cập nhật" : type;

            updateStoreRowsUI();
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btn_close_store_info_sheet).setOnClickListener(v -> bottomSheetDialog.dismiss());
        
        bottomSheetDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("ADDRESS_SELECTED");
            if (address != null && tvBottomSheetAddressRef != null) {
                tvBottomSheetAddressRef.setText(address);
                tvBottomSheetAddressRef.setTextColor(Color.BLACK);
            }
        }
    }
}
