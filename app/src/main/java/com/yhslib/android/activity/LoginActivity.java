package com.yhslib.android.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.fragment.LoginFragment;
import com.yhslib.android.fragment.RegisterFragment;
import com.yhslib.android.util.ActivityContainer;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.BaseFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends BaseActivity {
    private TextView register_textview;
    private TextView login_textview;
    private View login_underline;
    private View register_underline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Window window = getWindow();
//        //隐藏标题栏
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        //隐藏状态栏
//        //定义全屏参数
//        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
//        //设置当前窗体为全屏显示
//        window.setFlags(flag, flag);

        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);  //隐藏标题栏
            getWindow().setStatusBarColor(Color.TRANSPARENT);//隐藏状态栏
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        FragmentManager fm = getSupportFragmentManager();//3.0以下版本,没有fragment的api,所以用getSupportFragmentManager间接获取FragmentManager()对象。
        FragmentTransaction ft = fm.beginTransaction();//FragmentTransaction可以在运行时添加，删除或替换Fragment
        Fragment fragment = LoginFragment.newInstance();
        ft.replace(R.id.login_title, fragment);
        ft.commit();//提交修改
        findView();
    }

    @Override
    protected void getDataFromIntent() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login_main;
    }

    @Override
    protected void findView() {
        login_textview = findViewById(R.id.login_textview);
        register_textview = findViewById(R.id.register_textview);
        login_underline = findViewById(R.id.login_underline);
        register_underline = findViewById(R.id.register_underline);
    }

    @Override
    protected void initView() {
    //初始视图(界面控件等)
    }

    @Override
    protected void setListener() {
        login_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//login
                FragmentManager fm = getSupportFragmentManager();
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
            public void onClick(View view) {//register
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = RegisterFragment.newInstance();
                ft.replace(R.id.login_title, fragment);
                ft.commit();
                login_underline.setBackground(getResources().getDrawable((R.drawable.line_unchecked)));//给View设置背景图片
                register_underline.setBackground(getResources().getDrawable((R.drawable.line)));
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onBackPressed() {
        exit_dialog();//,判断Fragment栈里面有没有回退.
    }

    private void exit_dialog() {
        new AlertDialog.Builder(this).setMessage("您确定要退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityContainer.getInstance().finishAllActivity();
            }
        }).setNegativeButton("取消", null).show();
    }

    @Override
    public void onClick(View v) {

    }
}

