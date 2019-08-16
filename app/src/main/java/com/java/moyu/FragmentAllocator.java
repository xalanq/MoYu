package com.java.moyu;

class FragmentAllocator {
    private IndexFragment indexFragment;
    private FavoriteFragment favoriteFragment;
    private HistoryFragment historyFragment;
    private SettingFragment settingFragment;
    private AboutFragment aboutFragment;

    BasicFragment getDefault() {
        return getIndexFragment();
    }

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
}
