package com.java.moyu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

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

    @Override
    protected int getLayoutResource() {
        return R.layout.main_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentAllocator = new FragmentAllocator();

        setNavigation();

        switchFragment(fragmentAllocator.getDefault());
    }

    private void setNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().findItem(R.id.main_navigation_menu_index).setChecked(true);
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
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

}
