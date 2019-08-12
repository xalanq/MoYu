package com.java.moyu;

import androidx.fragment.app.Fragment;

class BasicFragment extends Fragment {
    private int titleId;

    void setTitleId(int titleId) {
        this.titleId = titleId;
    }

    int getTitleId() {
        return titleId;
    }
}
