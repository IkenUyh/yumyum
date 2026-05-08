package com.example.uitpayapp.paymentorder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class WaterProviderAdapter extends RecyclerView.Adapter<WaterProviderAdapter.ViewHolder> {
    private List<WaterProvider> providers;
    private OnProviderClickListener listener;

    public interface OnProviderClickListener {
        void onClick(WaterProvider provider);
    }

    public WaterProviderAdapter(List<WaterProvider> providers, OnProviderClickListener listener) {
        this.providers = providers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_provider, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterProvider p = providers.get(position);
        holder.tvTitle.setText(p.getName());
        holder.tvSubtitle.setText(p.getSubtitle());
        holder.ivIcon.setImageResource(p.getIconRes());
        holder.itemView.setOnClickListener(v -> listener.onClick(p));
    }

    @Override
    public int getItemCount() { return providers.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;
        ImageView ivIcon;
        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_provider_title);
            tvSubtitle = itemView.findViewById(R.id.tv_provider_subtitle);
            ivIcon = itemView.findViewById(R.id.iv_provider_icon);
        }
    }
}
