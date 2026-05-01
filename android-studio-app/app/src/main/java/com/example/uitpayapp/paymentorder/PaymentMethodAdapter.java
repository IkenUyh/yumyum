package com.example.uitpayapp.paymentorder;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;

import java.util.Collections;
import java.util.List;

public class PaymentMethodAdapter extends RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder> {

    private List<PaymentMethod> paymentList;
    private OnStartDragListener DragStartListener;

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public PaymentMethodAdapter(List<PaymentMethod> list, OnStartDragListener dragStartListener) {
        this.paymentList = list;
        this.DragStartListener = dragStartListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_payment_method, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaymentMethod item = paymentList.get(position);
        holder.tvOrderIndex.setText(String.valueOf(position + 1));
        holder.tvMethodName.setText(item.getName());
        holder.tvMethodDetail.setText(item.getDetail());
        holder.ivMethodIcon.setImageResource(item.getIconResId());
        holder.switchEnable.setChecked(item.isEnabled());

        holder.ivDragHandle.setOnTouchListener((v, event) -> {
            //lenh action giong C# mot xiu
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                DragStartListener.onStartDrag(holder);
            }
            return false;
        });
    }
    public void onItemMove(int fromPosition, int toPosition) {
        //doi cho
        Collections.swap(paymentList, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        //cap nhat adapter
        notifyItemRangeChanged(Math.min(fromPosition, toPosition), Math.abs(fromPosition - toPosition) + 1);
    }
    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderIndex, tvMethodName, tvMethodDetail;
        ImageView ivMethodIcon, ivDragHandle;
        SwitchCompat switchEnable;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderIndex = itemView.findViewById(R.id.tvOrderIndex);
            tvMethodName = itemView.findViewById(R.id.tvMethodName);
            tvMethodDetail = itemView.findViewById(R.id.tvMethodDetail);
            ivMethodIcon = itemView.findViewById(R.id.ivMethodIcon);
            ivDragHandle = itemView.findViewById(R.id.ivDragHandle);
            switchEnable = itemView.findViewById(R.id.switchEnable);
        }
    }
}