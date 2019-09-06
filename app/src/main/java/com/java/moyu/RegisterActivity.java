package com.java.moyu;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;

public class RegisterActivity extends SwipeActivity {

    @BindView(R.id.username)
    TextInputEditText usernameEdit;
    @BindView(R.id.password)
    TextInputEditText passwordEdit;
    @BindView(R.id.password_confirm)
    TextInputEditText passwordConfirmEdit;
    @BindView(R.id.email)
    TextInputEditText emailEdit;
    @BindView(R.id.submit)
    Button submitButton;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.back)
    ImageButton backButton;
    @BindView(R.id.title)
    TextView title;

    @Override
    protected int getLayoutResource() {
        return R.layout.register_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        emailEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO)
                    register();
                return false;
            }
        });

        title.setText(getResources().getString(R.string.app_name) + " - " + getResources().getString(R.string.register));
    }

    void register() {
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String passwordConfirm = passwordConfirmEdit.getText().toString();
        if (!password.equals(passwordConfirm)) {
            message.setText(getResources().getString(R.string.error_confirm));
            message.setVisibility(View.VISIBLE);
            return;
        }
        message.setVisibility(View.GONE);
        String email = emailEdit.getText().toString();
        final LoadingDialog dialog = new LoadingDialog(RegisterActivity.this,
            getResources().getString(R.string.register_loading));
        User.getInstance().register(username, password, email, new User.DefaultCallback() {
            @Override
            public void error(String msg) {
                dialog.dismiss();
                message.setText(msg);
                message.setVisibility(View.VISIBLE);
            }

            @Override
            public void ok() {
                dialog.cancel();
                BasicApplication.showToast(getResources().getString(R.string.register_success));
                finish();
            }
        });
        dialog.show();
    }

}
