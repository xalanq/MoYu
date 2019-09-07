package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    LinearLayout layout;
    ImageView title;
    ImageView subtitle;
    ImageView logo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);

        layout = findViewById(R.id.splash);
        title = findViewById(R.id.splash_title);
        subtitle = findViewById(R.id.splash_subtitle);
        logo = findViewById(R.id.splash_logo);

        if (BasicApplication.isNight()) {
            layout.setBackgroundColor(getResources().getColor(R.color.colorBackgroundNight, getTheme()));
            title.setImageResource(R.drawable.splash_title_night);
            subtitle.setImageResource(R.drawable.splash_subtitle_night);
            logo.setImageResource(R.drawable.splash_logo_night);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }, 1000);
    }

}



















