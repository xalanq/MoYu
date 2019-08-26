package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.StrictMode;

import com.google.android.material.tabs.TabLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    @BindView(R.id.refresh_layout) RefreshLayout refreshLayout;
    @BindView(R.id.index_more_button) ImageButton btnMore;

    @Override
    protected int getLayoutResource() {
        return R.layout.index_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MainActivity a = (MainActivity)getActivity();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout), new NewsAdapter.OnClick() {
            @Override
            public void click(View view, int position) {
                Toast.makeText(getContext(), "click news", Toast.LENGTH_SHORT).show();
            }
        });

        test();
    }

    private void test() {
        String[] tabs = {"推荐", "国内", "国际", "军事", "体育", "娱乐", "游戏"};
        for (String tab : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        final Runnable loadMore = new Runnable() {
            @Override
            public void run() {
                // TODO This maybe a sample
                String requestUrl = "https://api2.newsminer.net/svc/news/queryNewsList";
                Map params = new HashMap();
                params.put("size", "10");
                params.put("endDate", "2019-07-03");
                params.put("categories", "娱乐");
                String string = NetConnection.httpRequest(requestUrl, params);
                try {
                    JSONObject jsonData = new JSONObject(string);
                    JSONArray allNewsData = jsonData.getJSONArray("data");
                    List<News> data = new ArrayList<>();
                    DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    for (int i = 0; i < 4; ++i) {
                        JSONObject newsData = allNewsData.getJSONObject(i);
                        News news = new News();
                        news.title = newsData.getString("title");
                        news.publisher = newsData.getString("publisher");
                        news.publishTime = LocalDateTime.parse(newsData.getString("publishTime"), dataFormatter);
                        data.add(news);
                    }
                    adapter.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
