package com.example.uitpayapp.home.home_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;
import com.example.uitpayapp.home.home_models.CartItem;
import com.example.uitpayapp.home.home_models.CartTopping;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface CartActionListener {
        void onQuantityChanged();
        void onRequestRemoveItem(int position, CartItem item);
        void onEditItemClick(int position, CartItem item);
    }

    private final List<CartItem> cartItems;
    private final CartActionListener listener;
    private final NumberFormat formatter;

    public CartAdapter(List<CartItem> cartItems, CartActionListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
        this.formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        holder.imgItem.setImageResource(item.getMenuItem().getImageResId());
        holder.tvName.setText(item.getMenuItem().getName());
        
        // Show total price of one single item including toppings (or wait, the price per item including toppings, or just base price?)
        // Total price of an item is item price + toppings. The display in cart usually shows (base + toppings) * 1.
        long unitPrice = item.getTotalPrice() / item.getQuantity();
        holder.tvPrice.setText(formatter.format(unitPrice) + "đ");
        
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        if (item.getSelectedToppings() != null && !item.getSelectedToppings().isEmpty()) {
            holder.tvToppings.setVisibility(View.VISIBLE);
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < item.getSelectedToppings().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append("+ ").append(item.getSelectedToppings().get(i).getName());
            }
            holder.tvToppings.setText(sb.toString());
        } else {
            holder.tvToppings.setVisibility(View.GONE);
        }

        View.OnClickListener editListener = v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listener.onEditItemClick(pos, cartItems.get(pos));
            }
        };

        holder.tvEditToppings.setOnClickListener(editListener);
        holder.imgItem.setOnClickListener(editListener);
        holder.tvName.setOnClickListener(editListener);

        holder.btnIncrease.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            CartItem ci = cartItems.get(pos);
            ci.setQuantity(ci.getQuantity() + 1);
            notifyItemChanged(pos);
            listener.onQuantityChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            CartItem ci = cartItems.get(pos);
            if (ci.getQuantity() > 1) {
                ci.setQuantity(ci.getQuantity() - 1);
                notifyItemChanged(pos);
                listener.onQuantityChanged();
            } else {
                // Số lượng sẽ về 0 → yêu cầu xác nhận xóa
                listener.onRequestRemoveItem(pos, ci);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < cartItems.size()) {
            cartItems.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
        }
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView imgItem;
        TextView tvName, tvPrice, tvToppings, tvEditToppings, tvQuantity, btnIncrease, btnDecrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgItem = itemView.findViewById(R.id.img_cart_item);
            tvName = itemView.findViewById(R.id.tv_cart_item_name);
            tvPrice = itemView.findViewById(R.id.tv_cart_item_price);
            tvToppings = itemView.findViewById(R.id.tv_cart_item_toppings);
            tvEditToppings = itemView.findViewById(R.id.tv_edit_toppings);
            tvQuantity = itemView.findViewById(R.id.tv_cart_item_quantity);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
        }
    }
}
