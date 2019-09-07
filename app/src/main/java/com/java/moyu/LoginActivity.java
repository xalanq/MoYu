package com.java.moyu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.Nullable;
import butterknife.BindView;

public class LoginActivity extends SwipeActivity {

    @BindView(R.id.username)
    TextInputEditText usernameEdit;
    @BindView(R.id.password)
    TextInputEditText passwordEdit;
    @BindView(R.id.submit)
    Button submitButton;
    @BindView(R.id.register)
    TextView registerButton;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.back)
    ImageButton backButton;

    @Override
    protected int getLayoutResource() {
        return R.layout.login_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_stay);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_GO)
                    login();
                return false;
            }
        });
    }

    void login() {
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        message.setVisibility(View.GONE);
        final LoadingDialog dialog = new LoadingDialog(this,
            getResources().getString(R.string.login_loading));
        User.getInstance().login(username, password, new User.DefaultCallback() {
            @Override
            public void error(String msg) {
                dialog.dismiss();
                message.setText(msg);
                message.setVisibility(View.VISIBLE);
            }

            @Override
            public void ok() {
                dialog.cancel();
                BasicApplication.showToast(getResources().getString(R.string.login_success));
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
        dialog.show();
    }

}
