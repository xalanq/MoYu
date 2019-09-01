package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import github.hellocsl.layoutmanager.gallery.GalleryLayoutManager;

public class NewsActivity extends VideoActivity {

    @BindView(R.id.news_toolbar)
    Toolbar toolbar;
    @BindView(R.id.gallery_layout)
    ConstraintLayout galleryLayout;
    @BindView(R.id.gallery_view)
    RecyclerView galleryView;
    @BindView(R.id.video_layout)
    ConstraintLayout videoLayout;
    @BindView(R.id.video_player)
    CoverVideoPlayer player;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.publisher)
    TextView publisherView;
    @BindView(R.id.publish_time)
    TextView publishTimeView;
    @BindView(R.id.content)
    TextView contentView;

    private SwipeConsumer consumer;

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
        consumer = SmartSwipe.wrap(this)
            .addConsumer(new ActivitySlidingBackConsumer(this))
            .enableLeft();
        galleryView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    consumer.lockLeft();
                    break;
                case MotionEvent.ACTION_UP:
                    consumer.unlockLeft();
                    break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            }
        });

        test();
    }

    void test() {
        getSupportActionBar().setTitle("新华社");
        List<String> data = new ArrayList<>();
        data.add("http://5b0988e595225.cdn.sohucs.com/images/20190830/f655aaf1c880485585b93cea32a69381.jpeg");
        data.add("http://5b0988e595225.cdn.sohucs.com/images/20190830/711c0c66a12b48eca42baac81d5a7439.jpeg");
        data.add("http://5b0988e595225.cdn.sohucs.com/images/20190830/92f731d619454418b1efda327d7590b9.jpeg");
        GalleryLayoutManager layoutManager = new GalleryLayoutManager(GalleryLayoutManager.HORIZONTAL);
        layoutManager.attach(galleryView, 0);
        layoutManager.setItemTransformer(new GalleryLayoutManager.ItemTransformer() {
            @Override
            public void transformItem(GalleryLayoutManager layoutManager, View item, float fraction) {
                item.setPivotX(item.getWidth() / 2.f);
                item.setPivotY(item.getHeight() / 2.0f);
                float scale = 1 - 0.3f * Math.abs(fraction);
                item.setScaleX(scale);
                item.setScaleY(scale);
            }
        });
        ImageAdapter adapter = new ImageAdapter(this, data);
        galleryView.setAdapter(adapter);
        player.setup(this, "https://www.w3schools.com/html/movie.mp4", "这是标题", 0);
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
