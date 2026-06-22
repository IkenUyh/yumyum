package com.example.uitpayapp.deliveryaddressorder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;

import com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO;

import java.util.List;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder> {

    private List<AddressResponseDTO> addressList;
    private OnAddressActionListener actionListener;

    public interface OnAddressActionListener {
        void onEditClick(AddressResponseDTO address, int position);
    }

    public DeliveryAddressAdapter(List<AddressResponseDTO> list, OnAddressActionListener listener) {
        this.addressList = list;
        this.actionListener = listener;
    }

    public void updateData(List<AddressResponseDTO> list) {
        this.addressList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddressResponseDTO item = addressList.get(position);
        
        holder.tvOrderIndex.setText(String.valueOf(position + 1));
        
        String typeName = item.getAddressName() != null ? item.getAddressName() : "Nhà";
        holder.tvTypeAddressTitle.setText(typeName);
        
        if ("Công ty".equalsIgnoreCase(typeName) || "WORK".equalsIgnoreCase(typeName)) {
            holder.ivTypeAddress.setImageResource(R.drawable.ic_location);
        } else {
            holder.ivTypeAddress.setImageResource(R.drawable.ic_home_24px);
        }
        
        holder.tvDetailAddress.setText(item.getDetailedAddress() != null ? item.getDetailedAddress() : "");
        holder.tvNameReceiver.setText(item.getRecipientName() != null ? item.getRecipientName() : "");
        holder.tvPhoneNumber.setText(item.getPhoneNumber() != null ? item.getPhoneNumber() : "");
        
        if (item.getIsDefault() != null && item.getIsDefault()) {
            holder.tvDefaultBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvDefaultBadge.setVisibility(View.GONE);
        }

        holder.tvEditAddress.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(item, holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList == null ? 0 : addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderIndex, tvTypeAddressTitle, tvDetailAddress, tvNameReceiver, tvPhoneNumber, tvEditAddress, tvDefaultBadge;
        ImageView ivTypeAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderIndex = itemView.findViewById(R.id.tvOrderIndex);
            tvTypeAddressTitle = itemView.findViewById(R.id.tv_type_address_title);
            tvDetailAddress = itemView.findViewById(R.id.tv_detail_address);
            tvNameReceiver = itemView.findViewById(R.id.tv_name_receiver);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            tvEditAddress = itemView.findViewById(R.id.tv_edit_address);
            ivTypeAddress = itemView.findViewById(R.id.iv_type_address);
            tvDefaultBadge = itemView.findViewById(R.id.tv_default_badge);
        }
    }
}