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
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

public class AddressOrderActivity extends AppCompatActivity {

    private RecyclerView rvPaymentMethods;
    private DeliveryAddressAdapter adapter;
    private List<DeliveryAddress> deliveryAddresses;
    private ItemTouchHelper itemTouchHelper;
    private TextView tvSelectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_order);
        initView();
        setUpDataAddress();
        setupRecyclerView();
        
        findViewById(R.id.btn_add_address_header).setOnClickListener(v -> showAddressBottomSheet(null, -1));
        findViewById(R.id.btnSave).setOnClickListener(v -> finish());
    }

    private void initView() {
        View topBar = findViewById(R.id.top_bar_payment_order);
        TextView tvTitle = topBar.findViewById(R.id.top_bar_title);
        tvTitle.setText("Vị trí");
        topBar.findViewById(R.id.top_bar_back_btn).setOnClickListener(v -> finish());
        View bottomPanel = findViewById(R.id.bottom_panel);
        ViewCompat.setOnApplyWindowInsetsListener(topBar, (v, insets) -> {
            Insets cutout = insets.getInsets(WindowInsetsCompat.Type.displayCutout());
            int safeTopPadding = cutout.top + 10;
            v.setPadding(v.getPaddingLeft(), safeTopPadding, v.getPaddingRight(), v.getPaddingBottom());
            //thanh duoi
            Insets navInsets = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
            int safeBottomPadding = navInsets.bottom + 10;
            if (bottomPanel != null) {
                bottomPanel.setPadding(bottomPanel.getPaddingLeft(), bottomPanel.getPaddingTop(), bottomPanel.getPaddingRight(), safeBottomPadding);
            }
            return insets;
        });
    }

    private void setUpDataAddress() {
        deliveryAddresses = new ArrayList<>();
        deliveryAddresses.add(new DeliveryAddress(DeliveryAddress.AddressType.HOME,"48 Phó Cơ Điều, Phường Chợ Lớn, TP. Hồ Chí Minh","Nguyen Van A","6767676767"));
        deliveryAddresses.add(new DeliveryAddress(DeliveryAddress.AddressType.WORK,"268 Lý Thường Kiệt, Phường Diên Hồng, TP. Hồ Chí Minh","Nguyen Van B","6868686868"));
        deliveryAddresses.add(new DeliveryAddress(DeliveryAddress.AddressType.HOME,"803 Kha Vạn Cân, Phường Linh Xuân, TP. Hồ Chí Minh","Nguyen Van C","6969696969"));
        deliveryAddresses.add(new DeliveryAddress(DeliveryAddress.AddressType.WORK,"215 Điện Biên Phủ, Phường Gia Định, TP. Hồ Chí Minh","Nguyen Van D","6363636363"));
    }

    private void setupRecyclerView() {
        rvPaymentMethods = findViewById(R.id.rvPaymentMethods);
        rvPaymentMethods.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeliveryAddressAdapter(deliveryAddresses, new DeliveryAddressAdapter.OnAddressActionListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelper.startDrag(viewHolder);
            }

            @Override
            public void onEditClick(DeliveryAddress address, int position) {
                showAddressBottomSheet(address, position);
            }
        });
        rvPaymentMethods.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                adapter.onItemMove(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
                return true;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }
            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }
        };
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvPaymentMethods);
    }

    private void showAddressBottomSheet(DeliveryAddress address, int position) {
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
        tvSelectedAddress = view.findViewById(R.id.tv_selected_address);

        final DeliveryAddress.AddressType[] selectedType = {DeliveryAddress.AddressType.HOME};

        if (address != null) {
            tvTitle.setText("Chỉnh sửa địa chỉ");
            etName.setText(address.getReceiverName());
            etPhone.setText(address.getPhoneNumber());
            tvSelectedAddress.setText(address.getAddressDetail());
            selectedType[0] = address.getAddressType();
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
        } else {
            tvTitle.setText("Thêm địa chỉ mới");
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
            btnSave.setEnabled(true);
            btnSave.setAlpha(1.0f);
        }

        btnTypeHome.setOnClickListener(v -> {
            selectedType[0] = DeliveryAddress.AddressType.HOME;
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
        });

        btnTypeWork.setOnClickListener(v -> {
            selectedType[0] = DeliveryAddress.AddressType.WORK;
            updateTypeUI(btnTypeHome, btnTypeWork, selectedType[0]);
        });

        btnSave.setOnClickListener(v -> {
            if (address!=null) {
                address.updateAddress(tvSelectedAddress.getText().toString(),etName.getText().toString(),etPhone.getText().toString(),selectedType[0]);
            }
            else
                deliveryAddresses.add(new DeliveryAddress(selectedType[0],tvSelectedAddress.getText().toString(),etName.getText().toString(),etPhone.getText().toString()));
            adapter.notifyDataSetChanged();
            bottomSheetDialog.dismiss();
        });
        tvSelectedAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapPickerActivity.class);
            intent.putExtra("ADDRESS_PUT",tvSelectedAddress.getText().toString());
            startActivityForResult(intent, 1);
        });

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void updateTypeUI(TextView home, TextView work, DeliveryAddress.AddressType type) {
        if (type == DeliveryAddress.AddressType.HOME) {
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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String address = data.getStringExtra("ADDRESS_SELECTED");
            if (address != null) {
                Toast.makeText(this, "Address: " + address, Toast.LENGTH_SHORT).show();
                tvSelectedAddress.setText(address);
            }
        }
    }
}
