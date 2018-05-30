package com.yhslib.android.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.SimpleAdapter;

import com.yhslib.android.R;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.CustomListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class EmailActivity extends BaseActivity {
    private String TAG = "EmailActivity";
    private String userID;
    private String token;
    private int currentPage = 1;
    private int lastPage;

    private ArrayList<HashMap<String, Object>> hm = new ArrayList<>();

    private CustomListView listView;
    private SimpleAdapter adapter;
    private Boolean RefreshFlag = false; // 防止多次刷新标记

    @Override
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_email_list;
    }

    @Override
    protected void findView() {
        listView = findViewById(R.id.email_list);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
        setListViewListener();
    }

    @Override
    protected void initData() {
        fetchEmail();
    }

    @Override
    public void onClick(View v) {

    }

    private void setListViewListener() {
        // list view 下拉加载下一页文章
        listView.setOnPullToRefreshListener(new CustomListView.OnPullToRefreshListener() {
            @Override
            public void onBottom() {
                if (!RefreshFlag) {
                    return;
                }
                if (currentPage == lastPage) {
                    return;
                }
                listView.onRefresh(true);
                currentPage += 1;
                fetchEmail();
                listView.onRefresh(false);
            }

            @Override
            public void onTop() {
            }
        });
    }

    private void fetchEmail() {
        RefreshFlag = false;
        String url = URL.User.getEmailList();
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addParams("page", currentPage + "")
                .addParams("page_size", "20")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, formatEmailsJSON(response).toString());
                        hm.addAll(formatEmailsJSON(response));
                        // 在请求第一页的时候初始化Adapter
                        // 其他时候更新Adapter即可
                        if (currentPage == 1) {
                            setEmailListAdapter(hm);
                            RefreshFlag = true;
                            return;
                        }
                        adapter.notifyDataSetChanged();
                        RefreshFlag = true;
                    }
                });
    }

    private ArrayList<HashMap<String, Object>> formatEmailsJSON(String response) {
        RefreshFlag = false;
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            lastPage = jsonObject.getInt("last_page");
            JSONArray emailsArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < emailsArray.length(); i++) {
                JSONObject emailObject = (JSONObject) emailsArray.opt(i);
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put("email", currentPage + "." + emailObject.getString("email"));

                String primary = emailObject.getString("primary");
                if (primary.equals("true")) {
                    hashMap.put("primary", getResources().getString(R.string.primary_email));
                } else {
                    hashMap.put("primary", "");
                }

                String verified = emailObject.getString("verified");
                if (verified.equals("false")) {
                    hashMap.put("verified", getResources().getString(R.string.unverified));
                } else {
                    hashMap.put("verified", "");
                }

                resultList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private void setEmailListAdapter(ArrayList<HashMap<String, Object>> list) {
        String[] from = {"email", "primary", "verified"};
        int[] to = {R.id.email, R.id.email_primary, R.id.email_verified};
        adapter = new SimpleAdapter(EmailActivity.this, list, R.layout.list_email, from, to);
        listView.setAdapter(adapter);

    }
}
