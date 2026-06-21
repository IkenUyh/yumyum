package com.example.uitpayapp.modules.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.modules.user.models.responses.AddressResponseDTO;
import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<AddressResponseDTO> addresses;
    private Long selectedAddressId;
    private OnAddressClickListener listener;

    public interface OnAddressClickListener {
        void onAddressClick(AddressResponseDTO address);
    }

    public AddressAdapter(List<AddressResponseDTO> addresses, Long selectedAddressId, OnAddressClickListener listener) {
        this.addresses = addresses;
        this.selectedAddressId = selectedAddressId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressResponseDTO address = addresses.get(position);
        
        holder.tvAddressDetail.setText(address.getDetailedAddress() != null ? address.getDetailedAddress() : "");
        holder.tvAddressReceiver.setText((address.getRecipientName() != null ? address.getRecipientName() : "") + " - " + (address.getPhoneNumber() != null ? address.getPhoneNumber() : ""));
        
        String type = address.getAddressName() != null ? address.getAddressName() : "HOME";
        if ("GPS".equalsIgnoreCase(type) || (address.getId() != null && address.getId() == -1L)) {
            holder.ivAddressIcon.setImageResource(R.drawable.ic_location);
            holder.tvAddressTitle.setText("Địa điểm hiện tại");
            holder.tvAddressReceiver.setVisibility(View.GONE);
        } else if ("WORK".equalsIgnoreCase(type)) {
            holder.ivAddressIcon.setImageResource(R.drawable.ic_location); 
            holder.tvAddressTitle.setText("Công ty");
            holder.tvAddressReceiver.setVisibility(View.VISIBLE);
        } else {
            holder.ivAddressIcon.setImageResource(R.drawable.ic_home_24px);
            holder.tvAddressTitle.setText("Nhà");
            holder.tvAddressReceiver.setVisibility(View.VISIBLE);
        }

        if (selectedAddressId != null && selectedAddressId.equals(address.getId())) {
            holder.ivAddressCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivAddressCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAddressClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addresses == null ? 0 : addresses.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAddressIcon;
        TextView tvAddressTitle;
        TextView tvAddressDetail;
        TextView tvAddressReceiver;
        ImageView ivAddressCheck;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAddressIcon = itemView.findViewById(R.id.iv_address_icon);
            tvAddressTitle = itemView.findViewById(R.id.tv_address_title);
            tvAddressDetail = itemView.findViewById(R.id.tv_address_detail);
            tvAddressReceiver = itemView.findViewById(R.id.tv_address_receiver);
            ivAddressCheck = itemView.findViewById(R.id.iv_address_check);
        }
    }
}
