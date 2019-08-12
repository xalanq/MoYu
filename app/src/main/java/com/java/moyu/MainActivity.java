package com.java.moyu;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.navigation.NavigationView;

import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BasicActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.main_drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.main_navigation_view) NavigationView navigationView;
    @BindView(R.id.main_toolbar) Toolbar toolbar;
    @BindView(R.id.main_toolbar_title) TextView toolbarTitle;

    private FragmentAllocator fragmentAllocator;
    private BasicFragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        initValues();
        setToolbar();
        setNavigation();

        switchFragment(fragmentAllocator.getDefault());
    }

    private void initValues() {
        ButterKnife.bind(this);
        fragmentAllocator = new FragmentAllocator();
    }

    private void setNavigation() {
        navigationView.getMenu().findItem(R.id.main_navigation_menu_index).setChecked(true);
        final TextView username = navigationView.getHeaderView(0).findViewById(R.id.main_navigation_username);
        username.setText(R.string.main_navigation_login);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.main_navigation_drawer_open, R.string.main_navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void switchFragment(BasicFragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (currentFragment == fragment)
            return;
        if (!fragment.isAdded())
            fragmentTransaction.add(R.id.main_content_layout, fragment);
        if (currentFragment != null)
            fragmentTransaction.hide(currentFragment);
        currentFragment = fragment;
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
        toolbarTitle.setText(fragment.getTitleId());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_navigation_menu_index) {
            switchFragment(fragmentAllocator.getIndexFragment());
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
