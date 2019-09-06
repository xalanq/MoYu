package com.java.moyu;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.SwipeConsumer;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;

public abstract class SwipeActivity extends BasicActivity {

    protected SwipeConsumer consumer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        consumer = SmartSwipe.wrap(this)
            .addConsumer(new ActivitySlidingBackConsumer(this))
            .enableLeft();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_left_exit);
    }

}
