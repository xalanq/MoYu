package com.java.moyu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.beloo.widget.chipslayoutmanager.ChipsLayoutManager;
import com.beloo.widget.chipslayoutmanager.SpacingItemDecoration;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SearchActivity extends BasicActivity {

    @BindView(R.id.search_toolbar) Toolbar toolbar;
    @BindView(R.id.search_box) EditText searchBox;
    @BindView(R.id.search_edit_history) TextView editHistory;
    @BindView(R.id.search_clear_history) TextView clearHistory;

    ChipAdapter hotAdapter;
    ChipAdapter historyAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.search_activity;
    }

    ChipAdapter setAdapter(int id) {
        ChipAdapter adapter = new ChipAdapter();
        RecyclerView rv = findViewById(id);
        rv.setLayoutManager(ChipsLayoutManager.newBuilder(this).build());
        rv.setAdapter(adapter);
        rv.addItemDecoration(new SpacingItemDecoration(20, 20));
        return adapter;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        searchBox.requestFocus();

        hotAdapter = setAdapter(R.id.search_hot_layout);
        historyAdapter = setAdapter(R.id.search_history_layout);

        editHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (historyAdapter.toggleEdit()) {
                    editHistory.setText(getResources().getText(R.string.complete));
                } else {
                    editHistory.setText(getResources().getText(R.string.edit));
                }
            }
        });
        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(SearchActivity.this)
                    .setMessage(getResources().getString(R.string.search_clear_history_confirm))
                    .setPositiveButton(getResources().getText(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            historyAdapter.clear();
                        }
                    })
                    .setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
            }
        });
        test();
    }

    void test() {
        for (int i = 0; i < 9; ++i) {
            hotAdapter.add(String.format("热搜%d", new Random().nextInt() % 100));
        }
        for (int i = 0; i < 10; ++i) {
            historyAdapter.add(String.format("记录%d", new Random().nextInt() % 100));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class ChipAdapter extends RecyclerView.Adapter<ChipAdapter.ViewHolder> {

        List<String> data;
        boolean isEdit;

        ChipAdapter() {
            data = new ArrayList<>();
            isEdit = false;
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

        public static class ViewHolder extends RecyclerView.ViewHolder {

            Chip chip;

            ViewHolder(View itemView) {
                super(itemView);
                chip = (Chip)itemView;
            }

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Chip view = new Chip(new ContextThemeWrapper(parent.getContext(), R.style.Theme_MaterialComponents_Light));
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LOCALE);
            final ViewHolder holder = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BasicApplication.getContext(), "Click Item", Toast.LENGTH_SHORT).show();
                }
            });
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            Chip chip = holder.chip;
            chip.setText(data.get(position));
            chip.setCloseIconVisible(isEdit);
            if (isEdit) {
                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        remove(position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
