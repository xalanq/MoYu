package com.java.moyu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public abstract class BasicFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutResource() != 0) {
            View view = inflater.inflate(getLayoutResource(), container, false);
            ButterKnife.bind(this, view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected abstract int getLayoutResource();

}
