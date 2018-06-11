package com.yhslib.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.config.HashMapField;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.db.DatabaseFiled;
import com.yhslib.android.db.UserDao;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.JWTUtils;

import java.util.HashMap;


public class WelcomeActivity extends BaseActivity {
    private String TAG = "WelcomeActivity";
    TextView textView;
    AnimationSet animationSet;
    UserDao dao;

    @Override
    protected void getDataFromIntent() {

    }

    @Override
    protected int getLayoutId() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        return R.layout.welcome;
    }

    @Override
    protected void findView() {
        textView = findViewById(R.id.text_welcome);
    }

    @Override
    protected void initView() {
        animation();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    private void animation() {
        Animation welcome_alpha = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.welcome_alpha);
        Animation welcome_translate = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.welcome_translate);
        Animation welcome_scale = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.welcome_scale);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(welcome_alpha);
        animationSet.addAnimation(welcome_translate);
        animationSet.addAnimation(welcome_scale);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            int intentFlag;   // 1跳转到登录注册页，2跳转到首页
            HashMap<String, Object> hashMap = new HashMap<>();

            @Override
            public void onAnimationStart(Animation animation) {
                dao = new UserDao(getApplicationContext());
                Cursor cursor = dao.selectUser();


                if (cursor.getCount() == 0) {
                    intentFlag = 1;
                    return;
                }
                cursor.moveToFirst();

                String userid = cursor.getString(cursor.getColumnIndex(DatabaseFiled.User.USERID));
                String token = cursor.getString(cursor.getColumnIndex(DatabaseFiled.User.TOKEN));
                String exp = cursor.getString(cursor.getColumnIndex(DatabaseFiled.User.TIMESTAMP));
                hashMap.put(HashMapField.USERID, userid);
                hashMap.put(HashMapField.TOKEN, token);
                hashMap.put(HashMapField.EXP, exp);
                Log.d(TAG, hashMap.toString());
                Boolean bool = JWTUtils.inspectToken(hashMap, WelcomeActivity.this);
                if (bool) {
                    intentFlag = 2;
                } else {
                    intentFlag = 1;
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (intentFlag == 2) {
                    Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                    intent.putExtra(IntentFields.TOKEN, hashMap.get(HashMapField.TOKEN).toString());
                    intent.putExtra(IntentFields.USERID, hashMap.get(HashMapField.USERID).toString());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textView.startAnimation(animationSet);
    }

    @Override
    public void onClick(View v) {

    }
}