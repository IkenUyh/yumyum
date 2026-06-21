package com.example.uitpayapp.YumYumPriority;

import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.profile.GroupItemData;
import com.example.uitpayapp.profile.ProfileMenuAdapter;

import java.util.ArrayList;
import java.util.List;

public class PriorityAccountRankAdapter extends RecyclerView.Adapter<PriorityAccountRankAdapter.ViewHolder> {
    private List<RankModel> rankList;
    public PriorityAccountRankAdapter(List<RankModel> rankList) {
        this.rankList = rankList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.priority_account_rank_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RankModel model = rankList.get(position);
        holder.tvTitle.setText(model.getRankTitle());
        holder.tvBadge.setText(model.getRankBadge());
        holder.tvCondition.setText(model.getCondition());
        if (model.isLocked()) {
            holder.progressBar.setVisibility(View.GONE);
        } else holder.progressBar.setVisibility(VISIBLE);
        holder.progressBar.setProgress(model.getProgress());
        int color;
        color = Color.parseColor(model.getRankType().getColor());
        holder.layoutOuter.setBackgroundColor(color);
        holder.cardInner.setBackgroundColor(color);
        holder.btnViewMoreBenefits.setOnClickListener(v -> {
            if (holder.itemView.getContext() instanceof PriorityYumYumActivity) {
                ((PriorityYumYumActivity) holder.itemView.getContext()).showRankBenefitsBottomSheet();
            }
        });
    }

    @Override
    public int getItemCount() {
        return rankList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutOuter;
        View cardInner;
        TextView tvTitle, tvBadge, tvCondition, btnViewMoreBenefits;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutOuter = itemView.findViewById(R.id.priority_account_rank_outer);
            cardInner = itemView.findViewById(R.id.priority_account_rank_inner);
            tvTitle = itemView.findViewById(R.id.priority_account_rank_title);
            tvBadge = itemView.findViewById(R.id.priority_account_rank_badge);
            tvCondition = itemView.findViewById(R.id.rank_progress_bar_title);
            progressBar = itemView.findViewById(R.id.rank_progress_bar);
            btnViewMoreBenefits = itemView.findViewById(R.id.btn_view_more_benefits);
        }
    }
}
