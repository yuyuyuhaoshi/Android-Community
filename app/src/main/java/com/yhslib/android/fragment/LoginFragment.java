package com.yhslib.android.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.activity.MainActivity;
import com.yhslib.android.activity.Welcome;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.MugshotUrl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.internal.Internal;
import com.yhslib.android.db.*;

public class LoginFragment extends Fragment {
    private String TAG = "LoginFragment";
    private EditText login_edt_name, login_edt_password;
    private Button login_button;
    private View view;
    private String token = "";


    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public boolean isEmail(String name) {
        String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    private void findView() {
        login_edt_name = view.findViewById(R.id.login_edt_name);
        login_edt_password = view.findViewById(R.id.login_edt_password);
        login_button = view.findViewById(R.id.login_button);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_login, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findView();
        setOnClickListener();
        Log.d(TAG, TAG);

    }

    private void setOnClickListener() {
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameOrEmail = login_edt_name.getText().toString();
                String password = login_edt_password.getText().toString();
                boolean bool;
              //  Intent intent = getActivity().getIntent();

                if (usernameOrEmail.equals("") || password.equals("")) {
                    Toast.makeText(getActivity(), "账号或密码为空，请重新输入!", Toast.LENGTH_LONG).show();
                    return;
                }
                if (isEmail(usernameOrEmail)) {
                    bool = false;
                } else {
                    bool = true;
                }
                handleLogin(usernameOrEmail, password, bool);
            }

        });
    }

    private void handleLogin(String name, String password, boolean bool) {
        OkHttpUtils
                .post().url(URL.User.login())
                .addParams(bool ? "username" : "email", name)
                .addParams("password", password)
                .addParams("token", token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(getActivity(), "账号或密码错误，请重新输入!", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, response);
                        String userid="";
                        String username="";
                        Date day=new Date();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String timestamp=df.format(day);
                        //System.out.println(df.format(day));
                        try {
                            JSONObject jsonobject = new JSONObject(response);
                            token = jsonobject.getString("token");
//                            userid=jsonobject.getString("id");
//                            username=jsonobject.getString("username");
                            JSONObject user = jsonobject.getJSONObject("user");
                            userid=user.getString("id");
                            username=user.getString("username");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG,token);
                        Log.d(TAG,userid);
                        Intent intent=new Intent(getActivity(), MainActivity.class);
                        intent.putExtra(token,"token");
                        UserDao dao=new UserDao(getContext());

                        if(dao.search(userid)){
                            dao.updateLogin(token,timestamp);
                        }else{
                            dao.insertLogin(userid,username,token,timestamp);
                        }
                        startActivity(intent);
                    }
                });
    }
}

