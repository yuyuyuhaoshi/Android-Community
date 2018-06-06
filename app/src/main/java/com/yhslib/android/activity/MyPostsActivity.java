package com.yhslib.android.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
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

public class MyPostsActivity extends BaseActivity {

    private String TAG = "MyPostsActivity";
    private String userID;
    private String token;
    private int currentPage = 1;
    private int lastPage;
    private ArrayList<HashMap<String, Object>> hm = new ArrayList<>();

    private ActionBar actionBar;
    private CustomListView listView;
    private SimpleAdapter adapter;
    private Boolean RefreshFlag = false; // 防止多次刷新标记
    private ImageView returnArrowImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_post_list;
    }

    @Override
    protected void findView() {
        listView = findViewById(R.id.my_post_list);
        actionBar = getSupportActionBar();
    }

    @Override
    protected void initView() {
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_my_post_list);
            returnArrowImage = findViewById(R.id.return_image); // 此image findView 只能写在这
        }
    }

    @Override
    protected void setListener() {
        setListViewPullListener();
        returnArrowImage.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        fetchPosts();
    }

    /**
     * [下拉加载下一页文章]
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
                fetchPosts();
                listView.onRefresh(false);
            }

            @Override
            public void onTop() {
            }
        });
    }

    /**
     * [获取文章列表]
     */
    private void fetchPosts() {
        RefreshFlag = false;
        String url = URL.User.getPosts(userID);  //URL.host + "/posts/";
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
                        hm.addAll(formatPostsJSON(response));
                        // 在请求第一页的时候初始化Adapter
                        // 其他时候更新Adapter即可
                        if (currentPage == 1) {
                            setMyPostsListAdapter(hm);
                            RefreshFlag = true;
                            return;
                        }
                        adapter.notifyDataSetChanged();
                        RefreshFlag = true;
                    }
                });
    }

    /**
     * [解析文章JSON数据]
     *
     * @param response
     * @return ArrayList
     */
    private ArrayList<HashMap<String, Object>> formatPostsJSON(String response) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            lastPage = jsonObject.getInt("last_page");
            JSONArray postsArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject postObject = (JSONObject) postsArray.opt(i);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", postObject.getString("id"));
                hashMap.put("url", postObject.getString("url"));
                hashMap.put("title", currentPage + postObject.getString("title"));

                String[] timeArray = postObject.getString("created").split("T")[0].split("-");
                String month = timeArray[1].substring(0, 1).equals("0") ? timeArray[1].substring(1, 2) : timeArray[1];
                String time = month + "月" + timeArray[2] + "日";
                hashMap.put("time", time);

                hashMap.put("views", postObject.getString("views"));
                hashMap.put("reply_count", postObject.getString("reply_count"));

                JSONArray tagsArray = postObject.getJSONArray("tags");
                String tags = "";
                for (int j = 0; j < tagsArray.length(); j++) {
                    JSONObject tagObject = (JSONObject) tagsArray.opt(j);
                    tags += tagObject.getString("name");
                    if (j + 1 != tagsArray.length()) {
                        tags += "·";
                    }
                }
                hashMap.put("tags", tags);
                resultList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * [设置文章列表listAdapter]
     *
     * @param list
     */
    private void setMyPostsListAdapter(final ArrayList<HashMap<String, Object>> list) {
        String[] from = {"title", "time", "views", "tags", "reply_count"};
        int[] to = {R.id.my_post_title, R.id.my_post_create_time, R.id.my_post_views_count, R.id.my_post_tags, R.id.my_post_reply_count};
        adapter = new SimpleAdapter(MyPostsActivity.this, list, R.layout.list_my_post, from, to) {
            @Override
            public long getItemId(int position) {
                return Integer.parseInt(list.get(position).get("id").toString());
            }
        };
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPostDetail(id);
            }
        });
    }

    /**
     * [启动文章详情页]
     *
     * @param id
     */
    private void showPostDetail(Long id) {
        Log.d(TAG, id + "");
        Intent intent = new Intent(MyPostsActivity.this, PostActivity.class);
        intent.putExtra("userID", userID);
        intent.putExtra("token", token);
        intent.putExtra("postID", id + "");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_image:
                MyPostsActivity.this.finish();
                break;
        }
    }
}