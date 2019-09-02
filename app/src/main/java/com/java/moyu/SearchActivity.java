package com.java.moyu;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class SearchActivity extends BasicActivity {

    @BindView(R.id.search_toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_box)
    EditText searchBox;

    @Override
    protected int getLayoutResource() {
        return R.layout.search_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        searchBox.requestFocus();
        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (textView.getText().length() == 0) {
                        Toast.makeText(SearchActivity.this, R.string.no_empty, Toast.LENGTH_SHORT).show();
                    } else {
                        goSearch();
                    }
                    return true;
                }
                return false;
            }
        });
        switchFragment(new SearchIndexFragment());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(BasicFragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.search_layout, fragment)
            .commit();
    }

    private void startLoading() {
        switchFragment(new SearchLoadingFragment());
    }

    public void searchText(String text) {
        searchBox.setText(text);
        searchBox.setSelection(text.length());
        goSearch();
    }

    void goSearch() {
        InputMethodManager imm = (InputMethodManager) searchBox.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);
        final String text = searchBox.getText().toString();
        NewsDatabase.getInstance().addSearchHistory(text);
        startLoading();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                switchFragment(new SearchResultFragment(text));
            }
        });
    }

}
