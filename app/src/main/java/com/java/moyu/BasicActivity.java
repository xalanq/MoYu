package com.java.moyu;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;

public abstract class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        updateTheme();
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);
    }

    protected abstract int getLayoutResource();

    protected void updateTheme() {
        if (BasicApplication.isNight())
            setTheme(R.style.NightTheme);
        else
            setTheme(R.style.DayTheme);
    }

}
