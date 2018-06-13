package com.yhslib.android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.yhslib.android.R;
import com.yhslib.android.db.UserDao;
import com.yhslib.android.util.BaseActivity;

public class SettingsActivity extends BaseActivity {
    private String TAG = "SettingsActivity";
    private ActionBar actionBar;
    private ImageView returnArrowImage;
    private Button logoutImage;

    @Override
    protected void getDataFromIntent() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void findView() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_settings);
            returnArrowImage = findViewById(R.id.return_image);
        }
        logoutImage = findViewById(R.id.logout_button);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
        returnArrowImage.setOnClickListener(this);
        logoutImage.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_image:
                SettingsActivity.this.finish();
                break;
            case R.id.logout_button:
                Log.d(TAG, "Click");
                logout();
                break;
        }
    }

    private void logout() {
        UserDao dao = new UserDao(getApplicationContext());
        dao.deleteAllUser();
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
