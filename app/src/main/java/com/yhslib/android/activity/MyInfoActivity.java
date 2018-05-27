package com.yhslib.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.MugshotUrl;

public class MyInfoActivity extends BaseActivity {
    private String TAG = "MyInfoActivity";

    private String userID;
    private String token;
    private String nickname;
    private String mugshot_url;

    private EditText edit;
    private AlertDialog.Builder builder;
    private LinearLayout changeNicknameLayout;
    private AlertDialog changeNicknameDialog;
    private TextView nicknameTxt;
    private ImageView myViaImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        nickname = intent.getStringExtra("nickname");
        mugshot_url = intent.getStringExtra("mugshot_url");
        Log.d(TAG, userID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_info;
    }

    @Override
    protected void findView() {
        changeNicknameLayout = findViewById(R.id.my_info_change_nickname);
        nicknameTxt = findViewById(R.id.my_info_nickname);
        myViaImage = findViewById(R.id.my_info_via);
    }

    @Override
    protected void initView() {
        nicknameTxt.setText(nickname);
        loadMugshot(mugshot_url);
        buildChangeNicknameDialog();
    }

    @Override
    protected void setListener() {
        changeNicknameLayout.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_info_change_nickname:
                changeNicknameDialog.show();
        }
    }


    private void buildChangeNicknameDialog() {
        // 创建更改昵称对话框
        builder = new AlertDialog.Builder(MyInfoActivity.this);
        builder.setTitle("请输入新的昵称");
        edit = new EditText(MyInfoActivity.this);
        builder.setView(edit);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MyInfoActivity.this, "你输入的是: " + edit.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setCancelable(true);
        changeNicknameDialog = builder.create();
        changeNicknameDialog.setCanceledOnTouchOutside(true);
    }

    private void loadMugshot(String url) {
        url = URL.host + url;
        MugshotUrl.load(url, myViaImage);
    }
}
