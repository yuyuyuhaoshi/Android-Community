package com.yhslib.android.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.config.URL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class MyPostsActivity extends AppCompatActivity {

    private String TAG = "MyPostsActivity";
    private TextView textView;
    private String userID;
    private String token;
    private ArrayList<HashMap<String, Object>> hm;

    private ListView listView;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_list);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        findView();
        fetchPosts();
    }

    private void findView() {
        listView = findViewById(R.id.my_post_list);
    }

    private void fetchPosts() {
        String url = "http://api.dj-china.org/posts/"; // URL.User.getPosts(userID);
        Log.d(TAG, url);
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.d(TAG, formatPostsJSON(response).toString());
                        // formatPostsJSON(response);
                        hm = formatPostsJSON(response);
                        setMyPostsListAdapter(hm);
                    }
                });
    }

    private ArrayList<HashMap<String, Object>> formatPostsJSON(String response) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray postsArray = jsonObject.getJSONArray("data");
            for (int i = 0; i < postsArray.length(); i++) {
                JSONObject postObject = (JSONObject) postsArray.opt(i);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("_id", postObject.getString("id"));
                hashMap.put("url", postObject.getString("url"));
                hashMap.put("title", postObject.getString("title"));

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

    private void setMyPostsListAdapter(ArrayList<HashMap<String, Object>> hm) {
        String[] from = {"title", "time", "views", "tags", "reply_count"};
        int[] to = {R.id.my_post_title, R.id.my_post_create_time, R.id.my_post_views_count, R.id.my_post_tags, R.id.my_post_reply_count};
        adapter = new SimpleAdapter(MyPostsActivity.this, hm, R.layout.list_my_post, from, to);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Log.d(TAG, "position " + position);
                //Log.d(TAG, "id " + id);
                showPostDetail(id);
            }
        });
    }

    private void showPostDetail(Long id) {
        String _id = hm.get(id.intValue()).get("_id").toString();
        Log.d(TAG, _id);
    }
}
