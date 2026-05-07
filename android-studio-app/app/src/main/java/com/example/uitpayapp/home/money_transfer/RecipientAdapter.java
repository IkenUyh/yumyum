package com.example.uitpayapp.home.money_transfer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.ViewHolder> {

    public interface OnRecipientClickListener {
        void onRecipientClick(RecipientItem item);
    }

    private List<RecipientItem> listRecipients;
    private OnRecipientClickListener listener;

    public RecipientAdapter(List<RecipientItem> listRecipients, OnRecipientClickListener listener) {
        this.listRecipients = listRecipients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quick_select, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipientItem item = listRecipients.get(position);
        holder.tvName.setText(item.getName());
        holder.ivAvatar.setImageResource(item.getAvatarResId());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecipientClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRecipients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}