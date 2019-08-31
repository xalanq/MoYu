package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;

public class NewsActivity extends VideoActivity {

    @BindView(R.id.news_toolbar)
    Toolbar toolbar;

    @Override
    protected int getLayoutResource() {
        return R.layout.news_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String title = "新华社";
        getSupportActionBar().setTitle(title);
        SmartSwipe.wrap(this)
            .addConsumer(new ActivitySlidingBackConsumer(this))
            .enableLeft();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_toolbar, menu);
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_left_exit);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.search_button:
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        case R.id.star_button:
            Toast.makeText(this, "你点击了收藏", Toast.LENGTH_SHORT).show();
            item.setIcon(R.drawable.ic_starred);
            return true;
        case R.id.share_button:
            Toast.makeText(this, "你点击了分享", Toast.LENGTH_SHORT).show();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
