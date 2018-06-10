package com.yhslib.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseFragment;
import com.yhslib.android.util.Email;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

public class RegisterFragment extends BaseFragment {
    private String TAG = "RegisterFragment";
    private EditText usernameEdt;
    private EditText passwordEdt;
    private EditText repeatPasswordEdt;
    private EditText emailEdt;
    private Button OKBtn;

    public static RegisterFragment newInstance() {
        Bundle args = new Bundle();
        RegisterFragment fragment = new RegisterFragment();
        fragment.setArguments(args);
        return fragment;
    }


    protected void getDataFromBundle() {


    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected void findView() {
        usernameEdt = view.findViewById(R.id.register_username);
        passwordEdt = view.findViewById(R.id.register_password);
        repeatPasswordEdt = view.findViewById(R.id.register_repeat_password);
        emailEdt = view.findViewById(R.id.register_email);
        OKBtn = view.findViewById(R.id.register_ok);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
        OKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdt.getText().toString();
                String password = passwordEdt.getText().toString();
                String repeatPassword = repeatPasswordEdt.getText().toString();
                String email = emailEdt.getText().toString();
                if (username.equals("") || password.equals("") || repeatPassword.equals("") || email.equals("")) {
                    Toast.makeText(getActivity(), "字段为空，请重新输入!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!password.equals(repeatPassword)) {
                    Toast.makeText(getActivity(), "密码不一致，请重新输入!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Email.isEmail(email)) {
                    Toast.makeText(getActivity(), "邮箱格式错误，请重新输入!", Toast.LENGTH_LONG).show();
                    return;
                }
                handleRegistration(username, password, repeatPassword, email);
            }
        });
    }

    @Override
    protected void initData() {

    }


    /**
     * 点击注册，发送请求
     *
     * @param username  用户名
     * @param password1 密码
     * @param password2 重复密码
     * @param email     邮箱地址
     */
    private void handleRegistration(String username, String password1, String password2, String email) {
        String url = URL.User.registration();
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username", username)
                .addParams("password1", password1)
                .addParams("password2", password2)
                .addParams("email", email)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(getActivity(), "注册失败，请重新注册!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, response);
                        Toast.makeText(getActivity(), "注册成功，请登录!", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
