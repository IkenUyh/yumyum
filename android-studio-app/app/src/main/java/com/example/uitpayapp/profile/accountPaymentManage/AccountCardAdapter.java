package com.example.uitpayapp.profile.accountPaymentManage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.uitpayapp.R;

import java.util.List;

public class AccountCardAdapter extends RecyclerView.Adapter<AccountCardAdapter.CardViewHolder> {

    private List<AccountCardModel> cardList;

    public AccountCardAdapter(List<AccountCardModel> cardList) {
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_card_demo, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        AccountCardModel model = cardList.get(position);
        AccountCardModel.AccountType type = model.getType();

        holder.itemView.findViewById(R.id.card_container).setBackgroundColor(type.getBgColor());
        
        holder.tvTitle.setText(model.getTitle());
        holder.tvSubTitle.setText(model.getSubTitle());
        holder.tvAction.setText(model.getActionText());
    }

    @Override
    public int getItemCount() {
        return cardList != null ? cardList.size() : 0;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvSubTitle, tvAction;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_card_title);
            tvSubTitle = itemView.findViewById(R.id.tv_card_subtitle);
            tvAction = itemView.findViewById(R.id.tv_card_action);
        }
    }
}
