package com.yhslib.android.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        getDataFromIntent();
        findView();
        initView();
        setListener();
        initData();
    }

    protected abstract void getDataFromIntent();

    protected abstract int getLayoutId();

    protected abstract void findView();

    protected abstract void initView();

    protected abstract void setListener();

    protected abstract void initData();
}

