package com.yhslib.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.activity.MainActivity;
import com.yhslib.android.config.HashMapField;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseFragment;
import com.yhslib.android.util.Email;
import com.yhslib.android.util.JWTUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;

import com.yhslib.android.db.*;

public class LoginFragment extends BaseFragment {
    private String TAG = "LoginFragment";
    private EditText login_edt_name, login_edt_password;
    private Button login_button;

    public static LoginFragment newInstance() {//静态工厂方法,初始化fragment的参数，然后返回新的fragment到调用者
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void getDataFromBundle() {

    }

    @Override
    protected void findView() {
        login_edt_name = view.findViewById(R.id.login_edt_name);
        login_edt_password = view.findViewById(R.id.login_edt_password);
        login_button = view.findViewById(R.id.login_button);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView();
        setOnClickListener();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    private void setOnClickListener() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameOrEmail = login_edt_name.getText().toString();
                String password = login_edt_password.getText().toString();
                boolean bool;

                if (usernameOrEmail.equals("") || password.equals("")) {
                    Toast.makeText(getActivity(), "账号或密码为空，请重新输入!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (Email.isEmail(usernameOrEmail)) {
                    bool = false;
                } else {
                    bool = true;
                }
                handleLogin(usernameOrEmail, password, bool);
            }

        });
    }

    private void handleLogin(String name, String password, boolean bool) {
        String url = URL.User.login();
        Log.d(TAG, url);
        OkHttpUtils
                .post()
                .url(url)
                .addParams(bool ? "username" : "email", name)
                .addParams("password", password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(getActivity(), "账号或密码错误，请重新输入!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 存在网络被墙的问题 获取到193什么乱七八糟的主页 无法解析JSON键值对
                        try {
                            HashMap<String, Object> hashMap = formatUserInfo(response);
                            Log.d(TAG, response);
                            String token = hashMap.get(HashMapField.TOKEN).toString();
                            String userid = hashMap.get(HashMapField.USERID).toString();
                            String username = hashMap.get(HashMapField.USERNAME).toString();
                            HashMap<String, Object> tokenHashMap = JWTUtils.decoded(token);
                            String exp = tokenHashMap.get(HashMapField.EXP).toString();
                            Date day = new Date();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String time = df.format(day);

                            UserDao dao = new UserDao(getContext());
                            if (dao.searchUserById(userid)) {
                                dao.updateUser(userid, token, time, exp);
                            } else {
                                // 保证数据库只有一条用户记录
                                dao.deleteAllUser();
                                dao.insertUser(userid, username, token, time, exp);
                            }

                            startMainActivity(userid, token);
                        } catch (NullPointerException e) {
                            Toast.makeText(getActivity(), "登录失败，请检查网络环境!", Toast.LENGTH_LONG).show();
                        }

                    }
                });
    }

    private HashMap<String, Object> formatUserInfo(String response) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            JSONObject jsonobject = new JSONObject(response);
            hashMap.put(HashMapField.TOKEN, jsonobject.getString("token"));
            JSONObject user = jsonobject.getJSONObject("user");
            hashMap.put(HashMapField.USERID, user.getString("id"));
            hashMap.put(HashMapField.USERNAME, user.getString("username"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private void startMainActivity(String userid, String token) {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(IntentFields.USERID, userid);
        intent.putExtra(IntentFields.TOKEN, token);
        startActivity(intent);
    }
}

