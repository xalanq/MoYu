package com.java.moyu;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ViewHolder> {

    private List<String> data;
    private boolean isEdit;
    private OnClick onClick;

    private ChipAdapter(OnClick onClick) {
        data = new ArrayList<>();
        isEdit = false;
        this.onClick = onClick;
    }

    static ChipAdapter newAdapter(Context context, View view, OnClick onClick) {
        ChipAdapter adapter = new ChipAdapter(onClick);
        ChipsLayoutManager.Builder c = ChipsLayoutManager.newBuilder(context);
        RecyclerView rv = (RecyclerView) view;
        rv.setLayoutManager(c.build());
        rv.setAdapter(adapter);
        rv.addItemDecoration(new SpacingItemDecoration(20, 20));
        return adapter;
    }

    String get(int position) {
        return data.get(position);
    }

    void add(String s, int position) {
        data.add(position, s);
        notifyItemInserted(position);
    }

    void add(String s) {
        add(s, data.size());
    }

    void add(List<String> data, int position) {
        this.data.addAll(position, data);
        notifyItemRangeInserted(position, data.size());
    }

    void add(List<String> data) {
        add(data, this.data.size());
    }

    void remove(int position) {
        data.remove(position);
        notifyItemRemoved(position);
    }

    void clear() {
        int sz = data.size();
        data.clear();
        notifyItemRangeRemoved(0, sz);
    }

    boolean toggleEdit() {
        isEdit = !isEdit;
        notifyItemRangeChanged(0, data.size());
        return isEdit;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Chip view = new Chip(new ContextThemeWrapper(parent.getContext(), R.style.Theme_MaterialComponents_Light));
        view.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
        view.setChipCornerRadius(10);
        return new ViewHolder(view, onClick);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Chip chip = holder.chip;
        chip.setText(data.get(position));
        chip.setCloseIconVisible(isEdit);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnClick {

        void click(Chip chip, int position);

        void close(Chip chip, int position);

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        Chip chip;

        ViewHolder(View itemView, final OnClick onClick) {
            super(itemView);
            chip = (Chip) itemView;
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.click((Chip) view, getAdapterPosition());
                }
            });
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClick.close((Chip) view, getAdapterPosition());
                }
            });
        }

    }

}
