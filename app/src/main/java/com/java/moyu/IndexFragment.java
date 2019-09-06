package com.java.moyu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 首页碎片
 */
public class IndexFragment extends BasicFragment {

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

        tabLayout.setupWithViewPager(viewPager);
        initData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra("hasEdited", false)) {
                initData();
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
        if (!isAdded())
            return;
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
                viewPager.setOffscreenPageLimit(2);
            }
        });
    }

    class PagerAdapter extends FragmentPagerAdapter {

        List<String> tabs;

        PagerAdapter(FragmentManager fm, List<String> tabs) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.tabs = tabs;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return new IndexTabFragment(tabs.get(position));
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

    }

}
