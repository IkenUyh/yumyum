package com.example.uitpayapp.transaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import java.util.List;

public class ChonThangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MonthYearClass> items;
    private OnThangClickListener listener; // Thêm biến nhận bộ đàm

    public interface OnThangClickListener {
        void onThangClick(int thang, int nam);
    }

    public ChonThangAdapter(List<MonthYearClass> items, OnThangClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MonthYearClass.TYPE_NAM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_year_header, parent, false);
            return new NamViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_month_item, parent, false);
            return new ThangViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MonthYearClass item = items.get(position);

        if (holder instanceof NamViewHolder && item instanceof YearHeader) {
            YearHeader namItem = (YearHeader) item;
            ((NamViewHolder) holder).tvNam.setText(String.valueOf(namItem.getNam()));

        } else if (holder instanceof ThangViewHolder && item instanceof MonthItem) {
            MonthItem thangItem = (MonthItem) item;
            ThangViewHolder thangHolder = (ThangViewHolder) holder;
            ((ThangViewHolder) holder).tvThang.setText("Tháng " + thangItem.getThang());

            if (thangItem.isSelected()) {
                thangHolder.tvThang.setBackgroundResource(R.drawable.bg_transaction_history_thang_selected);
                thangHolder.tvThang.setTextColor(android.graphics.Color.parseColor("#0C40CC"));
            } else {
                thangHolder.tvThang.setBackgroundResource(R.drawable.transaction_history_bg_thang_unselected);
                thangHolder.tvThang.setTextColor(android.graphics.Color.parseColor("#333333"));
            }

            thangHolder.itemView.setOnClickListener(v -> {
                for (MonthYearClass i : items) {
                    if (i instanceof MonthItem) {
                        ((MonthItem) i).setSelected(false);
                    }
                }
                thangItem.setSelected(true);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.onThangClick(thangItem.getThang(), thangItem.getNam());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class NamViewHolder extends RecyclerView.ViewHolder {
        TextView tvNam;
        NamViewHolder(View itemView) {
            super(itemView);
            tvNam = itemView.findViewById(R.id.tvNam);
        }
    }

    static class ThangViewHolder extends RecyclerView.ViewHolder {
        TextView tvThang;
        ThangViewHolder(View itemView) {
            super(itemView);
            tvThang = itemView.findViewById(R.id.tvTenThang);
        }
    }
}