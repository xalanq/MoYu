package com.java.moyu;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
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

    @BindView(R.id.about_author)
    TextView author;
    @BindView(R.id.about_toolbar)
    Toolbar toolbar;
    @BindView(R.id.about_version)
    TextView version;

    @Override
    protected int getLayoutResource() {
        return R.layout.about_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MainActivity a = (MainActivity) getActivity();

        author.setText(R.string.app_author);
        author.setMovementMethod(LinkMovementMethod.getInstance());

        PackageManager manager = getActivity().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            version.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        a.setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            a, a.drawerLayout, toolbar, R.string.main_navigation_drawer_open, R.string.main_navigation_drawer_close);
        a.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        a.getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    void refreshUI() {
        Resources r = getResources();
        Resources.Theme theme = getActivity().getTheme();
        TypedValue colorPrimary = new TypedValue();
        TypedValue colorPrimaryDark = new TypedValue();
        TypedValue colorAccent = new TypedValue();
        TypedValue colorTitle = new TypedValue();
        TypedValue colorSubtitle = new TypedValue();
        TypedValue colorText = new TypedValue();
        TypedValue colorBackground = new TypedValue();
        TypedValue colorTabRipple = new TypedValue();
        TypedValue colorTopBackground = new TypedValue();
        TypedValue colorTabSelectedText = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        theme.resolveAttribute(R.attr.colorPrimaryDark, colorPrimaryDark, true);
        theme.resolveAttribute(R.attr.colorAccent, colorAccent, true);
        theme.resolveAttribute(R.attr.colorTitle, colorTitle, true);
        theme.resolveAttribute(R.attr.colorSubtitle, colorSubtitle, true);
        theme.resolveAttribute(R.attr.colorText, colorText, true);
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true);
        theme.resolveAttribute(R.attr.colorTabRipple, colorTabRipple, true);
        theme.resolveAttribute(R.attr.colorTopBackground, colorTopBackground, true);
        theme.resolveAttribute(R.attr.colorTabSelectedText, colorTabSelectedText, true);

    }

}
