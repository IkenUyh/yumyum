package com.example.uitpayapp.merchant.shop;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.MerchantMenuItem;
import com.example.uitpayapp.merchant.shop.shop_model.ToppingGroup;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class MerchantToppingAdapter extends RecyclerView.Adapter<MerchantToppingAdapter.ToppingViewHolder> {

    private List<ToppingGroup> groups;

    public MerchantToppingAdapter(List<ToppingGroup> groups) {
        this.groups = groups;
    }

    public void updateList(List<ToppingGroup> newList) {
        this.groups = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ToppingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topping_group, parent, false);
        return new ToppingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToppingViewHolder holder, int position) {
        ToppingGroup group = groups.get(position);
        holder.tvGroupName.setText(group.getName());
        holder.tvEnabledCount.setText(group.getEnabledCount() + "/" + group.getToppings().size());
        
        String toppingsText = group.getToppings().stream()
                .map(MerchantMenuItem::getName)
                .collect(Collectors.joining(", "));
        holder.tvToppingList.setText(toppingsText);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ToppingGroupDetailActivity.class);
            intent.putExtra("group_name", group.getName());
            intent.putExtra("toppings", (Serializable) group.getToppings());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class ToppingViewHolder extends RecyclerView.ViewHolder {
        TextView tvGroupName, tvEnabledCount, tvToppingList;

        public ToppingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvEnabledCount = itemView.findViewById(R.id.tv_enabled_count);
            tvToppingList = itemView.findViewById(R.id.tv_topping_list);
        }
    }
}
