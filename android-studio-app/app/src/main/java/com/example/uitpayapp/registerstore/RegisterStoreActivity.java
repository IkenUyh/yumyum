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
    private Double latitude = 0.0;
    private Double longitude = 0.0;

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
        findViewById(R.id.row_store_address).setOnClickListener(openSheetListener);
        findViewById(R.id.tv_register_store_edit_store_info).setOnClickListener(openSheetListener);

        View btnSubmit = findViewById(R.id.btn_register_store_submit);
        btnSubmit.setOnClickListener(v -> {
            if (storeName.equals("Chưa cập nhật") || storeAddress.equals("Chưa cập nhật")) {
                Toast.makeText(this, "Vui lòng cập nhật đầy đủ thông tin cửa hàng", Toast.LENGTH_SHORT).show();
                return;
            }

            btnSubmit.setEnabled(false);

            com.example.uitpayapp.modules.merchant.MerchantRepository merchantRepository = new com.example.uitpayapp.modules.merchant.MerchantRepository();
            com.example.uitpayapp.modules.merchant.models.requests.SubmitRequestDTO dto = 
                new com.example.uitpayapp.modules.merchant.models.requests.SubmitRequestDTO(
                    storeName,
                    storeAddress,
                    storePhone,
                    "LIC" + System.currentTimeMillis(),
                    latitude,
                    longitude
                );

            merchantRepository.submitRequest(dto, null, new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.merchant.models.responses.MerchantRequestResponseDTO>() {
                @Override
                public void onSuccess(com.example.uitpayapp.modules.merchant.models.responses.MerchantRequestResponseDTO response) {
                    Toast.makeText(RegisterStoreActivity.this, "Yêu cầu của bạn đã được gửi và đang chờ duyệt!", Toast.LENGTH_LONG).show();
                    finish();
                }

                @Override
                public void onError(String error) {
                    btnSubmit.setEnabled(true);
                    Toast.makeText(RegisterStoreActivity.this, "Lỗi đăng ký cửa hàng: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void setupInitialData() {
        com.example.uitpayapp.network.SessionManager session = com.example.uitpayapp.network.SessionManager.getInstance(this);
        String name = session.getUserName();
        String phone = session.getUserPhone();

        setRowData(R.id.row_owner_name, "Họ và tên chủ quán", name != null && !name.isEmpty() ? name : "Chưa cập nhật");
        setRowData(R.id.row_owner_phone, "Số điện thoại chủ", phone != null && !phone.isEmpty() ? phone : "Chưa cập nhật");
        setRowData(R.id.row_owner_address, "Email chủ quán", session.getUserEmail() != null && !session.getUserEmail().isEmpty() ? session.getUserEmail() : "Chưa cập nhật");

        if (phone != null && !phone.isEmpty()) {
            storePhone = phone;
        }
        updateStoreRowsUI();
    }

    private void updateStoreRowsUI() {
        setRowData(R.id.row_store_name, "Tên cửa hàng", storeName);
        setRowData(R.id.row_store_address, "Địa chỉ quán", storeAddress);
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
        TextView tvAddress = view.findViewById(R.id.tv_selected_store_address);

        if (!storeName.equals("Chưa cập nhật")) etName.setText(storeName);
        if (!storeAddress.equals("Chưa cập nhật") && !storeAddress.equals("Chọn địa chỉ từ bản đồ")) {
            tvAddress.setText(storeAddress);
            tvAddress.setTextColor(Color.BLACK);
        }
        tvBottomSheetAddressRef = tvAddress;
        view.findViewById(R.id.layout_select_store_address).setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickerActivity.class);
            startActivityForResult(intent, 100);
        });
        view.findViewById(R.id.btn_save_store_info).setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String address = tvAddress.getText().toString();

            if (name.isEmpty() || address.equals("Chọn địa chỉ từ bản đồ") || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên và chọn địa chỉ quán", Toast.LENGTH_SHORT).show();
                return;
            }

            storeName = name;
            storeAddress = address;

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
            double lat = data.getDoubleExtra("LATITUDE_SELECTED", 0.0);
            double lon = data.getDoubleExtra("LONGITUDE_SELECTED", 0.0);
            if (address != null && tvBottomSheetAddressRef != null) {
                tvBottomSheetAddressRef.setText(address);
                tvBottomSheetAddressRef.setTextColor(Color.BLACK);
                storeAddress = address;
                latitude = lat;
                longitude = lon;
            }
        }
    }
}
