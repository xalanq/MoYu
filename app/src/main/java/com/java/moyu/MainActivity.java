package com.java.moyu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends VideoActivity implements NavigationView.OnNavigationItemSelectedListener {

    public FragmentAllocator fragmentAllocator;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_navigation_view)
    NavigationView navigationView;
    CircleImageView avatarView;
    TextView username;
    private BasicFragment currentFragment;
    private boolean checkExit;

    @Override
    protected int getLayoutResource() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentAllocator = new FragmentAllocator();

        setNavigation();

        backDefault();
        reloadUser(false);
        User.getInstance().updateUserInfo(new User.DefaultCallback() {
            @Override
            public void error(String msg) {
                BasicApplication.showToast(msg);
                reloadUser(true);
            }

            @Override
            public void ok() {
                BasicApplication.showToast(getResources().getString(R.string.login_success));
                reloadUser(true);
            }
        });
    }

    private void setNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
        username = navigationView.getHeaderView(0).findViewById(R.id.main_navigation_username);
        avatarView = navigationView.getHeaderView(0).findViewById(R.id.main_navigation_avatar);
        username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickUser();
            }
        });
        avatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickUser();
            }
        });
        final Switch actionView = (Switch) navigationView.getMenu().findItem(R.id.main_navigation_menu_night_mode).getActionView();
        actionView.setChecked(BasicApplication.isNight());
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchDayNight();
            }
        });
    }

    void reloadUser(boolean refreshFragment) {
        String name = User.getInstance().getUsername();
        if (name.isEmpty())
            name = getResources().getString(R.string.main_navigation_login);
        username.setText(name);
        String avatar = User.getInstance().getAvatar();
        if (avatar.isEmpty()) {
            avatarView.setImageResource(R.drawable.default_avatar);
        } else {
            Glide.with(this).load(User.getInstance().getAvatar())
                .placeholder(R.drawable.loading_cover)
                .error(R.drawable.default_avatar).centerCrop()
                .into(avatarView);
        }
        if (refreshFragment) {
            if (currentFragment == fragmentAllocator.getIndexFragment())
                fragmentAllocator.getIndexFragment().initData();
            else if (currentFragment == fragmentAllocator.getFavoriteFragment())
                fragmentAllocator.getFavoriteFragment().initData();
            else if (currentFragment == fragmentAllocator.getHistoryFragment())
                fragmentAllocator.getHistoryFragment().initData();
        }
    }

    void clickUser() {
        if (User.getInstance().isLogged()) {
            startActivityForResult(new Intent(MainActivity.this, UserActivity.class), 2);
        } else {
            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), 1);
        }
        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1)
                reloadUser(true);
            else if (requestCode == 2)
                reloadUser(false);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    void switchFragment(BasicFragment fragment) {
        if (currentFragment == fragment)
            return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded())
            ft.add(R.id.main_layout, fragment);
        if (currentFragment != null)
            ft.hide(currentFragment);
        ft.show(fragment);
        ft.commit();
        currentFragment = fragment;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_navigation_menu_index) {
            switchFragment(fragmentAllocator.getIndexFragment());
        } else if (id == R.id.main_navigation_menu_favorite) {
            switchFragment(fragmentAllocator.getFavoriteFragment());
            fragmentAllocator.getFavoriteFragment().initData();
        } else if (id == R.id.main_navigation_menu_history) {
            switchFragment(fragmentAllocator.getHistoryFragment());
            fragmentAllocator.getHistoryFragment().initData();
        } else if (id == R.id.main_navigation_menu_about) {
            switchFragment(fragmentAllocator.getAboutFragment());
        } else if (id == R.id.main_navigation_menu_night_mode) {
            switchDayNight();
            return false;
        } else if (id == R.id.main_navigation_menu_clear_cache) {
            clearCache();
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == fragmentAllocator.getIndexFragment()) {
            if (!checkExit) {
                Toast.makeText(this, R.string.check_exit, Toast.LENGTH_SHORT).show();
                checkExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkExit = false;
                    }
                }, 2000);
                return;
            }
        } else {
            backDefault();
            return;
        }
        super.onBackPressed();
    }

    private void backDefault() {
        navigationView.getMenu().findItem(R.id.main_navigation_menu_index).setChecked(true);
        switchFragment(fragmentAllocator.getIndexFragment());
    }

    private void switchDayNight() {
        boolean isNight = !BasicApplication.isNight();
        BasicApplication.setNight(isNight);
        Switch mode = (Switch) navigationView.getMenu().findItem(R.id.main_navigation_menu_night_mode).getActionView();
        mode.setChecked(isNight);
        updateTheme();
        refreshUI();
    }

    void refreshUI() {
        Resources r = getResources();
        Resources.Theme theme = getTheme();
        TypedValue colorPrimary = new TypedValue();
        TypedValue colorTopBackground = new TypedValue();
        TypedValue colorSubtitle = new TypedValue();
        theme.resolveAttribute(R.attr.colorPrimary, colorPrimary, true);
        theme.resolveAttribute(R.attr.colorTopBackground, colorTopBackground, true);
        theme.resolveAttribute(R.attr.colorSubtitle, colorSubtitle, true);

        navigationView.setBackgroundResource(colorTopBackground.resourceId);
        navigationView.setItemTextColor(ColorStateList.valueOf(r.getColor(colorSubtitle.resourceId, theme)));
        navigationView.getHeaderView(0).setBackgroundResource(colorPrimary.resourceId);
        getWindow().setStatusBarColor(r.getColor(colorPrimary.resourceId, theme));

        fragmentAllocator.refreshUI();
    }

    private void clearCache() {
        final TextView resultView = new TextView(this);
        resultView.setPadding(64, 16, 64, 16);
        resultView.setTextSize(16);
        resultView.setTextColor(Color.parseColor("#000000"));
        new AlertDialog.Builder(this)
            .setTitle(R.string.cache_title).setView(resultView)
            .setPositiveButton(getResources().getText(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    new Cache.ClearCacheTask(getApplicationContext()).execute();
                    dialogInterface.dismiss();
                }
            })
            .setNegativeButton(getResources().getText(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            }).show();
        new Cache.GetSizeTask(resultView).execute(
            new File(getApplicationContext().getCacheDir(),
                DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
    }

    class FragmentAllocator {

        private IndexFragment indexFragment;
        private FavoriteFragment favoriteFragment;
        private HistoryFragment historyFragment;
        private AboutFragment aboutFragment;

        IndexFragment getIndexFragment() {
            if (indexFragment == null)
                indexFragment = new IndexFragment();
            return indexFragment;
        }

        FavoriteFragment getFavoriteFragment() {
            if (favoriteFragment == null)
                favoriteFragment = new FavoriteFragment();
            return favoriteFragment;
        }

        HistoryFragment getHistoryFragment() {
            if (historyFragment == null)
                historyFragment = new HistoryFragment();
            return historyFragment;
        }

        AboutFragment getAboutFragment() {
            if (aboutFragment == null)
                aboutFragment = new AboutFragment();
            return aboutFragment;
        }

        void refreshUI() {
            if (indexFragment != null)
                indexFragment.refreshUI();
            if (favoriteFragment != null)
                favoriteFragment.refreshUI();
            if (historyFragment != null)
                historyFragment.refreshUI();
            if (aboutFragment != null)
                aboutFragment.refreshUI();
        }

    }

}

