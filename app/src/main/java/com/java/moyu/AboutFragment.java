package com.java.moyu;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
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
    @BindView(R.id.about_logo)
    ImageView logo;

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
        TypedValue colorAccent = new TypedValue();
        TypedValue colorSubtitle = new TypedValue();
        TypedValue colorBackground = new TypedValue();
        TypedValue icon = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        theme.resolveAttribute(R.attr.colorSubtitle, colorSubtitle, true);
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true);
        theme.resolveAttribute(R.attr.colorAccent, colorAccent, true);
        theme.resolveAttribute(R.attr.icon, icon, true);

        getView().setBackgroundResource(colorBackground.resourceId);
        toolbar.setBackgroundResource(colorPrimary.resourceId);
        version.setTextColor(r.getColor(colorSubtitle.resourceId, theme));
        author.setTextColor(r.getColor(colorSubtitle.resourceId, theme));
        author.setLinkTextColor(r.getColor(colorAccent.resourceId, theme));
        logo.setImageResource(icon.resourceId);
        TextView app_name = getView().findViewById(R.id.about_app_name);
        app_name.setTextColor(r.getColor(colorSubtitle.resourceId, theme));
    }

}
