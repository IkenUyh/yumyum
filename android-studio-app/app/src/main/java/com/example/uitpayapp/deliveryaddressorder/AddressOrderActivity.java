package com.example.uitpayapp.deliveryaddressorder;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.user.AddressRepository;
import com.example.uitpayapp.modules.user.models.requests.CreateAddressDTO;
import com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import androidx.activity.EdgeToEdge;

public class AddressOrderActivity extends AppCompatActivity {

    private RecyclerView rvPaymentMethods;
    private DeliveryAddressAdapter adapter;
    private List<AddressResponseDTO> deliveryAddresses = new ArrayList<>();
    private TextView tvSelectedAddress;
    private AddressRepository addressRepository;
    private BigDecimal selectedLatitude = BigDecimal.ZERO;
    private BigDecimal selectedLongitude = BigDecimal.ZERO;
    
    private View layoutEmptyState;
    private TextView tvEmptyMessage;
    private View btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address_order);
        
        addressRepository = new AddressRepository();
        
        initView();
        setupRecyclerView();
        loadAddresses();
        
        findViewById(R.id.btn_add_address_header).setOnClickListener(v -> showAddressBottomSheet(null, -1));
        findViewById(R.id.btnSave).setOnClickListener(v -> finish());
    }

    private void loadAddresses() {
        rvPaymentMethods.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.GONE);
        
        addressRepository.getMyAddresses(new ApiCallback<List<AddressResponseDTO>>() {
            @Override
            public void onSuccess(List<AddressResponseDTO> result) {
                deliveryAddresses = result;
                adapter.updateData(deliveryAddresses);
                
                if (deliveryAddresses == null || deliveryAddresses.isEmpty()) {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    tvEmptyMessage.setText("Hiện chưa có vị trí");
                    btnRetry.setVisibility(View.GONE);
                    rvPaymentMethods.setVisibility(View.GONE);
                } else {
                    layoutEmptyState.setVisibility(View.GONE);
                    rvPaymentMethods.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String errorMessage) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                tvEmptyMessage.setText("Opps! Đã có lỗi xảy ra"); // Similar to home screen
                btnRetry.setVisibility(View.VISIBLE);
                rvPaymentMethods.setVisibility(View.GONE);
            }
        });
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_payment_order);
        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        tvTitle.setText("Vị trí");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        tvEmptyMessage = findViewById(R.id.tv_empty_message);
        btnRetry = findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(v -> loadAddresses());
        
        View bottomPanel = findViewById(R.id.bottom_panel);
        
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int topInset = Math.max(cutout.top, systemBars.top);
            int dpToPx = (int) (15 * getResources().getDisplayMetrics().density);
            v.setPadding(v.getPaddingLeft(), topInset + dpToPx, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        if (bottomPanel != null) {
            ViewCompat.setOnApplyWindowInsetsListener(bottomPanel, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                int bottomDpToPx = (int) (15 * getResources().getDisplayMetrics().density);
                v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), systemBars.bottom + bottomDpToPx);
                return insets;
            });
        }
    }

    private void setupRecyclerView() {
        rvPaymentMethods = findViewById(R.id.rvPaymentMethods);
        rvPaymentMethods.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryAddressAdapter(deliveryAddresses, new DeliveryAddressAdapter.OnAddressActionListener() {
            @Override
            public void onEditClick(AddressResponseDTO address, int position) {
                showAddressBottomSheet(address, position);
            }
        });
        rvPaymentMethods.setAdapter(adapter);
    }

    private void showAddressBottomSheet(AddressResponseDTO address, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_edit_address, null);
        bottomSheetDialog.setContentView(view);

        TextView tvTitle = view.findViewById(R.id.tv_bottom_sheet_title);
        EditText etName = view.findViewById(R.id.et_recipient_name);
        EditText etPhone = view.findViewById(R.id.et_phone_number);
        TextView btnTypeHome = view.findViewById(R.id.btn_type_home);
        TextView btnTypeWork = view.findViewById(R.id.btn_type_work);
        View btnSave = view.findViewById(R.id.btn_save_address);
        View btnClose = view.findViewById(R.id.btn_close_address_sheet);
        TextView btnDelete = view.findViewById(R.id.btn_delete_address);
        android.widget.CheckBox cbSetDefault = view.findViewById(R.id.cb_set_default);
        tvSelectedAddress = view.findViewById(R.id.tv_selected_address);

        final String[] selectedType = {"HOME"};

        if (address != null) {
            tvTitle.setText("Chỉnh sửa địa chỉ");
            etName.setText(address.getRecipientName());
            etPhone.setText(address.getPhoneNumber());
            tvSelectedAddress.setText(address.getDetailedAddress());
            selectedType[0] = address.getAddressName() != null ? address.getAddressName() : "HOME";
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
            
            if (address.getIsDefault() != null) {
                cbSetDefault.setChecked(address.getIsDefault());
            }
            
            selectedLatitude = address.getLatitude() != null ? address.getLatitude() : BigDecimal.ZERO;
            selectedLongitude = address.getLongitude() != null ? address.getLongitude() : BigDecimal.ZERO;
            
            btnDelete.setVisibility(View.VISIBLE);
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
        } else {
            tvTitle.setText("Thêm địa chỉ mới");
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
            btnDelete.setVisibility(View.GONE);
            btnSave.setEnabled(true); // Should enable based on validation ideally
            btnSave.setAlpha(1.0f);
            
            selectedLatitude = BigDecimal.ZERO;
            selectedLongitude = BigDecimal.ZERO;
        }

        btnTypeHome.setOnClickListener(v -> {
            selectedType[0] = "HOME";
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
        });

        btnTypeWork.setOnClickListener(v -> {
            selectedType[0] = "WORK";
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
        });

        btnDelete.setOnClickListener(v -> {
            if (address != null) {
                addressRepository.deleteAddress(address.getId(), new ApiCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Toast.makeText(AddressOrderActivity.this, "Đã xóa địa chỉ", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        loadAddresses();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AddressOrderActivity.this, "Lỗi xóa địa chỉ: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnSave.setOnClickListener(v -> {
            String addressName = selectedType[0];
            String recipientName = etName.getText().toString();
            String phoneNumber = etPhone.getText().toString();
            String detailedAddress = tvSelectedAddress.getText().toString();
            BigDecimal latitude = selectedLatitude;
            BigDecimal longitude = selectedLongitude;
            Boolean isDefault = cbSetDefault.isChecked();
            
            CreateAddressDTO dto = new CreateAddressDTO(addressName, recipientName, phoneNumber, detailedAddress, latitude, longitude, isDefault);

            if (address != null) {
                // Update Address
                addressRepository.updateAddress(address.getId(), dto, new ApiCallback<AddressResponseDTO>() {
                    @Override
                    public void onSuccess(AddressResponseDTO result) {
                        handleSetDefault(address.getId(), cbSetDefault.isChecked(), bottomSheetDialog);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AddressOrderActivity.this, "Lỗi sửa địa chỉ: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Create Address
                addressRepository.createAddress(dto, new ApiCallback<AddressResponseDTO>() {
                    @Override
                    public void onSuccess(AddressResponseDTO result) {
                        handleSetDefault(result.getId(), cbSetDefault.isChecked(), bottomSheetDialog);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(AddressOrderActivity.this, "Lỗi thêm địa chỉ: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        tvSelectedAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickerActivity.class);
            intent.putExtra("ADDRESS_PUT",tvSelectedAddress.getText().toString());
            startActivityForResult(intent, 1);
        });

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void handleSetDefault(Long addressId, boolean isChecked, BottomSheetDialog dialog) {
        if (isChecked) {
            addressRepository.setDefaultAddress(addressId, new ApiCallback<AddressResponseDTO>() {
                @Override
                public void onSuccess(AddressResponseDTO result) {
                    Toast.makeText(AddressOrderActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadAddresses();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(AddressOrderActivity.this, "Lỗi đặt mặc định: " + errorMessage, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    loadAddresses();
                }
            });
        } else {
            Toast.makeText(AddressOrderActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            loadAddresses();
        }
    }

    private void updateTypeUI(TextView home, TextView work, String type) {
        if ("HOME".equalsIgnoreCase(type)) {
            home.setBackgroundResource(R.drawable.bg_tab_selected);
            home.setTextColor(Color.parseColor("#f24405"));
            work.setBackgroundResource(R.drawable.bg_tab_unselected);
            work.setTextColor(Color.WHITE);
        } else {
            work.setBackgroundResource(R.drawable.bg_tab_selected);
            work.setTextColor(Color.parseColor("#f24405"));
            home.setBackgroundResource(R.drawable.bg_tab_unselected);
            home.setTextColor(Color.WHITE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            String address = data.getStringExtra("ADDRESS_SELECTED");
            double lat = data.getDoubleExtra("LATITUDE_SELECTED", 0.0);
            double lon = data.getDoubleExtra("LONGITUDE_SELECTED", 0.0);
            selectedLatitude = BigDecimal.valueOf(lat);
            selectedLongitude = BigDecimal.valueOf(lon);
            if (address != null) {
                Toast.makeText(this, "Address: " + address, Toast.LENGTH_SHORT).show();
                tvSelectedAddress.setText(address);
            }
        }
    }
}
