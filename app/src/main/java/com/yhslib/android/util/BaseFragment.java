package com.yhslib.android.util;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;


public abstract class BaseFragment extends Fragment {
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView();
        init();
        setListener();
    }

    protected abstract void setListener();

    protected abstract void findView();

    protected abstract void init();

}