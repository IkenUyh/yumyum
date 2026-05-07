package com.example.uitpayapp.home.phone_recharge;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class DataPackageAdapter extends RecyclerView.Adapter<DataPackageAdapter.DataViewHolder> {

    public static class DataPack {
        String volume, validity, price;
        boolean isHot;

        public DataPack(String volume, String validity, String price, boolean isHot) {
            this.volume = volume; this.validity = validity; this.price = price; this.isHot = isHot;
        }
    }

    public interface OnPackageSelectedListener {
        void onSelect(String price);
    }

    private List<DataPack> packList;
    private OnPackageSelectedListener listener;
    private int selectedPosition;

    // Đã thêm defaultSelectedPosition vào đây
    public DataPackageAdapter(List<DataPack> packList, int defaultSelectedPosition, OnPackageSelectedListener listener) {
        this.packList = packList;
        this.selectedPosition = defaultSelectedPosition;
        this.listener = listener;
    }

    // Hàm mới: Dùng để xóa viền xanh khi người dùng bấm chọn gói ở danh sách khác
    public void clearSelection() {
        if (selectedPosition != -1) {
            int previous = selectedPosition;
            selectedPosition = -1;
            notifyItemChanged(previous);
        }
    }

    @NonNull
    @Override
    public DataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_package, parent, false);
        return new DataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DataViewHolder holder, int position) {
        DataPack pack = packList.get(position);
        holder.tvVolume.setText(pack.volume);
        holder.tvValidity.setText(pack.validity);
        holder.tvPrice.setText(pack.price);

        if (pack.isHot) {
            holder.tvHot.setVisibility(View.VISIBLE);
        } else {
            holder.tvHot.setVisibility(View.GONE);
        }

        if (selectedPosition == position) {
            holder.cardView.setStrokeColor(Color.parseColor("#0A46A6"));
            holder.cardView.setStrokeWidth(4);
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.cardView.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.cardView.setStrokeWidth(2);
            holder.ivCheck.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            int previous = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previous);
            notifyItemChanged(selectedPosition);
            listener.onSelect(pack.price);
        });
    }

    @Override
    public int getItemCount() {
        return packList.size();
    }

    public static class DataViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardView;
        TextView tvVolume, tvValidity, tvPrice, tvHot;
        ImageView ivCheck;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            tvVolume = itemView.findViewById(R.id.tv_data_volume);
            tvValidity = itemView.findViewById(R.id.tv_data_validity);
            tvPrice = itemView.findViewById(R.id.tv_data_price);
            tvHot = itemView.findViewById(R.id.tv_hot_badge);
            ivCheck = itemView.findViewById(R.id.iv_check);
        }
    }
}