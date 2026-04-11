package com.example.uitpayapp.suggestion;// SuggestAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class SuggestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SuggestionModel> suggestionList;

    public SuggestAdapter(List<SuggestionModel> list) {
        this.suggestionList = list;
    }

    @Override
    public int getItemViewType(int position) {
        return suggestionList.get(position).getViewType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SuggestionModel.TYPE_HORIZONTAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion_horizontal, parent, false);
            return new HorizontalViewHolder(view);
        } else { // TYPE_VERTICAL
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion_vertical, parent, false);
            return new VerticalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SuggestionModel model = suggestionList.get(position);

        if (holder.getItemViewType() == SuggestionModel.TYPE_HORIZONTAL) {
            HorizontalViewHolder horizontalHolder = (HorizontalViewHolder) holder;
            horizontalHolder.img.setImageResource(model.getImageResId());
            horizontalHolder.title.setText(model.getTitle());
            horizontalHolder.subtitle.setText(model.getSubtitle());
        } else {
            VerticalViewHolder verticalHolder = (VerticalViewHolder) holder;
            verticalHolder.img.setImageResource(model.getImageResId());
            verticalHolder.title.setText(model.getTitle());
            // verticalHolder.subtitle.setText(...) nếu bạn có dùng
        }
    }

    @Override
    public int getItemCount() {
        return suggestionList == null ? 0 : suggestionList.size();
    }

    // ---------------- ViewHolder cho Item Ngang ----------------
    class HorizontalViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, subtitle;

        public HorizontalViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ivSuggestImageHorz);
            title = itemView.findViewById(R.id.tvSuggestTitleHorz);
            subtitle = itemView.findViewById(R.id.tvSuggestSubTitleHorz);
        }
    }

    // ---------------- ViewHolder cho Item Dọc ----------------
    class VerticalViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title;

        public VerticalViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.ivSuggestImageVert);
            title = itemView.findViewById(R.id.tvSuggestTitleVert);
        }
    }
}