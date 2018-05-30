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
import com.yhslib.android.activity.Welcome;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFragment extends Fragment {
    private String TAG = "LoginFragment";
    private EditText login_edt_name,login_edt_password;
    private Button login_button;
    private View view;
    private void findView(){

        login_edt_name=view.findViewById(R.id.login_edt_name);
        login_edt_password=view.findViewById(R.id.login_edt_password);
        login_button=view.findViewById(R.id.login_button);
    }
    public static LoginFragment newInstance() {
        Bundle args = new Bundle();
        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
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
    private void setOnClickListener(){
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = login_edt_name.getText().toString();
                String password = login_edt_password.getText().toString();
                if(name.equals("")||password.equals("")){
                    Toast.makeText(getActivity(),"账号或密码为空，请重新输入!",Toast.LENGTH_LONG).show();
                }else{
                    if(isEmail(name)){//判断是不是邮箱登录
                        if (true) {//登录验证模块
                            startActivity(new Intent(getActivity(), Welcome.class));
                            getActivity().finish();
                        } else{
                            Toast.makeText(getActivity(),"账号或密码错误，请重新输入!",Toast.LENGTH_LONG).show();
                            login_edt_name.setText("");
                            login_edt_password.setText("");
                        }
                    }else {//用户名登录
                        if (true) {//登录验证模块
                            String Token ="{\n" +
                                    "    \"token\": \"this is your jwt token\",\n" +
                                    "    \"user\": {\n" +
                                    "        \"id\": 1,\n" +
                                    "        \"username\": \"用户名\",\n" +
                                    "        \"nickname\": \"用户昵称\",\n" +
                                    "        \"email\": \"用户邮箱@gmail.com\",\n" +
                                    "        \"date_joined\": \"2018-04-20T05:35:26.866393+08:00\",\n" +
                                    "        \"mugshot_url\": \"/media/mugshots/用户名/default_mugshot.png\",\n" +
                                    "        \"ip_joined\": null,\n" +
                                    "        \"last_login_ip\": \"127.0.0.1\",\n" +
                                    "        \"is_superuser\": false,\n" +
                                    "        \"is_staff\": false,\n" +
                                    "        \"post_count\": 0,\n" +
                                    "        \"reply_count\": 0\n" +
                                    "    }\n" +
                                    "}";//从服务器端获取Token值
                            try {
                                JSONObject TokenObject=new JSONObject(Token);
                                String user =TokenObject.getString("user");
                                JSONObject userObject = new JSONObject(user);
                                SharedPreferences preferences;
                                preferences=getActivity().getSharedPreferences("loginToken", Context.MODE_PRIVATE);
                                SharedPreferences.Editor edit= preferences.edit();
                                edit.putString("username",userObject.getString("username"));
                                edit.putString("email",userObject.getString("email"));
                                edit.putString("Token",TokenObject.getString("token"));
                                edit.commit();//保存Token
                                preferences.getString("email","null");
                                Toast.makeText(getActivity(),preferences.getString("email","null"),Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                Toast.makeText(getActivity(),"666",Toast.LENGTH_LONG).show();

                                e.printStackTrace();
                            }



//                            startActivity(new Intent(getActivity(), Welcome.class));
//                            getActivity().finish();
                        } else{
                            Toast.makeText(getActivity(),"账号或密码错误，请重新输入!",Toast.LENGTH_LONG).show();
                            login_edt_name.setText("");
                            login_edt_password.setText("");
                        }
                    }
                }

            }
        });


    }
    public static  boolean isEmail(String name){
        String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

}