package com.java.moyu;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

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
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.favorite_toolbar)
    Toolbar toolbar;

    private NewsAdapter adapter;
    private int offset;

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

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout),
            NewsAdapter.defaultOnclick(getActivity()));

        initData();
    }

    void loadMore(boolean first) {
        if (first)
            offset = 0;
        List<News> data = NewsDatabase.getInstance().queryFavorList(offset, Constants.PAGE_SIZE);
        if (data.isEmpty()) {
            refreshLayout.finishLoadMoreWithNoMoreData();
        } else {
            offset += data.size();
            adapter.add(data);
            refreshLayout.finishLoadMore();
        }
        if (first) {
            loadingLayout.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
        }
    }

    void initData() {
        if (adapter == null)
            return;
        adapter.clear();
        loadMore(true);
        refreshLayout.resetNoMoreData();
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMore(false);
            }
        });
    }

}
