package com.java.moyu;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.empty_button)
    Button emptyButton;
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

    private void loadMore() {
        if (category.equals(getResources().getString(R.string.recommend))) {
            new GetRecommendTask(Constants.RECOMMEND_TAGS_SIZE, LocalDateTime.now(), new GetRecommendTask.Callback() {
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
                    }
                });
        }
    }

    void refresh(final boolean first) {
        if (category.equals(getResources().getString(R.string.recommend))) {
            new GetRecommendTask(Constants.RECOMMEND_TAGS_SIZE, LocalDateTime.now(), new GetRecommendTask.Callback() {
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
    }

    void initData() {
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

    private static class GetRecommendTask extends AsyncTask<Void, Void, List<News>> {

        List<String> tags;
        List<News> recommendData;
        String endDate;
        Callback callback;
        Integer recommendRemain;

        GetRecommendTask(int limit, LocalDateTime endDate, final Callback callback) {
            this.endDate = endDate.format(Constants.TIME_FORMATTER);
            this.callback = callback;
            User.getInstance().getTopTag(limit, new User.StringListCallback() {
                @Override
                public void error(String msg) {
                    BasicApplication.showToast(msg);
                    callback.ok(new ArrayList<News>());
                }

                @Override
                public void ok(List<String> data) {
                    tags = data;
                    if (tags.size() == 0) {
                        tags = Arrays.asList(Constants.category);
                    }
                    recommendData = new ArrayList<>();
                    recommendRemain = tags.size();
                    for (String tag : tags) {
                        new NewsNetwork.Builder()
                            .add("size", "" + Constants.PAGE_SIZE)
                            .add("words", tag)
                            .add("endDate", GetRecommendTask.this.endDate)
                            .build()
                            .run(new NewsNetwork.Callback() {
                                @Override
                                public void timeout() {
                                    recommendRemain--;
                                }

                                @Override
                                public void error() {
                                    recommendRemain--;
                                }

                                @Override
                                public void ok(List<News> data) {
                                    recommendData.addAll(data);
                                    recommendRemain--;
                                }
                            });
                    }
                    execute();
                }
            });
        }

        @Override
        protected List<News> doInBackground(Void... voids) {
            while (recommendRemain > 0) ;

            Set<News> set = new HashSet<>(recommendData);
            List<News> tmp = (new ArrayList<>(set)).subList(0, Math.min(Constants.PAGE_SIZE, recommendData.size()));

            Collections.sort(tmp, new Comparator<News>() {
                public int compare(News arg0, News arg1) {
                    return arg1.getTime().compareTo(arg0.getTime());
                }
            });

            return tmp;
        }

        @Override
        protected void onPostExecute(List<News> data) {
            callback.ok(data);
        }

        public interface Callback {

            void ok(List<News> data);

        }

    }

}
