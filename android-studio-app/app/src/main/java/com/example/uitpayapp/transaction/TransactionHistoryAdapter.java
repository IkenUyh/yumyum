package com.example.uitpayapp.transaction;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Object> list;
    private boolean showHeader;
    // View types
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private boolean isBalanceVisible = true;

    public TransactionHistoryAdapter(List<Object> list, boolean showHeader) {
        this.list = list;
        this.showHeader = showHeader;
    }

    // Xác định loại item
    @Override
    public int getItemViewType(int position) {
        if (showHeader && list.get(position) instanceof HeaderItem) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<Object> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }
    // Tạo ViewHolder
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_history_item_header_transaction, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_transaction_history, parent, false);
            return new MyViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (showHeader && holder instanceof HeaderViewHolder) {
            HeaderItem header = (HeaderItem) list.get(position);

            HeaderViewHolder hvh = (HeaderViewHolder) holder;

            hvh.tvMonth.setText("Tháng " + header.monthYear);

            DecimalFormat f = new DecimalFormat("###,###,###");

            String income = f.format(header.totalIncome) + "đ";
            String expense = f.format(header.totalExpense) + "đ";

            String fullText = "Tổng thu: " + income + " | Tổng chi: " + expense;

            SpannableString spannable = new SpannableString(fullText);

// tìm vị trí
            int startIncome = fullText.indexOf(income);
            int endIncome = startIncome + income.length();

            int startExpense = fullText.indexOf(expense);
            int endExpense = startExpense + expense.length();

// set bold + màu đen
            spannable.setSpan(new StyleSpan(Typeface.BOLD), startIncome, endIncome, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), startIncome, endIncome, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            spannable.setSpan(new StyleSpan(Typeface.BOLD), startExpense, endExpense, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan(new ForegroundColorSpan(Color.BLACK), startExpense, endExpense, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            hvh.tvSummary.setText(spannable);
            if (isBalanceVisible) {
                hvh.imgHeaderAction.setImageResource(R.drawable.ic_eye);
                hvh.tvAction.setText(" Ẩn số dư");
            } else {
                hvh.imgHeaderAction.setImageResource(R.drawable.ic_invisible_eye);
                hvh.tvAction.setText(" Hiện số dư");
            }
            hvh.layoutAction.setOnClickListener(v -> {
                toggleBalanceVisibility();
            });
        }
        else {
            TransactionHistory item = (TransactionHistory) list.get(position);

            MyViewHolder h = (MyViewHolder) holder;

            h.txtTitle.setText(item.getTitle());
            h.txtDate.setText(item.getDate());
            DecimalFormat formattersodu = new DecimalFormat("###,###,###");
            String remainText = isBalanceVisible
                    ? formattersodu.format(item.getRemain()) + "đ"
                    : "****";
            h.txtRemain.setText("Số dư ví: " + remainText);
            h.txtSource.setText(item.getSource());
            h.txtStatus.setText(item.getStatus());
            DecimalFormat formatter = new DecimalFormat("###,###,###");
            String formattedAmount = formatter.format(item.getAmount()) + "đ";

            if (item.isIncome()) {
                h.txtAmount.setText("+" + formattedAmount);
            } else {
                h.txtAmount.setText("-" + formattedAmount);
            }

            if (item.getStatus().equalsIgnoreCase("Thất bại")) {
                h.txtStatus.setTextColor(Color.RED);
            } else {
                h.txtStatus.setTextColor(Color.parseColor("#70CC58"));
            }

            h.imgMainIcon.setImageResource(item.getMainIconId());
        }
    }

    // ViewHolder cho item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate, txtRemain, txtAmount, txtStatus, txtSource;
        ImageView imgMainIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtRemain = itemView.findViewById(R.id.txtRemain);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtSource = itemView.findViewById(R.id.txtSource);
            imgMainIcon = itemView.findViewById(R.id.imgMainIcon);
        }
    }

    // ViewHolder cho header
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvMonth, tvSummary, tvAction;
        ImageView imgHeaderAction;
        LinearLayout layoutAction;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMonth = itemView.findViewById(R.id.tvMonth);
            tvSummary = itemView.findViewById(R.id.tvSummary);
            tvAction = itemView.findViewById(R.id.tvAction);
            imgHeaderAction = itemView.findViewById(R.id.imgHeaderAction);
            layoutAction = itemView.findViewById(R.id.layoutAction);
        }
    }

    public void toggleBalanceVisibility() {
        isBalanceVisible = !isBalanceVisible;
        notifyDataSetChanged();
    }

}