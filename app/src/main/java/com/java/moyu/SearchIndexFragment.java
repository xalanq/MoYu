package com.java.moyu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.chip.Chip;

import java.util.List;

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
                User.getInstance().delSearchHistory(historyAdapter.get(position), new User.DefaultCallback() {
                    @Override
                    public void error(String msg) {
                        BasicApplication.showToast(msg);
                    }

                    @Override
                    public void ok() {

                    }
                });
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
                            User.getInstance().delAllSearchHistory(new User.DefaultCallback() {
                                @Override
                                public void error(String msg) {
                                    BasicApplication.showToast(msg);
                                }

                                @Override
                                public void ok() {

                                }
                            });
                            historyAdapter.clear();
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

        initData();
    }

    void initData() {
        User.getInstance().getHotWord(new User.StringListCallback() {
            @Override
            public void error(String msg) {
                BasicApplication.showToast(msg);
            }

            @Override
            public void ok(List<String> data) {
                hotAdapter.add(data);
            }
        });
        User.getInstance().getSearchHistory(0, Constants.SEARCH_HISTORY_LIMIT, new User.StringListCallback() {
            @Override
            public void error(String msg) {
                BasicApplication.showToast(msg);
            }

            @Override
            public void ok(List<String> data) {
                historyAdapter.add(data);
            }
        });
    }

}
