package com.example.uitpayapp;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ProfileMenuAdapter extends RecyclerView.Adapter<ProfileMenuAdapter.ViewHolder> {
    private List<ProfileActivity.GroupItemData> ListGroupItem;
    private Boolean IsAmountHiden=true;
    private OnProfileMenuItemClickListener Listener;

    //bat click
    public interface OnProfileMenuItemClickListener {
        //khi nhan click ben kia phai truyen vao 1 ham xu ly
        void onMenuItemClick(ProfileActivity.MenuItemData item);
    }
    public ProfileMenuAdapter(ProfileActivity profileActivity, List<ProfileActivity.GroupItemData> ListGroupItem, OnProfileMenuItemClickListener Listener) {
        this.ListGroupItem = ListGroupItem;
        this.Listener=Listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View cardviewGroupItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item_profile_layout,parent,false);
        return new ViewHolder(cardviewGroupItem);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProfileActivity.GroupItemData GroupItem = ListGroupItem.get(position);
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
        for (ProfileActivity.MenuItemData MenuItem : GroupItem.getListItems())
        {
            View itemView;
            if (!MenuItem.IsSpecialItem)
            {
                itemView=LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.menuitem_profile_screen,null);
                ProfileActivity.SetDetaileMenuItem(itemView,MenuItem.getTitle(),MenuItem.getSubtitle(),MenuItem.getIcon());
            } else
            {
                itemView=LayoutInflater.from(holder.itemView.getContext()).inflate(R.layout.item_profile_wallet,null);
                View title_balace=itemView.findViewById(R.id.balance_title);
                ProfileActivity.SetDetaileMenuItem(title_balace,MenuItem.getTitle(),MenuItem.getSubtitle(),MenuItem.getIcon());
                TextView wallet_balance=itemView.findViewById(R.id.wallet_balance);
                TextView accmulated_balance=itemView.findViewById(R.id.accmulated_balance);
                ImageView hide_show_amount=itemView.findViewById(R.id.hide_show_amount);
                //sau nay goi api thi co kha nang goi o day
                wallet_balance.setText("0đ");
                accmulated_balance.setText("0đ");
                hide_show_amount.setOnClickListener(v->
                {
                    IsAmountHiden=!IsAmountHiden;
                    if (IsAmountHiden)
                    {
                        wallet_balance.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        accmulated_balance.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        hide_show_amount.setImageResource(R.drawable.ic_eye);
                    } else
                    {
                        wallet_balance.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        accmulated_balance.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        hide_show_amount.setImageResource(R.drawable.ic_invisible_eye);
                    }
                });
            }
            itemView.setOnClickListener(v->
            {
                if (Listener!=null) {
                    Listener.onMenuItemClick(MenuItem);
                }
            });
            holder.items_container.addView(itemView);
        }
    }
    @Override
    public int getItemCount() {
        return ListGroupItem.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView group_title;
        LinearLayout items_container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_title = itemView.findViewById(R.id.group_title);
            items_container=itemView.findViewById(R.id.items_container);
        }
    }
}
