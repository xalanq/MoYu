package com.java.moyu;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

public class IndexTabFragment extends BasicFragment {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    private NewsAdapter adapter;
    private String category;
    private Handler handler;
    private List<News> recommendData;
    private Integer recommendRemain;

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
        handler = new Handler();

        initData();
    }

    private void dealRecommendData(final Runnable runnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (recommendRemain > 0);

                Set<News> set = new HashSet<>(recommendData);
                List<News> tmp = (new ArrayList<>(set)).subList(0, Constants.PAGE_SIZE);
                recommendData = new ArrayList<>(tmp);

                Collections.sort(recommendData, new Comparator<News>(){
                    public int compare(News arg0, News arg1) {
                        return arg1.getTime().compareTo(arg0.getTime());
                    }
                });

                handler.post(runnable);
            }
        }).start();
    }

    private void loadMore() {
        if (category.equals(getResources().getString(R.string.recommend))) {
            // TODO Test
//            List<String> tags = NewsDatabase.getInstance().getTags(Constants.RECOMMEND_TAGS_SIZE);
            List<String> tags = Arrays.asList("香港");
            recommendData = new ArrayList<>();
            recommendRemain = tags.size();
            for (String tag: tags) {
                new NewsNetwork.Builder()
                    .add("size", "" + Constants.PAGE_SIZE)
                    .add("words", tag)
                    .add("endDate", adapter.get(adapter.getItemCount() - 1).getPublishTime().minusSeconds(1).format(Constants.TIME_FORMATTER))
                    .build()
                    .run(new NewsNetwork.Callback() {
                        @Override
                        public void timeout() { recommendRemain ++; }

                        @Override
                        public void error() { recommendRemain --; }

                        @Override
                        public void ok(List<News> data) {
                            recommendData.addAll(data);
                            recommendRemain --;
                        }
                    });
            }
            dealRecommendData(new Runnable(){
                @Override
                public void run() {
                    if (recommendData.isEmpty()) {
                        refreshLayout.finishLoadMoreWithNoMoreData();
                    } else {
                        adapter.add(recommendData);
                        refreshLayout.finishLoadMore();
                    }
                    loadingLayout.setVisibility(View.GONE);
                }
            });

        } else {
            new NewsNetwork.Builder()
                .add("size", "" + Constants.PAGE_SIZE)
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
    }

    void refresh(final boolean first) {
        if (category.equals(getResources().getString(R.string.recommend))) {
            // TODO Test
//            List<String> tags = NewsDatabase.getInstance().getTags(Constants.RECOMMEND_TAGS_SIZE);
            List<String> tags = Arrays.asList("香港");
            recommendData = new ArrayList<>();
            recommendRemain = tags.size();
            for (String tag: tags) {
                new NewsNetwork.Builder()
                    .add("size", "" + Constants.PAGE_SIZE)
                    .add("words", tag)
                    .add("endDate", LocalDateTime.now().format(Constants.TIME_FORMATTER))
                    .build()
                    .run(new NewsNetwork.Callback() {
                        @Override
                        public void timeout() { recommendRemain ++; }

                        @Override
                        public void error() { recommendRemain --; }

                        @Override
                        public void ok(List<News> data) {
                            recommendData.addAll(data);
                            recommendRemain --;
                        }
                    });
            }

            dealRecommendData(new Runnable(){
                @Override
                public void run() {
                    adapter.clear();
                    adapter.add(recommendData);
                    refreshLayout.finishRefresh();
                    if (first) {
                        loadingLayout.setVisibility(View.GONE);
                        refreshLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            new NewsNetwork.Builder()
                .add("size", "" + Constants.PAGE_SIZE)
                .add("categories", category)
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
    }

    void initData() {
        refresh(true);
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
