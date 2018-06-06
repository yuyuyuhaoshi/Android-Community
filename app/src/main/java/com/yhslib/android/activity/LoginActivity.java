package com.yhslib.android.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.fragment.LoginFragment;
import com.yhslib.android.fragment.RegisterFragment;
import com.yhslib.android.util.BaseFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private TextView register_textview;
    private TextView login_textview;
    private View login_underline;
    private View register_underline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = LoginFragment.newInstance();
        ft.replace(R.id.login_title, fragment);
        ft.commit();
        findView();
        setListener();

    }

    private void findView() {
        login_textview = findViewById(R.id.login_textview);
        register_textview = findViewById(R.id.register_textview);
        login_underline = findViewById(R.id.login_underline);
        register_underline = findViewById(R.id.register_underline);
    }

    private void setListener() {
        login_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = LoginFragment.newInstance();
                ft.replace(R.id.login_title, fragment);
                ft.commit();
                login_underline.setBackground(getResources().getDrawable((R.drawable.line)));
                register_underline.setBackground(getResources().getDrawable((R.drawable.line_unchecked)));
            }
        });
        register_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = RegisterFragment.newInstance();
                ft.replace(R.id.login_title, fragment);
                ft.commit();
                login_underline.setBackground(getResources().getDrawable((R.drawable.line_unchecked)));
                register_underline.setBackground(getResources().getDrawable((R.drawable.line)));
            }
        });

    }

}

