package com.example.uitpayapp.gift;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class giftAdapter extends ListAdapter<gift, giftAdapter.ViewHolder> {

    public giftAdapter() {
        super(DIFF_CALLBACK);
    }

    public static final DiffUtil.ItemCallback<gift> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<gift>() {
                @Override
                public boolean areItemsTheSame(gift oldItem, gift newItem) {
                    return oldItem.id == newItem.id;
                }

                @Override
                public boolean areContentsTheSame(gift oldItem, gift newItem) {
                    return oldItem.title.equals(newItem.title)
                            && oldItem.image == newItem.image;
                }
            };

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView desc;
        ImageView img;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            img = v.findViewById(R.id.imgGift);
            desc=v.findViewById(R.id.tvDesc);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gift_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        gift item = getItem(position);
        holder.title.setText(item.title);
        holder.desc.setText(item.desc);
        holder.img.setImageResource(item.image);
    }
}