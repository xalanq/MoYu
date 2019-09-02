package com.java.moyu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

public class SearchIndexFragment extends BasicFragment {

    @BindView(R.id.search_edit_history)
    TextView editHistory;
    @BindView(R.id.search_clear_history)
    TextView clearHistory;

    private ChipAdapter hotAdapter;
    private ChipAdapter historyAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.search_index_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final SearchActivity a = (SearchActivity) getActivity();

        hotAdapter = ChipAdapter.newAdapter(getContext(), view.findViewById(R.id.search_hot_layout), new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                String text = hotAdapter.get(position);
                historyAdapter.add(text);
                a.searchText(text);
            }

            @Override
            public void close(Chip chip, int position) {

            }
        });

        historyAdapter = ChipAdapter.newAdapter(getContext(), view.findViewById(R.id.search_history_layout), new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                String text = historyAdapter.get(position);
                historyAdapter.remove(position);
                historyAdapter.add(text, 0);
                a.searchText(text);
            }

            @Override
            public void close(Chip chip, int position) {
                NewsDatabase.getInstance().delSearchHistory(historyAdapter.get(position));
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
                new AlertDialog.Builder(getContext()).setTitle(R.string.search_clear_history)
                    .setMessage(getResources().getString(R.string.search_clear_history_confirm))
                    .setPositiveButton(getResources().getText(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            historyAdapter.clear();
                            NewsDatabase.getInstance().delAllSearchHistory();
                        }
                    })
                    .setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
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
                historyAdapter.add(NewsDatabase.getInstance().querySearchHistory(Constants.SEARCH_HISTORY_LIMIT));
            }
        }, 10);
    }

}
