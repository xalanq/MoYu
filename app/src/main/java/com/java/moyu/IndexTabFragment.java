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

    void initData() {
        if (category.equals(getResources().getString(R.string.recommend))) {
            this.word = "香港";
            this.category = "";
        } else {
            this.word = "";
        }

        final Runnable loadMore = new Runnable() {
            @Override
            public void run() {
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
                        }
                    });
            }
        };

        final Runnable refresh = new Runnable() {
            @Override
            public void run() {
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
                        }

                        @Override
                        public void error() {
                            refreshLayout.finishRefresh(false);
                        }

                        @Override
                        public void ok(List<News> data) {
                            adapter.clear();
                            adapter.add(data);
                            refreshLayout.finishRefresh();
                        }
                    });
            }
        };

        new Handler().post(refresh);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(loadMore);
            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(refresh);
            }
        });
    }

}
