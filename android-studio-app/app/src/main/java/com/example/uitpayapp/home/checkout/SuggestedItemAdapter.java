package com.example.uitpayapp.home.checkout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class SuggestedItemAdapter extends RecyclerView.Adapter<SuggestedItemAdapter.SuggestedItemViewHolder> {

    private List<SuggestedItem> items;

    public SuggestedItemAdapter(List<SuggestedItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public SuggestedItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggested_product, parent, false);
        return new SuggestedItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedItemViewHolder holder, int position) {
        SuggestedItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(String.format("%,dđ", item.getPrice()).replace(',', '.'));
        // holder.imgItem.setImageResource(item.getImageResId()); // Skipping for dummy

        holder.btnAdd.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Đã thêm " + item.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class SuggestedItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvName;
        TextView tvPrice;
        View btnAdd;

        public SuggestedItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_suggested_item);
            tvName = itemView.findViewById(R.id.tv_suggested_item_name);
            tvPrice = itemView.findViewById(R.id.tv_suggested_item_price);
            btnAdd = itemView.findViewById(R.id.btn_add_suggested_item);
        }
    }

    public static class SuggestedItem {
        private String name;
        private long price;
        private int imageResId;

        public SuggestedItem(String name, long price, int imageResId) {
            this.name = name;
            this.price = price;
            this.imageResId = imageResId;
        }

        public String getName() {
            return name;
        }

        public long getPrice() {
            return price;
        }

        public int getImageResId() {
            return imageResId;
        }
    }
}
