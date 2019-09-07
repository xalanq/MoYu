package com.java.moyu;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
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

        adapter = NewsAdapter.newAdapter(getContext(), view.findViewById(R.id.news_layout), new NewsAdapter.OnClick() {
                @Override
                public void click(View view, int position, final News news) {
                    Intent intent = new Intent(getActivity(), NewsActivity.class);
                    intent.putExtra("news", news.toJSONObject().toString());
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
                }
            }
        );

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
        TypedValue colorTitle = new TypedValue();
        TypedValue colorSubtitle = new TypedValue();
        TypedValue colorText = new TypedValue();
        TypedValue colorBackground = new TypedValue();
        TypedValue colorButton = new TypedValue();
        TypedValue colorHasRead = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        theme.resolveAttribute(R.attr.colorTitle, colorTitle, true);
        theme.resolveAttribute(R.attr.colorSubtitle, colorSubtitle, true);
        theme.resolveAttribute(R.attr.colorText, colorText, true);
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true);
        theme.resolveAttribute(R.attr.colorButton, colorButton, true);
        theme.resolveAttribute(R.attr.colorHasRead, colorHasRead, true);

        toolbar.setBackgroundResource(colorPrimary.resourceId);
        getView().findViewById(R.id.news_frame_layout).setBackgroundResource(colorBackground.resourceId);
        loadingLayout.setBackgroundResource(colorBackground.resourceId);
        TextView loadingText = loadingLayout.findViewById(R.id.loading_text);
        loadingText.setTextColor(r.getColor(colorSubtitle.resourceId, theme));
        emptyLayout.setBackgroundResource(colorBackground.resourceId);
        TextView emptyText = emptyLayout.findViewById(R.id.empty_text);
        emptyText.setTextColor(r.getColor(colorSubtitle.resourceId, theme));
        emptyButton.setTextColor(r.getColor(colorText.resourceId, theme));
        emptyButton.setBackgroundTintList(ColorStateList.valueOf(r.getColor(colorButton.resourceId, theme)));
        RecyclerView newsView = refreshLayout.findViewById(R.id.news_layout);

        for (int i = 0; i < newsView.getChildCount(); ++i) {
            View view = newsView.getChildAt(i).findViewById(R.id.news_card);
            int position = newsView.getChildAdapterPosition(newsView.getChildAt(i));
            if (adapter.get(position).isRead)
                view.setBackgroundResource(colorHasRead.resourceId);
            else
                view.setBackgroundResource(colorBackground.resourceId);
            view.setBackgroundResource(colorBackground.resourceId);
            TextView title = view.findViewById(R.id.title);
            title.setTextColor(r.getColor(colorTitle.resourceId, theme));
            TextView publisher = view.findViewById(R.id.publisher);
            publisher.setTextColor(r.getColor(colorSubtitle.resourceId, theme));
            TextView publishTime = view.findViewById(R.id.publish_time);
            publishTime.setTextColor(r.getColor(colorSubtitle.resourceId, theme));
            View divider = view.findViewById(R.id.divider);
            divider.setBackgroundResource(colorSubtitle.resourceId);
        }

        try {
            Class<RecyclerView> recyclerViewClass = RecyclerView.class;
            Field declaredField = recyclerViewClass.getDeclaredField("mRecycler");
            declaredField.setAccessible(true);
            Method declaredMethod = Class.forName(RecyclerView.Recycler.class.getName()).getDeclaredMethod("clear", (Class<?>[]) new Class[0]);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(declaredField.get(newsView));
            RecyclerView.RecycledViewPool recycledViewPool = newsView.getRecycledViewPool();
            recycledViewPool.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
