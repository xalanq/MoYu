package com.java.moyu;

import android.widget.TextView;

import butterknife.BindView;

public class SearchLoadingFragment extends BasicFragment {

    @BindView(R.id.loading_text) TextView loadingText;

    @Override
    protected int getLayoutResource() {
        return R.layout.search_loading_fragment;
    }

}
