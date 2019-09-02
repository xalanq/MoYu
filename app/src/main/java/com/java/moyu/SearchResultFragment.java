package com.java.moyu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.time.LocalDateTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

public class SearchResultFragment extends BasicFragment {

    private final String text;
    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    private NewsAdapter adapter;

    public SearchResultFragment(String text) {
        this.text = text;
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

        test();
    }

    void refresh(final boolean first) {
        new NewsNetwork.Builder()
            .add("size", "" + Constants.PAGE_SIZE)
            .add("words", text)
            .add("endDate", LocalDateTime.now().format(Constants.TIME_FORMATTER))
            .build()
            .run(new NewsNetwork.Callback() {
                @Override
                public void timeout() {
                    refreshLayout.finishRefresh(false);
                    if (first) {
                        loadingLayout.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void error() {
                    refreshLayout.finishRefresh(false);
                    if (first) {
                        loadingLayout.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void ok(List<News> data) {
                    adapter.clear();
                    adapter.add(data);
                    refreshLayout.finishRefresh();
                    if (first) {
                        loadingLayout.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
    }

    void loadMore() {
        new NewsNetwork.Builder()
            .add("size", "" + Constants.PAGE_SIZE)
            .add("words", text)
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
                }
            });
    }

    private void test() {
        refresh(true);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
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
