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
    int exploreMode=View.GONE;

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
        holder.tvBenefit1.setText(model.getAccumulatedBenefit());
        holder.tvBenefit2.setText(model.getVoucherBenefit());
        int color;
        color = Color.parseColor(model.getRankType().getColor());
        holder.layoutOuter.setBackgroundColor(color);
        holder.cardInner.setBackgroundColor(color);
        List<GroupItemData> group=new ArrayList<>();
        group.add(new GroupItemData("Chi tiết đặc quyền",model.getRankBenefits()));
        ((RecyclerView)holder.rvBenefit).setAdapter(new ProfileMenuAdapter(holder.itemView.getContext(),group,null));
        holder.tvExplore.setOnClickListener(v->
        {
            if (exploreMode==View.GONE) {
                holder.tvExplore.setText("Thu gọn <");
                exploreMode=View.VISIBLE;
                holder.rvBenefit.setVisibility(VISIBLE);
            } else {
                holder.tvExplore.setText("Khám phá >");
                exploreMode=View.GONE;
                holder.rvBenefit.setVisibility(View.GONE);
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
        TextView tvTitle, tvBadge, tvCondition, tvBenefit1, tvBenefit2,tvExplore;
        ProgressBar progressBar;
        RecyclerView rvBenefit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutOuter = itemView.findViewById(R.id.priority_account_rank_outer);
            cardInner = itemView.findViewById(R.id.priority_account_rank_inner);
            tvTitle = itemView.findViewById(R.id.priority_account_rank_title);
            tvBadge = itemView.findViewById(R.id.priority_account_rank_badge);
            tvCondition = itemView.findViewById(R.id.rank_progress_bar_title);
            progressBar = itemView.findViewById(R.id.rank_progress_bar);
            tvBenefit1 = itemView.findViewById(R.id.priority_accumulated_balance);
            tvBenefit2 = itemView.findViewById(R.id.priority_voucher);
            tvExplore = itemView.findViewById(R.id.tv_priority_explore);
            rvBenefit = itemView.findViewById(R.id.rv_priority_rank);
        }
    }
}
