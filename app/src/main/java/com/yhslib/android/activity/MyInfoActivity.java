package com.yhslib.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.MugshotUrl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.RequestBody;

public class MyInfoActivity extends BaseActivity {
    private String TAG = "MyInfoActivity";

    private String userID;
    private String token;
    private String nickname;
    private String mugshot_url;

    private AlertDialog.Builder changeNicknameDialogBuilder, changePasswordDialogBuilder;
    private LinearLayout changeNicknameLayout, changePasswordLayout, changeEmailLayout, changeViaLayout;
    private AlertDialog changeNicknameDialog, changePasswordDialog;
    private TextView nicknameTxt;
    private ImageView myViaImage;
    private ActionBar actionBar;
    private ImageView returnArrowImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra(IntentFields.USERID);
        token = intent.getStringExtra(IntentFields.TOKEN);
        nickname = intent.getStringExtra(IntentFields.NICKNAME);
        mugshot_url = intent.getStringExtra(IntentFields.MUGSHOTURL);
        Log.d(TAG, userID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_info;
    }

    @Override
    protected void findView() {
        changeNicknameLayout = findViewById(R.id.my_info_change_nickname);
        changePasswordLayout = findViewById(R.id.my_info_change_password);
        changeEmailLayout = findViewById(R.id.my_info_change_email);
        nicknameTxt = findViewById(R.id.my_info_nickname);
        myViaImage = findViewById(R.id.my_info_via);
        changeViaLayout = findViewById(R.id.my_info_change_via);
        actionBar = getSupportActionBar();
    }

    @Override
    protected void initView() {
        nicknameTxt.setText(nickname);
        loadMugshot(mugshot_url);
        buildChangeNicknameDialog();
        buildChangePasswordDialog();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_my_info);
            returnArrowImage = findViewById(R.id.return_image); // 此image findView 只能写在这
        }
    }

    @Override
    protected void setListener() {
        changeNicknameLayout.setOnClickListener(this);
        changePasswordLayout.setOnClickListener(this);
        changeEmailLayout.setOnClickListener(this);
        returnArrowImage.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_info_change_nickname:
                changeNicknameDialog.show();
                break;
            case R.id.my_info_change_password:
                changePasswordDialog.show();
                break;
            case R.id.my_info_change_email:
                Intent intent = new Intent(MyInfoActivity.this, EmailActivity.class);
                intent.putExtra(IntentFields.USERID, userID);
                intent.putExtra(IntentFields.TOKEN, token);
                startActivity(intent);
                break;
            case R.id.return_image:
                MyInfoActivity.this.finish();
                break;
        }
    }

    /**
     * [创建更改昵称对话框]
     *
     * @param
     */
    private void buildChangeNicknameDialog() {
        // 创建更改昵称对话框
        changeNicknameDialogBuilder = new AlertDialog.Builder(MyInfoActivity.this);
        changeNicknameDialogBuilder.setTitle("修改昵称");
        final EditText edit = new EditText(MyInfoActivity.this);
        edit.setSingleLine();
        changeNicknameDialogBuilder.setView(edit);
        changeNicknameDialogBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeNickname(edit.getText().toString());
            }
        });
        changeNicknameDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        changeNicknameDialogBuilder.setCancelable(true);
        changeNicknameDialog = changeNicknameDialogBuilder.create();
        changeNicknameDialog.setCanceledOnTouchOutside(true);
    }

    /**
     * [创建更改密码对话框]
     */
    private void buildChangePasswordDialog() {
        View dialogView = LayoutInflater.from(MyInfoActivity.this).inflate(R.layout.dialog_change_password, null);
        final EditText edit_old_password = dialogView.findViewById(R.id.dialog_old_password);
        final EditText edit_new_password = dialogView.findViewById(R.id.dialog_new_password);
        final EditText edit_new_password_repeat = dialogView.findViewById(R.id.dialog_new_password_repeat);

        changePasswordDialogBuilder = new AlertDialog.Builder(MyInfoActivity.this);
        changePasswordDialogBuilder.setTitle("修改密码");


        changePasswordDialogBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!edit_new_password.getText().toString().equals(edit_new_password_repeat.getText().toString())) {
                    Toast.makeText(MyInfoActivity.this, "两遍密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
                changePassword(edit_old_password.getText().toString(), edit_new_password.getText().toString(), edit_new_password_repeat.getText().toString());
            }
        });
        changePasswordDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        changePasswordDialogBuilder.setCancelable(true);
        changePasswordDialog = changePasswordDialogBuilder.create();
        changePasswordDialog.setCanceledOnTouchOutside(true);
        changePasswordDialog.setView(dialogView);
    }

    /**
     * [加载头像]
     *
     * @param url
     */
    private void loadMugshot(String url) {
        url = URL.host + url;
        MugshotUrl.load(url, myViaImage);
    }

    /**
     * [发起更改昵称的请求]
     *
     * @param nickname
     */
    private void changeNickname(String nickname) {
        // 发起更改昵称的请求
        String url = URL.User.changeNickname(userID);

        RequestBody requestBody = new FormBody.Builder()
                .add("nickname", nickname)
                .build();

        OkHttpUtils
                .patch()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .requestBody(requestBody)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyInfoActivity.this, "昵称修改失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        HashMap<String, Object> hashMap = formatUserInfoJSON(response);
                        nicknameTxt.setText(hashMap.get("nickname").toString());
                        Toast.makeText(MyInfoActivity.this, "昵称修改成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * [解析JSON数据]
     *
     * @param response
     * @return hashMap
     */
    private HashMap<String, Object> formatUserInfoJSON(String response) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            JSONObject jsonobject = new JSONObject(response);
            hashMap.put("nickname", jsonobject.getString("nickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    /**
     * [更改密码请求]
     *
     * @param oldPassword
     * @param newPassword
     * @param newPasswordRepeat
     */
    private void changePassword(String oldPassword, String newPassword, String newPasswordRepeat) {
        String url = URL.User.changePassword();
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addParams("old_password", oldPassword)
                .addParams("new_password1", newPassword)
                .addParams("new_password2", newPasswordRepeat)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(MyInfoActivity.this, "密码修改失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Toast.makeText(MyInfoActivity.this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}