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

import java.util.Collections;
import java.util.List;

public class DeliveryAddressAdapter extends RecyclerView.Adapter<DeliveryAddressAdapter.ViewHolder> {

    private List<DeliveryAddress> addressList;
    private OnAddressActionListener actionListener;

    public interface OnAddressActionListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
        void onEditClick(DeliveryAddress address, int position);
    }

    public DeliveryAddressAdapter(List<DeliveryAddress> list, OnAddressActionListener listener) {
        this.addressList = list;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_delivery_address, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeliveryAddress item = addressList.get(position);
        DeliveryAddress.AddressType addressType = item.getAddressType();
        holder.tvOrderIndex.setText(String.valueOf(position + 1));
        holder.tvTypeAddressTitle.setText(addressType.getDisplayName());
        holder.tvDetailAddress.setText(item.getAddressDetail());
        holder.tvNameReceiver.setText(item.getReceiverName());
        holder.tvPhoneNumber.setText(item.getPhoneNumber());
        holder.ivTypeAddress.setImageResource(addressType.getIconResId());

        holder.ivDragHandle.setOnTouchListener((v, event) -> {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                actionListener.onStartDrag(holder);
            }
            return false;
        });

        holder.tvEditAddress.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(item, holder.getBindingAdapterPosition());
            }
        });
    }

    public void onItemMove(int fromPosition, int toPosition) {
        Collections.swap(addressList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1);
    }


    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderIndex, tvTypeAddressTitle, tvDetailAddress, tvNameReceiver, tvPhoneNumber, tvEditAddress;
        ImageView ivTypeAddress, ivDragHandle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderIndex = itemView.findViewById(R.id.tvOrderIndex);
            tvTypeAddressTitle = itemView.findViewById(R.id.tv_type_address_title);
            tvDetailAddress = itemView.findViewById(R.id.tv_detail_address);
            tvNameReceiver = itemView.findViewById(R.id.tv_name_receiver);
            tvPhoneNumber = itemView.findViewById(R.id.tv_phone_number);
            tvEditAddress = itemView.findViewById(R.id.tv_edit_address);
            ivTypeAddress = itemView.findViewById(R.id.iv_type_address);
            ivDragHandle = itemView.findViewById(R.id.ivDragHandle);
        }
    }
}