package com.java.moyu;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

public class CategoryFragment extends BasicFragment {

    @BindView(R.id.category_edit_current) TextView editCurrent;
    @BindView(R.id.category_close) ImageButton btnClose;
    private ChipAdapter allAdapter;
    private ChipAdapter currentAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.category_fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allAdapter = ChipAdapter.newAdapter(view.getContext(), view.findViewById(R.id.category_all_layout), new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                currentAdapter.add(allAdapter.get(position));
                allAdapter.remove(position);
            }

            @Override
            public void close(Chip chip, int position) {
            }
        });

        currentAdapter = ChipAdapter.newAdapter(view.getContext(), view.findViewById(R.id.category_current_layout), new ChipAdapter.OnClick() {
            @Override
            public void click(Chip chip, int position) {
                Toast.makeText(getActivity(), "click chip", Toast.LENGTH_SHORT).show();
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
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        test();
    }

    private void test() {
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

}
