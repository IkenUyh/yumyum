package com.example.uitpayapp.merchant.shop;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;
import com.example.uitpayapp.merchant.shop.shop_model.ReviewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SellerReviewAdapter extends RecyclerView.Adapter<SellerReviewAdapter.ViewHolder> {

    public interface OnReplyListener {
        void onReply(ReviewModel review, int position, String replyContent, BottomSheetDialog dialog);
    }

    public interface OnOrderClickListener {
        void onOrderClick(String orderIdRaw);
    }

    private List<ReviewModel> reviewList;
    private OnReplyListener onReplyListener;
    private OnOrderClickListener onOrderClickListener;

    public SellerReviewAdapter(List<ReviewModel> reviewList) {
        this.reviewList = reviewList;
    }

    public void setOnReplyListener(OnReplyListener listener) {
        this.onReplyListener = listener;
    }

    public void setOnOrderClickListener(OnOrderClickListener listener) {
        this.onOrderClickListener = listener;
    }

    public void updateData(List<ReviewModel> newList) {
        this.reviewList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_seller_review, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);
        holder.tvUserName.setText(review.getUserName());
        holder.tvDate.setText(review.getDate());
        holder.tvContent.setText(review.getContent());
        holder.ratingBar.setRating(review.getRating());
        holder.tvRatingScore.setText(String.valueOf(review.getRating()));
        holder.tvOrderId.setText(review.getOrderId());
        
        // Remove underline from tvOrderId
        holder.tvOrderId.setTextColor(android.graphics.Color.parseColor("#999999"));
        holder.tvOrderId.setPaintFlags(holder.tvOrderId.getPaintFlags() & (~android.graphics.Paint.UNDERLINE_TEXT_FLAG));

        // Load Avatar
        if (review.getUserAvatar() != null && !review.getUserAvatar().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(review.getUserAvatar())
                    .placeholder(R.drawable.bg_circle_gray)
                    .error(R.drawable.bg_circle_gray)
                    .into(holder.ivAvatar);
        } else {
            holder.ivAvatar.setImageResource(R.drawable.bg_circle_gray);
        }

        // Set click listener on the correct "Xem đơn hàng" button
        if (holder.tvBtnViewOrder != null) {
            holder.tvBtnViewOrder.setOnClickListener(v -> {
                if (onOrderClickListener != null) {
                    onOrderClickListener.onOrderClick(review.getOrderId());
                }
            });
        }

        if (review.getReviewImage() != null && !review.getReviewImage().isEmpty()) {
            holder.cvReviewImage.setVisibility(View.VISIBLE);
        } else {
            holder.cvReviewImage.setVisibility(View.GONE);
        }
        if (review.hasReply()) {
            holder.cvReplyContainer.setVisibility(View.VISIBLE);
            holder.tvReplyName.setText(review.getReplyName());
            holder.tvReplyDate.setVisibility(View.GONE); // Ẩn thời gian của reply
            holder.tvReplyContent.setText(review.getReplyContent());
            holder.tvBtnReply.setVisibility(View.GONE);
        } else {
            holder.cvReplyContainer.setVisibility(View.GONE);
            holder.tvBtnReply.setVisibility(View.VISIBLE);
            holder.tvBtnReply.setOnClickListener(v -> showReplyBottomSheet(v, position));
        }

        holder.ivAvatar.setImageResource(R.drawable.bg_circle_gray);
    }

    private void showReplyBottomSheet(View view, int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
        View sheetView = LayoutInflater.from(view.getContext()).inflate(R.layout.layout_bottom_sheet_reply_review, null);
        
        EditText etReply = sheetView.findViewById(R.id.et_reply_content);
        Button btnCancel = sheetView.findViewById(R.id.btn_cancel);
        Button btnSend = sheetView.findViewById(R.id.btn_send_reply);

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        
        btnSend.setOnClickListener(v -> {
            String content = etReply.getText().toString().trim();
            if (!content.isEmpty() && onReplyListener != null) {
                ReviewModel review = reviewList.get(position);
                onReplyListener.onReply(review, position, content, bottomSheetDialog);
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvContent, tvOrderId, tvRatingScore;
        TextView tvReplyName, tvReplyDate, tvReplyContent, tvBtnReply, tvBtnViewOrder;
        RatingBar ratingBar;
        ImageView ivAvatar, ivReviewImage;
        View cvReviewImage, cvReplyContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tv_user_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvRatingScore = itemView.findViewById(R.id.tv_rating_score);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            ivReviewImage = itemView.findViewById(R.id.iv_review_image);
            cvReviewImage = itemView.findViewById(R.id.cv_review_image);
            
            cvReplyContainer = itemView.findViewById(R.id.cv_reply_container);
            tvReplyName = itemView.findViewById(R.id.tv_reply_name);
            tvReplyDate = itemView.findViewById(R.id.tv_reply_date);
            tvReplyContent = itemView.findViewById(R.id.tv_reply_content);
            tvBtnReply = itemView.findViewById(R.id.tv_btn_reply);
            tvBtnViewOrder = itemView.findViewById(R.id.tv_btn_view_order);
        }
    }
}
