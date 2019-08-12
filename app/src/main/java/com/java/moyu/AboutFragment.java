package com.java.moyu;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

/**
 * 设置碎片
 */
class AboutFragment extends BasicFragment {

    public AboutFragment() {
        super();
        setTitleId(R.string.about_fragment_title);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_fragment, container, false);
        TextView author = view.findViewById(R.id.about_author);
        author.setText(R.string.app_author);
        author.setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }
}
