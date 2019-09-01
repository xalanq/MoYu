package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

/**
 * 首页碎片
 */
public class IndexFragment extends BasicFragment {

    @BindView(R.id.index_search_box)
    EditText searchBox;
    @BindView(R.id.index_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    @BindView(R.id.index_more_button)
    ImageButton btnMore;
    private NewsAdapter adapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.index_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MainActivity a = (MainActivity) getActivity();

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
                startActivity(new Intent(getActivity(), CategoryActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
            }
        });

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout), new NewsAdapter.OnClick() {
            @Override
            public void click(View view, int position) {
                startActivity(new Intent(getActivity(), NewsActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
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
                NewsDatabase db = new NewsDatabase(getActivity(), Constants.DB_NAME, null, Constants.DB_VERSION);
                String requestUrl = "https://api2.newsminer.net/svc/news/queryNewsList";
                Map params = new HashMap();
                params.put("size", "1");
                params.put("words", "野熊");
                params.put("startDate", "2018-08-15");
                params.put("endDate", "2018-08-21");
                String string = NetConnection.httpRequest(requestUrl, params);
                try {
                    JSONObject jsonData = new JSONObject(string);
                    JSONArray allNewsData = jsonData.getJSONArray("data");
                    List<News> data = new ArrayList<>();
                    for (int i = 0; i < allNewsData.length(); ++i) {
                        JSONObject newsData = allNewsData.getJSONObject(i);
                        News news = new News(newsData);
                        data.add(news);
                        if (db.addNews(news) == false)
                            Log.d("IndexFragment","addNews Fail");
                    }
                    adapter.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                params = new HashMap();
                params.put("size", "20");
                params.put("words", "香港");
                params.put("endDate", LocalDateTime.now().format(Constants.dataFormatter));
                string = NetConnection.httpRequest(requestUrl, params);
                try {
                    JSONObject jsonData = new JSONObject(string);
                    JSONArray allNewsData = jsonData.getJSONArray("data");
                    List<News> data = new ArrayList<>();
                    int l = 10;
                    if (allNewsData.length() < l)
                        l = allNewsData.length();
                    for (int i = 0; i < l; ++i) {
                        JSONObject newsData = allNewsData.getJSONObject(i);
                        News news = new News(newsData);
                        db.addNews(news);
                        data.add(news);
                        if (db.addNews(news) == false) {
                            Log.d("IndexFragment", "addNews Fail");
                        }
                        if (db.addFavour(news.getID()) == false) {
                            Log.d("IndexFragment", "addFavour Fail");
                        }
                        if (db.addHistory(news.getID(), LocalDateTime.now()) == false) {
                            Log.d("IndexFragment", "addHistory Fail");
                        }
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
