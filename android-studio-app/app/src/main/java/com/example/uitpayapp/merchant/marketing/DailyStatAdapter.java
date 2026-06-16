package com.example.uitpayapp.merchant.marketing;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.text.DecimalFormat;
import java.util.List;

public class DailyStatAdapter extends RecyclerView.Adapter<DailyStatAdapter.ViewHolder> {
    private final List<DailyStat> stats;
    private final DecimalFormat currencyFormatter;

    public DailyStatAdapter(List<DailyStat> stats, DecimalFormat currencyFormatter) {
        this.stats = stats;
        this.currencyFormatter = currencyFormatter;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyStat item = stats.get(position);
        holder.tvLabel.setText(item.getDate());
        holder.tvValue.setText(String.format("%sđ", currencyFormatter.format(item.getAmount())));
    }

    @Override
    public int getItemCount() {
        return stats.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;

        ViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tv_label);
            tvValue = itemView.findViewById(R.id.tv_value);
        }
    }
}
