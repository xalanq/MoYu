package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
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
 * 首页碎片
 */
class IndexFragment extends BasicFragment {

    private NewsAdapter adapter;
    @BindView(R.id.index_search_box) EditText searchBox;
    @BindView(R.id.index_tab_layout) TabLayout tabLayout;
    @BindView(R.id.index_refresh_layout) RefreshLayout refreshLayout;
    @BindView(R.id.index_more_button) ImageButton btnMore;

    @Override
    protected int getLayoutResource() {
        return R.layout.index_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MainActivity a = (MainActivity)getActivity();

        Toolbar toolbar = view.findViewById(R.id.index_toolbar);
        a.setSupportActionBar(toolbar);
        a.getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                a, a.drawerLayout, toolbar, R.string.main_navigation_drawer_open, R.string.main_navigation_drawer_close);
        a.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        searchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });

        btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                a.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_up_in, R.anim.slide_stay, R.anim.slide_stay, R.anim.slide_down_out)
                    .add(R.id.main_layout, a.fragmentAllocator.getCategoryFragment())
                    .hide(IndexFragment.this)
                    .addToBackStack(null)
                    .commit();
            }
        });

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.index_fragment_layout), new NewsAdapter.OnClick() {
            @Override
            public void click(View view, int position) {
                Toast.makeText(getContext(), "click news", Toast.LENGTH_SHORT).show();
            }
        });

        test();
    }

    private void test() {
        List<News> data = new ArrayList<>();
        for (int i = 0; i < 10; ++i) {
            News news = new News();
            news.title = String.format("这是标题 %d 啊", i);
            news.publisher = String.format("第%d号", i);
            news.publishTime = LocalDateTime.now().minusMinutes(i * i * i * i * 30);
            data.add(news);
        }
        adapter.add(data);
        tabLayout.addTab(tabLayout.newTab().setText("全部"));
        tabLayout.addTab(tabLayout.newTab().setText("推荐"));
        tabLayout.addTab(tabLayout.newTab().setText("国内"));
        tabLayout.addTab(tabLayout.newTab().setText("国际"));
        tabLayout.addTab(tabLayout.newTab().setText("军事"));
        tabLayout.addTab(tabLayout.newTab().setText("体育"));
        tabLayout.addTab(tabLayout.newTab().setText("娱乐"));
        tabLayout.addTab(tabLayout.newTab().setText("游戏"));
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        List<News> data = new ArrayList<>();
                        for (int i = 0; i < 10; ++i) {
                            News news = new News();
                            news.title = String.format("这是标题 %d 啊", i);
                            news.publisher = String.format("第%d号", i);
                            news.publishTime = LocalDateTime.now().minusMinutes(i * i * i * i * 30);
                            data.add(news);
                        }
                        adapter.add(data);
                        refreshLayout.finishLoadMore();
                    }
                }, 500);
            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(500);
            }
        });
    }

}
