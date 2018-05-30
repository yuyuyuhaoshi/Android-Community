package com.yhslib.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.yhslib.android.R;

/**
 * Created by jerryzheng on 2018/5/25.
 */

public class Welcome extends AppCompatActivity {
    TextView textView;
    AnimationSet animationSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.welcome);
        textView=findViewById(R.id.text_welcome);
        animation();
    }

    private void animation() {
        Animation welcome_alpha = AnimationUtils.loadAnimation(Welcome.this, R.anim.welcome_alpha);
        Animation welcome_translate = AnimationUtils.loadAnimation(Welcome.this, R.anim.welcome_translate);
        Animation welcome_scale = AnimationUtils.loadAnimation(Welcome.this, R.anim.welcome_scale);
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(welcome_alpha);
        animationSet.addAnimation(welcome_translate);
        animationSet.addAnimation(welcome_scale);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(Welcome.this, MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textView.startAnimation(animationSet);
    }
}