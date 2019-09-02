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

import java.time.LocalDateTime;
import java.util.List;

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
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                intent.putExtra("news", adapter.get(position).toJSONObject().toString());
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
            }
        });

        initData();
    }

    private void initData() {
        String[] tabs = {"推荐", "国内", "国际", "军事", "体育", "娱乐", "游戏"};
        for (String tab : tabs) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }
        final Runnable loadMore = new Runnable() {
            LocalDateTime end_time = LocalDateTime.now();

            @Override
            public void run() {
                final NewsDatabase db = new NewsDatabase(getActivity());
                new NewsNetwork.Builder()
                    .add("size", "" + Constants.PAGE_SIZE)
                    .add("words", "香港")
                    .add("endDate", end_time.format(Constants.dataFormatter))
                    .build()
                    .run(new NewsNetwork.Callback() {
                        @Override
                        public void timeout() {

                        }

                        @Override
                        public void error() {
                        }

                        @Override
                        public void ok(List<News> data) {
                            if (data.isEmpty()) {
                                refreshLayout.finishLoadMoreWithNoMoreData();
                            } else {
                                end_time = data.get(data.size() - 1).getPublishTime().minusSeconds(1);
                                for (News news : data)
                                    db.addNews(news);
                                adapter.add(data);
                                refreshLayout.finishLoadMore();
                            }
                        }
                    });
            }
        };
        loadMore.run();
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(loadMore);
            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(500);
            }
        });
    }

}
