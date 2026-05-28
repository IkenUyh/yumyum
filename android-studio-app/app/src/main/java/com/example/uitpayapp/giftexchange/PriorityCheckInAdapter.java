package com.example.uitpayapp.giftexchange;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class PriorityCheckInAdapter extends RecyclerView.Adapter<PriorityCheckInAdapter.ViewHolder> {
    private List<CheckInModel> checkInList;
    private OnCheckInClickListener checkInClickListener;


    public interface OnCheckInClickListener {
        void onCheckInClick(CheckInModel item);
    }

    public PriorityCheckInAdapter(List<CheckInModel> checkInList, OnCheckInClickListener checkInClickListener) {
        this.checkInList = checkInList;
        this.checkInClickListener = checkInClickListener;
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

        if (item.isOpened()) {
            holder.tvCoin.setTextColor(Color.parseColor("#f24405"));
            holder.tvDay.setTextColor(Color.parseColor("#000000"));
            holder.imgIcon.clearColorFilter();
            if (item.isChecked()) {
                holder.imgIcon.setImageResource(R.drawable.ic_circle_check);
            }
        } else {
            holder.tvCoin.setTextColor(Color.parseColor("#BDBDBD"));
            holder.tvDay.setTextColor(Color.parseColor("#BDBDBD"));
            holder.imgIcon.setColorFilter(Color.parseColor("#BDBDBD"));
        }
        holder.itemView.setOnClickListener(v -> {
            if (checkInClickListener != null&& item.isOpened()&&!item.isChecked()) {
                Animation animation = AnimationUtils.loadAnimation(holder.imgIcon.getContext(), R.anim.anim_collect_coin);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        checkInClickListener.onCheckInClick(item);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                holder.imgIcon.startAnimation(animation);
            }
        });
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
