package com.example.uitpayapp.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.deliveryaddressorder.AddressOrderActivity;
import com.example.uitpayapp.modules.user.AddressAdapter;
import com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

public class AddressBottomSheetHelper {

    public interface OnAddressSelectedListener {
        void onAddressSelected(AddressResponseDTO address);
    }

    public static void showAddressBottomSheet(Context context, List<AddressResponseDTO> addresses, Long currentSelectedId, OnAddressSelectedListener listener) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_bottom_sheet_addresses, null);
        bottomSheetDialog.setContentView(view);

        RecyclerView rvAddresses = view.findViewById(R.id.rv_addresses);
        rvAddresses.setLayoutManager(new LinearLayoutManager(context));

        if (addresses != null && !addresses.isEmpty()) {
            AddressAdapter adapter = new AddressAdapter(addresses, currentSelectedId, address -> {
                if (listener != null) {
                    listener.onAddressSelected(address);
                }
                bottomSheetDialog.dismiss();
            });
            rvAddresses.setAdapter(adapter);
        } else {
            // Hiển thị một message không có địa chỉ nếu cần (hoặc RecyclerView tự động trống)
        }

        TextView btnAddNew = view.findViewById(R.id.btn_add_new_address);
        btnAddNew.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(context, AddressOrderActivity.class);
            context.startActivity(intent);
        });

        bottomSheetDialog.show();
    }
}
