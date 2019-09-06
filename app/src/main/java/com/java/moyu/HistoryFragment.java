package com.java.moyu;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.util.List;

import butterknife.BindView;

/**
 * 历史碎片
 */
public class HistoryFragment extends BasicFragment {

    @BindView(R.id.refresh_layout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;
    @BindView(R.id.empty_layout)
    LinearLayout emptyLayout;
    @BindView(R.id.empty_button)
    Button emptyButton;
    @BindView(R.id.history_toolbar)
    Toolbar toolbar;

    private NewsAdapter adapter;
    private int offset;

    @Override
    protected int getLayoutResource() {
        return R.layout.history_fragment;
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

    void loadMore(final boolean first) {
        if (first)
            offset = 0;
        User.getInstance().getHistory(offset, Constants.PAGE_SIZE, new User.NewsCallback() {
            @Override
            public void error(String msg) {
                BasicApplication.showToast(msg);
            }

            @Override
            public void ok(List<News> newsList) {
                if (newsList.isEmpty()) {
                    refreshLayout.finishLoadMoreWithNoMoreData();
                } else {
                    offset += newsList.size();
                    adapter.add(newsList);
                    refreshLayout.finishLoadMore();
                }
                if (first) {
                    loadingLayout.setVisibility(View.GONE);
                    if (newsList.isEmpty()) {
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
        if (!isAdded())
            return;
        adapter.clear();
        loadMore(true);
        emptyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emptyLayout.setVisibility(View.INVISIBLE);
                loadingLayout.setVisibility(View.VISIBLE);
                loadMore(true);
            }
        });
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

    void refreshUI() {
        Resources r = getResources();
        Resources.Theme theme = getActivity().getTheme();
        TypedValue colorPrimary = new TypedValue();
        TypedValue colorPrimaryDark = new TypedValue();
        TypedValue colorAccent = new TypedValue();
        TypedValue colorTitle = new TypedValue();
        TypedValue colorSubtitle = new TypedValue();
        TypedValue colorText = new TypedValue();
        TypedValue colorBackground = new TypedValue();
        TypedValue colorTabRipple = new TypedValue();
        TypedValue colorTopBackground = new TypedValue();
        TypedValue colorTabSelectedText = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        theme.resolveAttribute(R.attr.colorPrimaryDark, colorPrimaryDark, true);
        theme.resolveAttribute(R.attr.colorAccent, colorAccent, true);
        theme.resolveAttribute(R.attr.colorTitle, colorTitle, true);
        theme.resolveAttribute(R.attr.colorSubtitle, colorSubtitle, true);
        theme.resolveAttribute(R.attr.colorText, colorText, true);
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true);
        theme.resolveAttribute(R.attr.colorTabRipple, colorTabRipple, true);
        theme.resolveAttribute(R.attr.colorTopBackground, colorTopBackground, true);
        theme.resolveAttribute(R.attr.colorTabSelectedText, colorTabSelectedText, true);

    }

}
