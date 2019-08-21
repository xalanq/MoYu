package com.java.moyu;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;

public class MainActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.main_navigation_view) NavigationView navigationView;

    public FragmentAllocator fragmentAllocator;
    private BasicFragment currentFragment;
    boolean checkExit;

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
        } else if (id == R.id.main_navigation_menu_setting) {
            switchFragment(fragmentAllocator.getSettingFragment());
        } else if (id == R.id.main_navigation_menu_about) {
            switchFragment(fragmentAllocator.getAboutFragment());
        } else if (id == R.id.main_navigation_menu_night_mode) {
            switchNightMode();
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
        Switch mode = (Switch)navigationView.getMenu().findItem(R.id.main_navigation_menu_night_mode).getActionView();
        if (mode.isChecked()) {

        } else {

        }
        mode.setChecked(!mode.isChecked());
    }

}

class FragmentAllocator {

    private IndexFragment indexFragment;
    private FavoriteFragment favoriteFragment;
    private HistoryFragment historyFragment;
    private SettingFragment settingFragment;
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

    SettingFragment getSettingFragment() {
        if (settingFragment == null)
            settingFragment = new SettingFragment();
        return settingFragment;
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
