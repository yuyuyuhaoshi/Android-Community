package com.yhslib.android.activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.yhslib.android.R;
import com.yhslib.android.config.HashMapField;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.CustomListView;
import com.yhslib.android.util.FormatDate;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class MyReplyActivity extends BaseActivity {
    private String TAG = "MyReplyActivity";
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
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra(IntentFields.USERID);
        token = intent.getStringExtra(IntentFields.TOKEN);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_reply_list;
    }

    @Override
    protected void findView() {
        listView = findViewById(R.id.reply_list);
        actionBar = getSupportActionBar();
    }

    @Override
    protected void initView() {
        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(R.layout.actionbar_my_reply_list);
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
        fetchReply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.return_image:
                MyReplyActivity.this.finish();
                break;
        }
    }

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
                fetchReply();
                listView.onRefresh(false);
            }

            @Override
            public void onTop() {
            }
        });
    }

    private void fetchReply() {
        RefreshFlag = false;
        String url = URL.User.getReplyList(userID);
        OkHttpUtils
                .get()
                .url(url)
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
                        hm.addAll(formatReplyJSON(response));
                        // 在请求第一页的时候初始化Adapter
                        // 其他时候更新Adapter即可
                        if (currentPage == 1) {
                            setMyReplyListAdapter(hm);
                            RefreshFlag = true;
                            return;
                        }
                        adapter.notifyDataSetChanged();
                        RefreshFlag = true;
                    }
                });
    }

    /**
     * [解析评论JSON数据]
     *
     * @param
     * @return ArrayList
     */
    private ArrayList<HashMap<String, Object>> formatReplyJSON(String response) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            lastPage = jsonObject.getInt("last_page");
            currentPage = jsonObject.getInt("current_page");
            JSONArray replyArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < replyArray.length(); i++) {
                JSONObject replyObject = (JSONObject) replyArray.get(i);
                HashMap<String, Object> hashMap = new HashMap<>();

                hashMap.put(HashMapField.ID, replyObject.getString("id"));
                hashMap.put(HashMapField.COMMENT, replyObject.getString("comment"));

                String time = replyObject.getString("submit_date");
                time = FormatDate.changeDate(time);
                hashMap.put(HashMapField.TIME, time);

                // 文章作者信息
                try {
                    JSONObject parentUserObject = replyObject.getJSONObject("parent_user");
                    hashMap.put(HashMapField.USERID, parentUserObject.getString("id"));
                    hashMap.put(HashMapField.NICKNAME, parentUserObject.getString("nickname"));
                } catch (JSONException e) {
                    JSONObject parentUserObject = replyObject.getJSONObject("user");
                    hashMap.put(HashMapField.USERID, parentUserObject.getString("id"));
                    hashMap.put(HashMapField.NICKNAME, parentUserObject.getString("nickname"));
                }

                // 文章信息
                JSONObject postObject = replyObject.getJSONObject("post");
                hashMap.put(HashMapField.POST_TITLE, postObject.getString("title"));
                hashMap.put(HashMapField.POST_ID, postObject.getString("id"));

                resultList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private void setMyReplyListAdapter(final ArrayList<HashMap<String, Object>> list) {
        String[] from = {HashMapField.COMMENT, HashMapField.POST_TITLE, HashMapField.NICKNAME, HashMapField.TIME};
        int[] to = {R.id.comment, R.id.post_title, R.id.post_author_nickname, R.id.reply_date};
        adapter = new SimpleAdapter(MyReplyActivity.this, list, R.layout.list_my_reply, from, to) {
            @Override
            public long getItemId(int position) {
                return Integer.parseInt(list.get(position).get(HashMapField.ID).toString());
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // showPostDetail(id);
            }
        });
    }
}
