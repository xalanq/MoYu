package com.java.moyu;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.material.tabs.TabLayout;

import java.lang.reflect.Field;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;

/**
 * 首页碎片
 */
public class IndexFragment extends BasicFragment {

    @BindView(R.id.index_toolbar)
    Toolbar toolbar;
    @BindView(R.id.index_search_box)
    EditText searchBox;
    @BindView(R.id.index_tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.index_pager)
    ViewPager viewPager;
    @BindView(R.id.index_more_button)
    ImageButton btnMore;

    private PagerAdapter pagerAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.index_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity a = (MainActivity) getActivity();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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
                startActivityForResult(new Intent(getActivity(), CategoryActivity.class), 3);
                getActivity().overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
            }
        });

        tabLayout.setupWithViewPager(viewPager);
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra("hasEdited", false)) {
                MainActivity a = (MainActivity) getActivity();
                a.onActivityResult(requestCode, resultCode, data);
                return;
            }
            int position = data.getIntExtra("selectPosition", -1);
            if (position != -1) {
                tabLayout.getTabAt(position + 1).select();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void initData() {
        User.getInstance().getCategory(new User.CategoryCallback() {
            @Override
            public void error(String msg) {
                BasicApplication.showToast(msg);
            }

            @Override
            public void ok(List<String> chosen, List<String> remain) {
                chosen.add(0, BasicApplication.getContext().getString(R.string.recommend));
                pagerAdapter = new PagerAdapter(getChildFragmentManager(), chosen);
                viewPager.setAdapter(pagerAdapter);
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
        TypedValue colorTabRipple = new TypedValue();
        TypedValue colorTopBackground = new TypedValue();
        TypedValue colorTabSelectedText = new TypedValue();
        TypedValue searchBoxBackground = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        theme.resolveAttribute(R.attr.colorTitle, colorTitle, true);
        theme.resolveAttribute(R.attr.colorSubtitle, colorSubtitle, true);
        theme.resolveAttribute(R.attr.colorText, colorText, true);
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true);
        theme.resolveAttribute(R.attr.colorButton, colorButton, true);
        theme.resolveAttribute(R.attr.colorTabRipple, colorTabRipple, true);
        theme.resolveAttribute(R.attr.colorTopBackground, colorTopBackground, true);
        theme.resolveAttribute(R.attr.colorTabSelectedText, colorTabSelectedText, true);
        theme.resolveAttribute(R.attr.searchBoxBackground, searchBoxBackground, true);

        searchBox.setBackgroundResource(searchBoxBackground.resourceId);
        getView().setBackgroundResource(colorBackground.resourceId);
        getView().findViewById(R.id.app_bar_layout).setBackgroundResource(colorPrimary.resourceId);
        toolbar.setBackgroundResource(colorPrimary.resourceId);
        getView().findViewById(R.id.news_frame_layout).setBackgroundResource(colorBackground.resourceId);
        searchBox.setHintTextColor(r.getColor(colorSubtitle.resourceId, theme));
        try {
            Field field;
            field = tabLayout.getClass().getDeclaredField("tabBackgroundResId");
            field.setAccessible(true);
            field.set(tabLayout, colorTopBackground.resourceId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tabLayout.setTabTextColors(r.getColor(colorSubtitle.resourceId, theme), r.getColor(colorTabSelectedText.resourceId, theme));
        tabLayout.setTabRippleColor(ColorStateList.valueOf(r.getColor(colorTabRipple.resourceId, theme)));
        tabLayout.setSelectedTabIndicatorColor(r.getColor(colorPrimary.resourceId, theme));
        btnMore.setBackgroundResource(colorTopBackground.resourceId);
        viewPager.setBackgroundResource(colorBackground.resourceId);
        for (Fragment tmp : getChildFragmentManager().getFragments()) {
            if (tmp instanceof IndexTabFragment) {
                IndexTabFragment f = (IndexTabFragment) tmp;
                f.refreshUI();
            }
        }
        getView().findViewById(R.id.index_tab_linear_layout).setBackgroundResource(colorTopBackground.resourceId);
    }

    class PagerAdapter extends FragmentPagerAdapter {

        List<String> tabs;
        Fragment[] fragments;

        PagerAdapter(FragmentManager fm, List<String> tabs) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.tabs = tabs;
            fragments = new Fragment[tabs.size()];
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            if (fragments[position] != null)
                return fragments[position];
            return fragments[position] = new IndexTabFragment(tabs.get(position));
        }

        @Override
        public int getCount() {
            return tabs.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        }

    }

}
