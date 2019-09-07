package com.java.moyu;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserActivity extends SwipeActivity {

    @BindView(R.id.user_toolbar)
    Toolbar toolbar;
    @BindView(R.id.username)
    TextInputEditText usernameEdit;
    @BindView(R.id.email)
    TextInputEditText emailEdit;
    @BindView(R.id.avatar)
    TextInputEditText avatarEdit;
    @BindView(R.id.avatar_view)
    CircleImageView avatarView;
    @BindView(R.id.submit)
    Button submitButton;
    @BindView(R.id.logout)
    Button logoutButton;
    @BindView(R.id.message)
    TextView message;

    @Override
    protected int getLayoutResource() {
        return R.layout.user_activity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        usernameEdit.setText(User.getInstance().getUsername());
        emailEdit.setText(User.getInstance().getEmail());
        avatarEdit.setText(User.getInstance().getAvatar());
        Glide.with(this).load(User.getInstance().getAvatar())
            .placeholder(R.drawable.loading_cover)
            .error(R.drawable.default_avatar).centerCrop()
            .into(avatarView);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String avatar = avatarEdit.getText().toString();
                message.setVisibility(View.GONE);
                User.getInstance().editUserInfo(avatar, new User.DefaultCallback() {
                    @Override
                    public void error(String msg) {
                        message.setText(msg);
                        message.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void ok() {
                        setResult(1);
                        finish();
                    }
                });
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.getInstance().logout();
                setResult(2);
                BasicApplication.showToast(getString(R.string.logout_success));
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}
