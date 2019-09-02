package com.java.moyu;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.time.LocalDateTime;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

public class IndexTabFragment extends BasicFragment {

    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    private NewsAdapter adapter;
    private String category;

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

    void initData() {
        if (category.equals(getResources().getString(R.string.recommend)))
            this.category = "香港";
        final Runnable loadMore = new Runnable() {
            LocalDateTime end_time = LocalDateTime.now();

            @Override
            public void run() {
                new NewsNetwork.Builder()
                    .add("size", "" + Constants.PAGE_SIZE)
                    .add("words", category)
                    .add("endDate", end_time.format(Constants.TIME_FORMATTER))
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
                                end_time = data.get(data.size() - 1).getPublishTime().minusSeconds(1);
                                adapter.add(data);
                                refreshLayout.finishLoadMore();
                            }
                        }
                    });
            }
        };
        new Handler().post(loadMore);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(loadMore);
            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(500);
            }
        });
    }

}
