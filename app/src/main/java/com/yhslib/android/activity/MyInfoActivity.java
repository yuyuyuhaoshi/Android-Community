package com.yhslib.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yhslib.android.R;

public class MyInfoActivity extends AppCompatActivity {
    private String TAG = "MyInfoActivity";

    private String userID;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        Log.d(TAG, userID);
    }
}
