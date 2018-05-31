package com.yhslib.android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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
    private ActionBar actionBar;
    private ImageView returnArrowImage;

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
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_email);
            returnArrowImage = findViewById(R.id.return_image); // 此image findView 只能写在这
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
        setListViewPullListener();
        returnArrowImage.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        fetchEmail();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_image:
                EmailActivity.this.finish();
                break;
        }
    }

    /**
     * [设置往下拉至底部加载更多数据]
     *
     * @param
     */
    private void setListViewPullListener() {
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

    /**
     * [获取邮箱列表]
     *
     * @param
     */
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
                        Log.d(TAG, response);
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

    /**
     * [解析JSON字符串]
     *
     * @param response
     */
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
                hashMap.put("id", emailObject.getString("id"));
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


    /**
     * [设置listAdapter]
     *
     * @param list
     */
    private void setEmailListAdapter(final ArrayList<HashMap<String, Object>> list) {
        String[] from = {"email", "primary", "verified"};
        int[] to = {R.id.email, R.id.email_primary, R.id.email_verified};
        adapter = new SimpleAdapter(EmailActivity.this, list, R.layout.list_email, from, to) {
            @Override
            public long getItemId(int position) {
                return Integer.parseInt(list.get(position).get("id").toString());
            }
        };
        listView.setAdapter(adapter);
        listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuInflater menuInflater = EmailActivity.this.getMenuInflater();
                menuInflater.inflate(R.menu.email_list_menu, menu);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        long id = info.id;
        Log.d(TAG, id + "");
        switch (item.getItemId()) {
            case R.id.set_primary:
                set_primary_email(id + "");
                break;
            case R.id.verify:
                verify_email(id + "");
                break;
            case R.id.delete:
                delete_email(id + "");
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * [设置主邮箱]
     *
     * @param emailID
     */
    private void set_primary_email(String emailID) {
        String url = URL.User.setPrimaryEmail(emailID);
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(EmailActivity.this, "设置失败，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.d(TAG, response);
                        currentPage = 1;
                        hm = new ArrayList<>();
                        fetchEmail();
                        Toast.makeText(EmailActivity.this, "设置成功!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * [删除邮箱]
     *
     * @param emailID
     */
    private void delete_email(String emailID) {
        String url = URL.User.deleteEmail(emailID);
        OkHttpUtils
                .delete()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(EmailActivity.this, "邮箱删除失败，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        currentPage = 1;
                        hm = new ArrayList<>();
                        fetchEmail();
                        Toast.makeText(EmailActivity.this, "邮箱删除成功!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * [验证邮箱]
     *
     * @param emailID
     */
    private void verify_email(String emailID) {
        String url = URL.User.deleteEmail(emailID);
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(EmailActivity.this, "邮箱验证失败，请稍后再试!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.d(TAG, response);
                        currentPage = 1;
                        hm = new ArrayList<>();
                        fetchEmail();
                        Toast.makeText(EmailActivity.this, "邮箱验证成功!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
