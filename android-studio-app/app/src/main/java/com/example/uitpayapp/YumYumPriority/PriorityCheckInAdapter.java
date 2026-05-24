package com.example.uitpayapp.YumYumPriority;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class PriorityCheckInAdapter extends RecyclerView.Adapter<PriorityCheckInAdapter.ViewHolder> {
    private List<CheckInModel> checkInList;

    public PriorityCheckInAdapter(List<CheckInModel> checkInList) {
        this.checkInList = checkInList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.priority_checkin_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckInModel item = checkInList.get(position);
        CheckInModel.DayConfig config = item.getConfig();

        holder.tvDay.setText(config.getTitle());
        holder.tvCoin.setText("+" + config.getCoins());
        holder.imgIcon.setImageResource(config.getIconRes());

        if (item.isChecked()) {
            holder.tvCoin.setTextColor(Color.parseColor("#f24405"));
            holder.tvDay.setTextColor(Color.parseColor("#000000"));
            holder.imgIcon.clearColorFilter();
        } else {
            holder.tvCoin.setTextColor(Color.parseColor("#BDBDBD"));
            holder.tvDay.setTextColor(Color.parseColor("#BDBDBD"));
            holder.imgIcon.setColorFilter(Color.parseColor("#BDBDBD"));
        }
    }

    @Override
    public int getItemCount() {
        return checkInList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCoin, tvDay;
        ImageView imgIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCoin = itemView.findViewById(R.id.checkin_coin);
            tvDay = itemView.findViewById(R.id.checkin_day);
            imgIcon = itemView.findViewById(R.id.checkin_icon);
        }
    }
}
