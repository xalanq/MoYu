package com.java.moyu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

class SearchActivity extends BasicActivity {

    @BindView(R.id.search_toolbar) Toolbar toolbar;
    @BindView(R.id.search_box) EditText searchBox;
    @BindView(R.id.search_edit_history) TextView editHistory;
    @BindView(R.id.search_clear_history) TextView clearHistory;

    private ChipAdapter hotAdapter;
    private ChipAdapter historyAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.search_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        searchBox.requestFocus();

        hotAdapter = ChipAdapter.newAdapter(this, (RecyclerView) findViewById(R.id.search_hot_layout),
            new ChipAdapter.OnClick() {
                @Override
                public void click(Chip chip, int position) {
                    Toast.makeText(SearchActivity.this, "click hot", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void close(Chip chip, int position) {

                }
            });
        historyAdapter = ChipAdapter.newAdapter(this, (RecyclerView) findViewById(R.id.search_history_layout),
            new ChipAdapter.OnClick() {
                @Override
                public void click(Chip chip, int position) {
                    Toast.makeText(SearchActivity.this, "click history", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void close(Chip chip, int position) {
                    historyAdapter.remove(position);
                }
            });

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

    private void test() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> a = new ArrayList<>();
                for (int i = 0; i < 9; ++i) {
                    a.add(String.format("热搜%d", new Random().nextInt() % 999));
                }
                hotAdapter.add(a);
                a = new ArrayList<>();
                for (int i = 0; i < 100; ++i) {
                    a.add(String.format("记录%d", new Random().nextInt() % 999));
                }
                historyAdapter.add(a);
            }
        }, 10);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
