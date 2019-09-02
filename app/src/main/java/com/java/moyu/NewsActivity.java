package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;

import org.json.JSONObject;

import java.time.LocalDateTime;

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
    @BindView(R.id.gallery_hint)
    TextView galleryHintView;
    @BindView(R.id.video_layout)
    ConstraintLayout videoLayout;
    @BindView(R.id.video_player)
    CoverVideoPlayer player;
    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.publisher)
    TextView publisherView;
    @BindView(R.id.category)
    TextView categoryView;
    @BindView(R.id.publish_time)
    TextView publishTimeView;
    @BindView(R.id.content)
    TextView contentView;

    private SwipeConsumer consumer;
    private News news;
    private boolean isStarred;

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

        initData();

        getSupportActionBar().setTitle(news.publisher);
        if (news.image != null) {
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
            ImageAdapter adapter = new ImageAdapter(this, news.image);
            galleryView.setAdapter(adapter);
        } else {
            galleryLayout.setVisibility(View.GONE);
        }
        if (news.image == null || news.image.length == 1) {
            galleryHintView.setVisibility(View.GONE);
        }
        if (news.video != null && !news.video.isEmpty()) {
            player.setup(this, news.video, news.title, 0);
        } else {
            videoLayout.setVisibility(View.GONE);
        }
        titleView.setText(news.title);
        contentView.setText(news.content);
        publisherView.setText(news.publisher);
        publishTimeView.setText(Util.parseTime(news.publishTime));
        categoryView.setText(news.category);
    }

    private void initData() {
        try {
            news = new News(new JSONObject(getIntent().getExtras().getString("news")));
            isStarred = NewsDatabase.getInstance().queryFavour(news.id);
        } catch (Exception e) {
        }
    }

    void clickStar(MenuItem item) {

        isStarred = !isStarred;
        if (isStarred) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    NewsDatabase.getInstance().addFavour(news.id, LocalDateTime.now());
                }
            });
            item.setIcon(R.drawable.ic_starred);
        } else {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    NewsDatabase.getInstance().delFavour(news.id);
                }
            });
            item.setIcon(R.drawable.ic_star_light);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_toolbar, menu);
        if (isStarred)
            menu.findItem(R.id.star_button).setIcon(R.drawable.ic_starred);
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
            clickStar(item);
            return true;
        case R.id.share_button:
            new ShareUtil.Builder(this)
                .setContentType(ShareUtil.ShareContentType.TEXT)
                .setTitle(news.title)
                .setTextContent(news.content, 50)
                .build().shareBySystem();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
