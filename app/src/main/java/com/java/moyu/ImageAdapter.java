package com.java.moyu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    private List<String> data;
    private final Context context;

    public ImageAdapter(Context context, List<String> data) {
        this.data = data;
        this.context = context.getApplicationContext();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        Glide.with(context).load(data.get(position))
            .placeholder(R.drawable.loading_cover)
            .error(R.drawable.error).centerCrop()
            .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView imageView;

        ViewHolder(final View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }

    }

}
