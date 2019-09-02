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

import androidx.annotation.Nullable;
import butterknife.BindView;

public class CategoryActivity extends BasicActivity {

    @BindView(R.id.category_edit_current)
    TextView editCurrent;
    @BindView(R.id.category_close)
    ImageButton btnClose;
    private ChipAdapter remainAdapter;
    private ChipAdapter currentAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.category_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        remainAdapter = ChipAdapter.newAdapter(this, findViewById(R.id.category_remain_layout), new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                currentAdapter.add(remainAdapter.get(position));
                remainAdapter.remove(position);
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
                remainAdapter.add(currentAdapter.get(position));
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
                    NewsDatabase.getInstance().updateCategory(currentAdapter.getData(), remainAdapter.getData());
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

        initData();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
        overridePendingTransition(R.anim.slide_stay, R.anim.slide_left_exit);
    }

    void initData() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                currentAdapter.add(NewsDatabase.getInstance().queryCategory(1));
                remainAdapter.add(NewsDatabase.getInstance().queryCategory(0));
            }
        });
    }

}
