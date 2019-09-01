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
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

/**
 * 收藏碎片
 */
public class FavoriteFragment extends BasicFragment {

    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    @BindView(R.id.favorite_toolbar)
    Toolbar toolbar;

    private NewsAdapter adapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.favorite_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MainActivity a = (MainActivity) getActivity();

        a.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            a, a.drawerLayout, toolbar, R.string.main_navigation_drawer_open, R.string.main_navigation_drawer_close);
        a.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        a.getSupportActionBar().setDisplayShowTitleEnabled(false);

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout), new NewsAdapter.OnClick() {
            @Override
            public void click(View view, int position) {
                Toast.makeText(getContext(), "click favorite news", Toast.LENGTH_SHORT).show();
            }
        });

        test();
    }

    private void test() {
        final NewsDatabase db = new NewsDatabase(getActivity(), Constants.DB_NAME, null, Constants.DB_VERSION);
        final Runnable loadMore = new Runnable() {
            int page = 0;
            @Override
            public void run() {
                List<News> data = db.queryFavour(this.page * 5, 5);
                this.page++;
//                for (int i = 0; i < 10; ++i) {
//                    News news = new News();
//                    news.title = String.format("收藏 %d 啊", i);
//                    news.publisher = String.format("第%d号", i);
//                    news.publishTime = LocalDateTime.now().minusMinutes(i * i * i * i * 30);
//                    data.add(news);
//                }
                adapter.add(data);
                refreshLayout.finishLoadMore();
            }
        };
        loadMore.run();
        refreshLayout.setEnableRefresh(false);
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
