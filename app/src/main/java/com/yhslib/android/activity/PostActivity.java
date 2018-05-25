package com.yhslib.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhslib.android.R;

import com.yhslib.android.config.URL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zzhoujay.richtext.RichText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import ru.noties.markwon.Markwon;

public class PostActivity extends AppCompatActivity {
    private String TAG = "PostActivity";
    private String userID;
    private String token;
    private String postID;
    private TextView postContentTxt;
    private TextView postTitleTxt;
    private TextView postAuthorNickname;
    private TextView postCreatedTime;
    private TextView postViewsCount;
    private ImageView postAuthorMugshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        token = intent.getStringExtra("token");
        postID = intent.getStringExtra("postID");
        findView();
        fetchPost();
        //fetchReply();
        Log.d(TAG, postID);
    }

    private void findView() {
        postContentTxt = findViewById(R.id.post_content);
        postTitleTxt = findViewById(R.id.post_title);
        postAuthorNickname = findViewById(R.id.post_author_nickname);
        postAuthorMugshot = findViewById(R.id.post_author_mugshot);
        postViewsCount = findViewById(R.id.post_views_count);
        postCreatedTime = findViewById(R.id.post_created_time);
    }

    private void fetchPost() {
        String url = URL.Post.getPostDetail(postID);
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
                        HashMap<String, Object> hm = formatPostJSON(response);
                        //Log.d(TAG, hm.get("body").toString());
                        postTitleTxt.setText(hm.get("title").toString());
                        postAuthorNickname.setText(hm.get("nickname").toString());
                        postViewsCount.setText(hm.get("views").toString());
                        postCreatedTime.setText(hm.get("time").toString());
                        loadMugshotUrl(hm.get("mugshot").toString());
                        //TODO markdown图片无法加载
                        // RichText.fromMarkdown(hm.get("body").toString()).autoFix(true).into(postContentTxt);
                        Markwon.setMarkdown(postContentTxt, hm.get("body").toString());
                        Markwon.scheduleDrawables(postContentTxt);
                    }
                });
    }

    private void fetchReply() {
        String url = URL.Post.getPostReply(postID);
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
                        // TODO 层级回复
                        //formatReplyJSON
                        // Log.d(TAG, response);
                    }
                });
    }

    private void loadMugshotUrl(String url) {
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        postAuthorMugshot.setImageBitmap(response);
                    }
                });
    }

    private HashMap<String, Object> formatPostJSON(String response) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            JSONObject postObject = new JSONObject(response);
            hashMap.put("title", postObject.getString("title"));
            hashMap.put("body", postObject.getString("body"));
            hashMap.put("views", postObject.getString("views"));

            String[] timeArray = postObject.getString("created").split("T")[0].split("-");
            String month = timeArray[1].substring(0, 1).equals("0") ? timeArray[1].substring(1, 2) : timeArray[1];
            String time = month + "月" + timeArray[2] + "日";
            hashMap.put("time", time);

            JSONObject authorObject = postObject.getJSONObject("author");
            hashMap.put("nickname", authorObject.getString("nickname"));
            hashMap.put("mugshot", authorObject.getString("mugshot"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private ArrayList<HashMap<String, Object>> formatReplyJSON(String response) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject repliesObject = new JSONObject(response);
            JSONArray repliesArray = repliesObject.getJSONArray("data");
            for (int i = 0; i < repliesArray.length(); i++) {
                JSONObject replyObject = (JSONObject) repliesArray.opt(i);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("comment", replyObject.getString("comment"));
            }
            } catch(JSONException e){
                e.printStackTrace();
            }
            return resultList;
        }
    }
