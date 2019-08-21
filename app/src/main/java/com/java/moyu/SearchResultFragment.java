package com.java.moyu;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

public class SearchResultFragment extends BasicFragment {

    @BindView(R.id.refresh_layout) RefreshLayout refreshLayout;

    private NewsAdapter adapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.news_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout), new NewsAdapter.OnClick() {
            @Override
            public void click(View view, int position) {
                Toast.makeText(getContext(), "click result news", Toast.LENGTH_SHORT).show();
            }
        });

        test();
    }

    private void test() {
        final Runnable loadMore = new Runnable() {
            @Override
            public void run() {
                List<News> data = new ArrayList<>();
                for (int i = 0; i < 10; ++i) {
                    News news = new News();
                    news.title = String.format("搜索结果 %d 啊", i);
                    news.publisher = String.format("第%d号", i);
                    news.publishTime = LocalDateTime.now().minusMinutes(i * i * i * i * 30);
                    data.add(news);
                }
                adapter.add(data);
                refreshLayout.finishLoadMore();
            }
        };
        loadMore.run();
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(loadMore, 500);
            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(500);
            }
        });
    }

}
