package com.example.uitpayapp.home.receipt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.uitpayapp.R;

import java.util.List;

public class ProviderAdapter extends ArrayAdapter<ProviderItem> {
    public ProviderAdapter(@NonNull Context context, @NonNull List<ProviderItem> providers) {
        super(context, 0, providers);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createViewFromResource(position, convertView, parent);
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_dropdown_provider, parent, false);
        }

        ProviderItem item = getItem(position);

        ImageView icon = convertView.findViewById(R.id.iv_provider_icon);
        TextView title = convertView.findViewById(R.id.tv_provider_title);
        TextView subtitle = convertView.findViewById(R.id.tv_provider_subtitle);

        if (item != null) {
            title.setText(item.getTitle());
            subtitle.setText(item.getSubtitle());
            
            if (item.getIconUrl() != null && !item.getIconUrl().isEmpty()) {
                Glide.with(getContext())
                        .load(item.getIconUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(icon);
            } else {
                icon.setImageResource(item.getIconResId());
            }
        }
        return convertView;
    }
}
