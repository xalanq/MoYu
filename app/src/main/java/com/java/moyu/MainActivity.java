package com.java.moyu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.cache.DiskCache;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;

public class MainActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    public FragmentAllocator fragmentAllocator;
    @BindView(R.id.main_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_navigation_view)
    NavigationView navigationView;
    boolean checkExit;
    private BasicFragment currentFragment;

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
    }

    private void setNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
        final TextView username = navigationView.getHeaderView(0).findViewById(R.id.main_navigation_username);
        username.setText(R.string.main_navigation_login);
    }

    private void switchFragment(BasicFragment fragment) {
        if (currentFragment == fragment)
            return;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded())
            ft.add(R.id.main_layout, fragment);
        if (currentFragment != null)
            ft.hide(currentFragment);
        currentFragment = fragment;
        ft.show(fragment);
        ft.commit();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_navigation_menu_index) {
            switchFragment(fragmentAllocator.getIndexFragment());
        } else if (id == R.id.main_navigation_menu_favorite) {
            switchFragment(fragmentAllocator.getFavoriteFragment());
        } else if (id == R.id.main_navigation_menu_history) {
            switchFragment(fragmentAllocator.getHistoryFragment());
        } else if (id == R.id.main_navigation_menu_about) {
            switchFragment(fragmentAllocator.getAboutFragment());
        } else if (id == R.id.main_navigation_menu_night_mode) {
            switchNightMode();
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

    private void switchNightMode() {
        Switch mode = (Switch) navigationView.getMenu().findItem(R.id.main_navigation_menu_night_mode).getActionView();
        if (mode.isChecked()) {

        } else {

        }
        mode.setChecked(!mode.isChecked());
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
        new Cache.GetSizeTask(resultView).execute(new File(getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR));
    }

}

class FragmentAllocator {

    private IndexFragment indexFragment;
    private FavoriteFragment favoriteFragment;
    private HistoryFragment historyFragment;
    private AboutFragment aboutFragment;
    private CategoryFragment categoryFragment;

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

    CategoryFragment getCategoryFragment() {
        if (categoryFragment == null)
            categoryFragment = new CategoryFragment();
        return categoryFragment;
    }

}
