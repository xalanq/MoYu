package com.java.moyu;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

/**
 * 设置碎片
 */
class AboutFragment extends BasicFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);

        TextView author = view.findViewById(R.id.about_author);
        author.setText(R.string.app_author);
        author.setMovementMethod(LinkMovementMethod.getInstance());

        MainActivity a = (MainActivity)getActivity();
        Toolbar toolbar = view.findViewById(R.id.about_toolbar);
        a.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                a, a.drawerLayout, toolbar, R.string.main_navigation_drawer_open, R.string.main_navigation_drawer_close);
        a.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        a.getSupportActionBar().setDisplayShowTitleEnabled(false);
        return view;
    }
}
