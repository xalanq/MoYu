package com.java.moyu;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

public class LoadingDialog extends Dialog {

    private String text;

    public LoadingDialog(Context context, String text) {
        super(context);
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        TextView textView = findViewById(R.id.loading_text);
        textView.setText(text);
    }

}
