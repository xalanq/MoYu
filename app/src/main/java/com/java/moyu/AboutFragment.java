package com.java.moyu;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

/**
 * 设置碎片
 */
public class AboutFragment extends BasicFragment {

    @BindView(R.id.about_author) TextView author;
    @BindView(R.id.about_toolbar) Toolbar toolbar;

    @Override
    protected int getLayoutResource() {
        return R.layout.about_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MainActivity a = (MainActivity)getActivity();

        author.setText(R.string.app_author);
        author.setMovementMethod(LinkMovementMethod.getInstance());

        a.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                a, a.drawerLayout, toolbar, R.string.main_navigation_drawer_open, R.string.main_navigation_drawer_close);
        a.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        a.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

}
