package com.example.uitpayapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.MyViewHolder> {

    // Danh sách dữ liệu truyền vào
    private List<TransactionHistory> transactionList;

    // Constructor để nhận dữ liệu từ Activity
    public TransactionHistoryAdapter(List<TransactionHistory> transactionList) {
        this.transactionList = transactionList;
    }

    //  Nạp file giao diện XML vào Adapter
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction_history, parent, false);
        return new MyViewHolder(view);
    }

    //  Gán dữ liệu vào các Text/Image trên giao diện
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TransactionHistory item = transactionList.get(position);

        // Gán các chuỗi text cơ bản
        holder.txtTitle.setText(item.getTitle());
        holder.txtDate.setText(item.getDate());
        holder.txtRemain.setText("Số dư ví: " + item.getRemain());
        holder.txtSource.setText(item.getSource());
        holder.txtStatus.setText(item.getStatus());

        // Định dạng số tiền (Thêm dấu chấm hàng nghìn và chữ đ)
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        String formattedAmount = formatter.format(item.getAmount()) + "đ";

        // Logic đổi màu theo Số tiền (Âm hay Dương)
        if (item.isIncome()) {
            holder.txtAmount.setText("+" + formattedAmount);
            holder.txtAmount.setTextColor(Color.BLACK); // Màu đen cho tiền trừ
        } else {
            holder.txtAmount.setText("-"+formattedAmount); // Bản thân số âm đã có dấu -
            holder.txtAmount.setTextColor(Color.BLACK); // Màu đen cho tiền trừ
        }

        // Logic đổi màu theo Trạng thái
        if (item.getStatus().equalsIgnoreCase("Thất bại")) {
            holder.txtStatus.setTextColor(Color.RED);
        } else {
            holder.txtStatus.setTextColor(Color.parseColor("#70CC58")); // Xanh lá
        }
        holder.imgMainIcon.setImageResource(item.getMainIconId());
    }

    //  Khai báo số lượng item có trong danh sách
    @Override
    public int getItemCount() {
        if (transactionList != null) {
            return transactionList.size();
        }
        return 0;
    }

    // Dùng để ánh xạ (tìm) các View bên trong file XML
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDate, txtRemain, txtAmount, txtStatus, txtSource;
        ImageView imgSourceIcon, imgMainIcon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Khớp ID với file item_transaction_history.xml của bạn
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtRemain = itemView.findViewById(R.id.txtRemain);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtSource = itemView.findViewById(R.id.txtSource);
            imgSourceIcon = itemView.findViewById(R.id.imgSourceIcon);
            imgMainIcon = itemView.findViewById(R.id.imgMainIcon);
        }
    }
}