package com.example.uitpayapp.home.home_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uitpayapp.R;

import java.util.List;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder> {

    private List<Integer> imageList;

    public ImageSliderAdapter(List<Integer> imageList) {
        this.imageList = imageList;
    }

    //Tao 1 trang moi
    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_slider, parent, false);
        //xml->view->layout(co san)->view hoan chinh
        return new SliderViewHolder(view);
    }
    //gan anh vao tung trang
    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        holder.imageView.setImageResource(imageList.get(position));
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
        }
    }
}