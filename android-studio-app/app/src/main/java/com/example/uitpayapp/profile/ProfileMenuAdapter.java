package com.example.uitpayapp.profile;

import android.content.Context;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class ProfileMenuAdapter extends RecyclerView.Adapter<ProfileMenuAdapter.MenuItemViewHolder> {
    private List<GroupItemData> ListGroupItem;
    private Boolean IsAmountHiden=true;
    private OnProfileMenuItemClickListener Listener;

    //bat click
    public interface OnProfileMenuItemClickListener {
        //khi nhan click ben kia phai truyen vao 1 ham xu ly
        void onMenuItemClick(MenuItemData item);
    }
    public ProfileMenuAdapter(Context context, List<GroupItemData> ListGroupItem, OnProfileMenuItemClickListener Listener) {
        this.ListGroupItem = ListGroupItem;
        this.Listener=Listener;
    }
    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardviewGroupItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_profile_layout,parent,false);
        return new MenuItemViewHolder(cardviewGroupItem);
    }
    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        GroupItemData GroupItem = ListGroupItem.get(position);
        if (GroupItem.getTitle()!="")
        {
            holder.group_title.setText(GroupItem.getTitle());
            holder.group_title.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.group_title.setVisibility(View.GONE);
        }
        holder.items_container.removeAllViews();
        for (MenuItemData MenuItem : GroupItem.getListItems())
        {
            View itemView;
            if (!MenuItem.IsSpecialItem)
            {
                itemView=LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.menuitem_profile_screen,null);
                ProfileActivity.SetDetailMenuItem(itemView,MenuItem.getTitle(),MenuItem.getSubtitle(),MenuItem.getIcon());
                itemView.setOnClickListener(v->
                {
                    if (Listener!=null) {
                        Listener.onMenuItemClick(MenuItem);
                    }
                });
            } else
            {
                itemView=LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_profile_wallet,null);
                View title_balace=itemView.findViewById(R.id.balance_title);
                ProfileActivity.SetDetailMenuItem(title_balace,MenuItem.getTitle(),MenuItem.getSubtitle(),MenuItem.getIcon());
                
                ImageView arrow = title_balace.findViewById(R.id.menu_less_than);
                if (arrow != null) {
                    arrow.setVisibility(View.GONE);
                }
                
                LinearLayout layoutBalances = itemView.findViewById(R.id.layout_balances);

                // Add Personal Wallet
                View personalWalletView = LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_wallet_entry, layoutBalances, false);
                TextView tvPersonalName = personalWalletView.findViewById(R.id.tv_wallet_name);
                TextView btnPersonalAction = personalWalletView.findViewById(R.id.btn_action);
                TextView tvPersonalBalance = personalWalletView.findViewById(R.id.wallet_balance);
                
                final boolean[] isPersonalHidden = {true};
                
                tvPersonalName.setText("Ví");
                btnPersonalAction.setText("Nạp +");
                btnPersonalAction.setOnClickListener(v -> {
                    android.content.Context ctx = holder.itemView.getContext();
                    if (ctx instanceof ProfileActivity) {
                        ((ProfileActivity) ctx).showTopUpDialog();
                    }
                });
                tvPersonalBalance.setOnClickListener(v -> {
                    isPersonalHidden[0] = !isPersonalHidden[0];
                    if (isPersonalHidden[0]) {
                        tvPersonalBalance.setText("***");
                    } else {
                        if (tvPersonalBalance.getTag() != null) {
                            tvPersonalBalance.setText((String)tvPersonalBalance.getTag());
                        } else {
                            tvPersonalBalance.setText("Lỗi tải");
                        }
                    }
                });
                
                com.example.uitpayapp.modules.wallet.WalletRepository walletRepo = new com.example.uitpayapp.modules.wallet.WalletRepository();
                walletRepo.getBalance(new com.example.uitpayapp.network.ApiCallback<com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse>() {
                    @Override
                    public void onSuccess(com.example.uitpayapp.modules.wallet.models.responses.BalanceResponse data) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            java.text.NumberFormat format = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
                            if (tvPersonalBalance != null) {
                                tvPersonalBalance.setTag(format.format(data.getBalance()) + "đ");
                                if (!isPersonalHidden[0]) {
                                    tvPersonalBalance.setText((String)tvPersonalBalance.getTag());
                                } else {
                                    tvPersonalBalance.setText("***");
                                }
                            }
                        });
                    }
                    @Override
                    public void onError(String errorMessage) {
                        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
                            if (tvPersonalBalance != null) {
                                tvPersonalBalance.setText("Lỗi tải");
                            }
                        });
                    }
                });
                layoutBalances.addView(personalWalletView);




            }
            holder.items_container.addView(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return ListGroupItem.size();
    }
    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        TextView group_title;
        LinearLayout items_container;
        public MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            group_title = itemView.findViewById(R.id.group_title);
            items_container=itemView.findViewById(R.id.items_container);
        }
    }
}
