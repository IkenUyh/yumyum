package com.example.uitpayapp.home.phone_recharge;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.example.uitpayapp.R;
import java.util.List;

public class CarrierAdapter extends RecyclerView.Adapter<CarrierAdapter.CarrierViewHolder> {

    private List<Integer> carrierLogos;
    private int selectedPosition = 0;

    public CarrierAdapter(List<Integer> carrierLogos) {
        this.carrierLogos = carrierLogos;
    }

    @NonNull
    @Override
    public CarrierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrier, parent, false);
        return new CarrierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarrierViewHolder holder, int position) {
        holder.ivCarrier.setImageResource(carrierLogos.get(position));

        if (selectedPosition == position) {
            holder.cardView.setStrokeColor(Color.parseColor("#0A46A6"));
            holder.cardView.setStrokeWidth(4);
        } else {
            holder.cardView.setStrokeColor(Color.parseColor("#E0E0E0"));
            holder.cardView.setStrokeWidth(2);
        }

        holder.itemView.setOnClickListener(v -> {
            int previousItem = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousItem);
            notifyItemChanged(selectedPosition);
        });
    }

    @Override
    public int getItemCount() {
        return carrierLogos.size();
    }

    public static class CarrierViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCarrier;
        MaterialCardView cardView;

        public CarrierViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCarrier = itemView.findViewById(R.id.iv_carrier_logo);
            cardView = (MaterialCardView) itemView;
        }
    }
}