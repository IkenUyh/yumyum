package com.example.uitpayapp.home;// File: ServiceAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private List<ServiceItem> serviceList;
    private int layoutResId;

    public ServiceAdapter(List<ServiceItem> serviceList, int layoutResId) {
        this.serviceList = serviceList;
        this.layoutResId = layoutResId;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResId, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceItem item = serviceList.get(position);

        // Đặt tên và icon
        holder.tvServiceName.setText(item.getName());
        holder.ivServiceIcon.setImageResource(item.getIconResId());

        if (item.getBadgeText() != null && !item.getBadgeText().isEmpty()) {
            holder.tvServiceBadge.setText(item.getBadgeText());
            holder.tvServiceBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvServiceBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView ivServiceIcon;
        TextView tvServiceName, tvServiceBadge;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            ivServiceIcon = itemView.findViewById(R.id.ivServiceIcon);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceBadge = itemView.findViewById(R.id.tvServiceBadge);
        }
    }
}