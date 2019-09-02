package com.java.moyu;

import android.app.Activity;
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
                startActivityForResult(new Intent(getActivity(), CategoryActivity.class), 1);
                getActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
            }
        });

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout),
            NewsAdapter.defaultOnclick(getActivity()));

        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra("hasEdited", false)) {
                updateTab();
            }
            int position = data.getIntExtra("selectPosition", -1);
            if (position != -1) {
                tabLayout.getTabAt(position + 1).select();
            }
        }
    }

    void updateTab() {
        List<String> tabs = NewsDatabase.getInstance().queryCategory(1);
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.recommend)));
        for (String tab : tabs)
            tabLayout.addTab(tabLayout.newTab().setText(tab));
    }

    void initData() {
        updateTab();
        final Runnable loadMore = new Runnable() {
            @Override
            public void run() {
                new NewsNetwork.Builder()
                    .add("size", "" + Constants.PAGE_SIZE)
                    .add("words", "香港")
                    .add("endDate", adapter.get(adapter.getItemCount()-1).getPublishTime().minusSeconds(1).format(Constants.TIME_FORMATTER))
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
        };

        final Runnable refresh = new Runnable() {
            @Override
            public void run() {
                new NewsNetwork.Builder()
                    .add("size", "" + Constants.PAGE_SIZE)
                    .add("words", "香港")
                    .add("endDate", LocalDateTime.now().format(Constants.TIME_FORMATTER))
                    .build()
                    .run(new NewsNetwork.Callback() {
                        @Override
                        public void timeout() {
                            refreshLayout.finishRefresh(false);
                        }

                        @Override
                        public void error() {
                            refreshLayout.finishRefresh(false);
                        }

                        @Override
                        public void ok(List<News> data) {
                            adapter.clear();
                            adapter.add(data);
                            refreshLayout.finishRefresh();
                        }
                    });
            }
        };

        refresh.run();
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(loadMore);
            }

            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().post(refresh);
            }
        });
    }

}
