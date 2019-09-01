package com.java.moyu;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;
import butterknife.BindView;

public class CategoryActivity extends BasicActivity {

    @BindView(R.id.category_edit_current)
    TextView editCurrent;
    @BindView(R.id.category_close)
    ImageButton btnClose;
    private ChipAdapter allAdapter;
    private ChipAdapter currentAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.category_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allAdapter = ChipAdapter.newAdapter(this, findViewById(R.id.category_all_layout), new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                currentAdapter.add(allAdapter.get(position));
                allAdapter.remove(position);
            }

            @Override
            public void close(Chip chip, int position) {
            }
        });

        currentAdapter = ChipAdapter.newAdapter(this, findViewById(R.id.category_current_layout), new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                Toast.makeText(CategoryActivity.this, "click chip", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void close(Chip chip, int position) {
                allAdapter.add(currentAdapter.get(position));
                currentAdapter.remove(position);
            }
        });

        editCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAdapter.toggleEdit()) {
                    editCurrent.setText(getResources().getText(R.string.complete));
                } else {
                    editCurrent.setText(getResources().getText(R.string.edit));
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SmartSwipe.wrap(this)
            .addConsumer(new ActivitySlidingBackConsumer(this))
            .enableLeft();

        test();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_left_exit);
    }

    private void test() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> a = new ArrayList<>();
                for (int i = 0; i < 9; ++i) {
                    a.add(String.format("当前%d", new Random().nextInt() % 999));
                }
                currentAdapter.add(a);
                a = new ArrayList<>();
                for (int i = 0; i < 20; ++i) {
                    allAdapter.add(String.format("分类%d", new Random().nextInt() % 999));
                }
                allAdapter.add(a);
            }
        }, 100);
    }

}
