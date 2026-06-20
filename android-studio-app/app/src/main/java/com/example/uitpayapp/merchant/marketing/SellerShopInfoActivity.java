package com.example.uitpayapp.merchant.marketing;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.restaurant.RestaurantRepository;
import com.example.uitpayapp.modules.restaurant.models.RestaurantResponseDTO;
import com.example.uitpayapp.modules.restaurant.models.UpdateRestaurantInfoDTO;
import com.example.uitpayapp.network.ApiCallback;
import com.example.uitpayapp.network.SessionManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class SellerShopInfoActivity extends AppCompatActivity {

    private ImageView ivShopAvatar;
    private ProgressBar progressBar;
    private RestaurantRepository restaurantRepository;
    private Long currentRestaurantId;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.yumyum_demo_logo)
                            .circleCrop()
                            .into(ivShopAvatar);
                    Toast.makeText(this, "Đã cập nhật ảnh đại diện (chức năng upload ảnh đang phát triển)", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_seller_shop_info);

        restaurantRepository = new RestaurantRepository();
        initViews();
        loadShopDataFromApi();
    }


    private void initViews() {
        ivShopAvatar = findViewById(R.id.iv_shop_avatar);
        progressBar = findViewById(R.id.progress_bar_shop_info);

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


    private void loadShopDataFromApi() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        // Lấy restaurant_id được truyền từ màn hình trước (SellerHomeActivity hoặc SellerReviewActivity)
        long restaurantIdFromIntent = getIntent().getLongExtra("RESTAURANT_ID", -1L);

        if (restaurantIdFromIntent != -1L) {
            currentRestaurantId = restaurantIdFromIntent;
            fetchRestaurantInfo(currentRestaurantId);
        } else {
            // Check SellerPrefs first
            android.content.SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
            long currentStoreId = prefs.getLong("current_store_id", -1L);
            if (currentStoreId != -1L) {
                currentRestaurantId = currentStoreId;
                fetchRestaurantInfo(currentRestaurantId);
            } else {
                // Fallback: tìm restaurant của merchant đang đăng nhập
                SessionManager sessionManager = SessionManager.getInstance(this);
                Long merchantId = sessionManager.getUserId();

                restaurantRepository.getAllRestaurants(new ApiCallback<java.util.List<RestaurantResponseDTO>>() {
                    @Override
                    public void onSuccess(java.util.List<RestaurantResponseDTO> data) {
                        if (data != null) {
                            for (RestaurantResponseDTO r : data) {
                                if (r.getMerchantId() != null && r.getMerchantId().equals(merchantId)) {
                                    currentRestaurantId = r.getId();
                                    fetchRestaurantInfo(currentRestaurantId);
                                    return;
                                }
                            }
                        }
                        // Nếu không tìm thấy, dùng mock
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        loadMockData();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        // Fallback mock nếu API lỗi
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        loadMockData();
                    }
                });
            }
        }
    }

    private void fetchRestaurantInfo(Long restaurantId) {
        restaurantRepository.getRestaurantById(restaurantId, new ApiCallback<RestaurantResponseDTO>() {
            @Override
            public void onSuccess(RestaurantResponseDTO data) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                populateUiWithData(data);
            }

            @Override
            public void onError(String errorMessage) {
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(SellerShopInfoActivity.this,
                        "Không thể tải thông tin quán: " + errorMessage, Toast.LENGTH_SHORT).show();
                loadMockData();
            }
        });
    }

    private void populateUiWithData(RestaurantResponseDTO data) {
        setRowData(R.id.row_shop_name, "Tên cửa hàng", data.getName() != null ? data.getName() : "—");
        setRowData(R.id.row_address, "Địa chỉ", data.getAddress() != null ? data.getAddress() : "—");

        // Đồng bộ dữ liệu vào SharedPreferences
        android.content.SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = prefs.edit();
        if (data.getName() != null) editor.putString("current_store_name", data.getName());
        if (data.getAddress() != null) editor.putString("current_store_address", data.getAddress());
        editor.apply();

        String openTime = data.getOpenTime() != null ? data.getOpenTime() : "";
        String closeTime = data.getCloseTime() != null ? data.getCloseTime() : "";
        String hours = (!openTime.isEmpty() && !closeTime.isEmpty())
                ? openTime + " - " + closeTime
                : "Chưa cập nhật";
        setRowData(R.id.row_opening_hours, "Giờ mở cửa", hours);

        setRowData(R.id.row_shop_type, "Loại hình", "Nhà hàng / Quán ăn");
        setRowData(R.id.row_phone, "Số điện thoại", "Chưa cập nhật");
        setRowData(R.id.row_email, "Email", "Chưa cập nhật");
        setRowData(R.id.row_description, "Mô tả cửa hàng", "Chưa cập nhật");

        TextView tvHeader = findViewById(R.id.tv_shop_name_header);
        if (tvHeader != null) tvHeader.setText(data.getName() != null ? data.getName() : "Cửa hàng của tôi");

        if (data.getImageUrl() != null && !data.getImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(data.getImageUrl())
                    .placeholder(R.drawable.yumyum_demo_logo)
                    .circleCrop()
                    .into(ivShopAvatar);
        }
    }

    private void loadMockData() {
        setRowData(R.id.row_shop_name, "Tên cửa hàng", "Cửa hàng của tôi");
        setRowData(R.id.row_shop_type, "Loại hình", "Nhà hàng / Quán ăn");
        setRowData(R.id.row_address, "Địa chỉ", "Chưa cập nhật");
        setRowData(R.id.row_opening_hours, "Giờ mở cửa", "07:00 - 22:00");
        setRowData(R.id.row_phone, "Số điện thoại", "Chưa cập nhật");
        setRowData(R.id.row_email, "Email", "Chưa cập nhật");
        setRowData(R.id.row_description, "Mô tả cửa hàng", "Chưa cập nhật");

        TextView tvHeader = findViewById(R.id.tv_shop_name_header);
        if (tvHeader != null) tvHeader.setText("Cửa hàng của tôi");
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

        // Pre-fill với dữ liệu hiện tại
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
                String newName = etName != null ? etName.getText().toString().trim() : "";
                String newAddress = etAddress != null ? etAddress.getText().toString().trim() : "";

                // Cập nhật UI trước
                setRowData(R.id.row_shop_name, "Tên cửa hàng", newName);
                setRowData(R.id.row_address, "Địa chỉ", newAddress);
                if (etPhone != null)
                    setRowData(R.id.row_phone, "Số điện thoại", etPhone.getText().toString());
                if (etEmail != null)
                    setRowData(R.id.row_email, "Email", etEmail.getText().toString());
                if (etDescription != null)
                    setRowData(R.id.row_description, "Mô tả cửa hàng", etDescription.getText().toString());

                TextView tvHeader = findViewById(R.id.tv_shop_name_header);
                if (tvHeader != null) tvHeader.setText(newName);

                // Đồng bộ dữ liệu vào SharedPreferences
                android.content.SharedPreferences prefs = getSharedPreferences("SellerPrefs", MODE_PRIVATE);
                android.content.SharedPreferences.Editor editor = prefs.edit();
                editor.putString("current_store_name", newName);
                editor.putString("current_store_address", newAddress);
                editor.apply();

                dialog.dismiss();

                // Gọi API lưu lên Server (nếu có restaurant ID)
                if (currentRestaurantId != null) {
                    saveToApi(newName, newAddress);
                } else {
                    Toast.makeText(this, "Đã cập nhật thông tin (chỉ cục bộ)", Toast.LENGTH_SHORT).show();
                }
            });
        }

        dialog.show();
    }

    private void saveToApi(String name, String address) {
        String currentHours = getRowValue(R.id.row_opening_hours); // Dạng "07:00 - 22:00"
        String openTime = null;
        String closeTime = null;
        if (currentHours.contains(" - ")) {
            String[] parts = currentHours.split(" - ");
            if (parts.length == 2) {
                openTime = parts[0].trim();
                closeTime = parts[1].trim();
            }
        }

        UpdateRestaurantInfoDTO dto = new UpdateRestaurantInfoDTO(name, address, openTime, closeTime, null);
        restaurantRepository.updateRestaurantInfo(currentRestaurantId, dto, new ApiCallback<RestaurantResponseDTO>() {
            @Override
            public void onSuccess(RestaurantResponseDTO data) {
                Toast.makeText(SellerShopInfoActivity.this, "✅ Đã lưu thông tin cửa hàng thành công!", Toast.LENGTH_SHORT).show();
                // Refresh lại dữ liệu từ server
                populateUiWithData(data);
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(SellerShopInfoActivity.this,
                        "⚠️ Cập nhật thất bại: " + errorMessage + "\n(Thay đổi đã được lưu cục bộ)", Toast.LENGTH_LONG).show();
            }
        });
    }
}
