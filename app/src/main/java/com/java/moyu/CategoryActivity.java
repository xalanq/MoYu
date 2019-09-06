package com.java.moyu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.List;

import butterknife.BindView;

public class CategoryActivity extends SwipeActivity {

    @BindView(R.id.category_edit_current)
    TextView editCurrent;
    @BindView(R.id.category_close)
    ImageButton btnClose;
    @BindView(R.id.category_current_layout)
    RecyclerView currentView;
    boolean hasEdited = false;
    int selectPosition = -1;
    private ChipAdapter remainAdapter;
    private ChipAdapter currentAdapter;
    private ItemTouchHelper itemTouchHelper;

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
                hasEdited = true;
                User.getInstance().updateCategory(currentAdapter.getData(), remainAdapter.getData(), new User.DefaultCallback() {
                    @Override
                    public void error(String msg) {
                        BasicApplication.showToast(msg);
                    }

                    @Override
                    public void ok() {

                    }
                });
            }

            @Override
            public void close(Chip chip, int position) {
            }
        });

        currentAdapter = ChipAdapter.newAdapter(this, currentView, new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                selectPosition = position;
                finish();
            }

            @Override
            public void close(Chip chip, int position) {
                remainAdapter.add(currentAdapter.get(position));
                currentAdapter.remove(position);
            }
        });

        currentView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (currentAdapter.isEditable())
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

        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                if (currentAdapter.isEditable()) {
                    return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
                }
                return 0;
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                if (currentAdapter.isEditable()) {
                    currentAdapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });
        itemTouchHelper.attachToRecyclerView(currentView);

        editCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAdapter.toggleEdit()) {
                    editCurrent.setText(getResources().getText(R.string.complete));
                } else {
                    editCurrent.setText(getResources().getText(R.string.edit));
                    hasEdited = true;
                    User.getInstance().updateCategory(currentAdapter.getData(), remainAdapter.getData(), new User.DefaultCallback() {
                        @Override
                        public void error(String msg) {
                            BasicApplication.showToast(msg);
                        }

                        @Override
                        public void ok() {

                        }
                    });
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initData();
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("hasEdited", hasEdited);
        data.putExtra("selectPosition", selectPosition);
        setResult(RESULT_OK, data);
        super.finish();
    }

    void initData() {
        User.getInstance().getCategory(new User.CategoryCallback() {
            @Override
            public void error(String msg) {
                BasicApplication.showToast(msg);
            }

            @Override
            public void ok(List<String> chosen, List<String> remain) {
                currentAdapter.add(chosen);
                remainAdapter.add(remain);
            }
        });
    }

}
