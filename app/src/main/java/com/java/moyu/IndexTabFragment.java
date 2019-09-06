package com.java.moyu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.time.LocalDateTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

public class IndexTabFragment extends BasicFragment {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.empty_button)
    Button emptyButton;
    private NewsAdapter adapter;
    private String category;
    private String word;

    IndexTabFragment(String category) {
        this.category = category;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.news_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout),
            NewsAdapter.defaultOnclick(getActivity()));

        initData();
    }

    private void loadMore() {
        new NewsNetwork.Builder()
            .add("size", "" + Constants.PAGE_SIZE)
            .add("words", word)
            .add("categories", category)
            .add("endDate", adapter.get(adapter.getItemCount() - 1).getPublishTime().minusSeconds(1).format(Constants.TIME_FORMATTER))
            .build()
            .run(new NewsNetwork.Callback() {
                @Override
                public void timeout() {
                    refreshLayout.finishLoadMore(false);
                }

                @Override
                public void error() {
                    refreshLayout.finishLoadMore(false);
                }

                @Override
                public void ok(List<News> data) {
                    if (data.isEmpty()) {
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    } else {
                        adapter.add(data);
                        refreshLayout.finishLoadMore();
                    }
                    loadingLayout.setVisibility(View.GONE);
                }
            });
    }

    void refresh(final boolean first) {
        new NewsNetwork.Builder()
            .add("size", "" + Constants.PAGE_SIZE)
            .add("words", word)
            .add("categories", category)
            .add("endDate", LocalDateTime.now().format(Constants.TIME_FORMATTER))
            .build()
            .run(new NewsNetwork.Callback() {
                @Override
                public void timeout() {
                    refreshLayout.finishRefresh(false);
                    if (first) {
                        loadingLayout.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void error() {
                    refreshLayout.finishRefresh(false);
                    if (first) {
                        loadingLayout.setVisibility(View.GONE);
                        emptyLayout.setVisibility(View.VISIBLE);
                        refreshLayout.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void ok(List<News> data) {
                    adapter.clear();
                    adapter.add(data);
                    refreshLayout.finishRefresh();
                    if (first) {
                        loadingLayout.setVisibility(View.GONE);
                        if (data.isEmpty()) {
                            emptyLayout.setVisibility(View.VISIBLE);
                            refreshLayout.setVisibility(View.INVISIBLE);
                        } else {
                            emptyLayout.setVisibility(View.INVISIBLE);
                            refreshLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
    }

    void initData() {
        if (category.equals(getResources().getString(R.string.recommend))) {
            this.word = "香港";
            this.category = "";
        } else {
            this.word = "";
        }
        refresh(true);
        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyLayout.setVisibility(View.INVISIBLE);
                loadingLayout.setVisibility(View.VISIBLE);
                refresh(true);
            }
        });
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                loadMore();
            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refresh(false);
            }
        });
    }

}
