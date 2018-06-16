package com.yhslib.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.config.HashMapField;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.CustomListView;
import com.yhslib.android.util.CustomScrollView;
import com.yhslib.android.util.FormatDate;
import com.yhslib.android.util.MugshotUrl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import ru.noties.markwon.Markwon;

public class PostActivity extends BaseActivity {
    private String TAG = "PostActivity";
    private String userID;
    private String token;
    private String postID;
    private TextView postContentTxt;
    private TextView postTitleTxt;
    private TextView postAuthorNickname;
    private TextView postCreatedTime;
    private TextView postViewsCount;
    private ImageView postAuthorMugshotImage;
    private CustomListView listView;
    private CustomScrollView scrollview;

    private SimpleAdapter adapter;

    private Boolean RefreshFlag = false; // 防止多次刷新标记
    private int currentPage = 1;
    private int lastPage = 1;

    private ArrayList<HashMap<String, Object>> hm = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getDataFromIntent() {
        Intent intent = getIntent();
        userID = intent.getStringExtra(IntentFields.USERID);
        token = intent.getStringExtra(IntentFields.TOKEN);
        postID = intent.getStringExtra(IntentFields.POSTID);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void findView() {
        postContentTxt = findViewById(R.id.post_content);
        postTitleTxt = findViewById(R.id.post_title);
        postAuthorNickname = findViewById(R.id.post_author_nickname);
        postAuthorMugshotImage = findViewById(R.id.post_author_mugshot);
        postViewsCount = findViewById(R.id.post_views_count);
        postCreatedTime = findViewById(R.id.post_created_time);
        listView = findViewById(R.id.reply_list);
        scrollview = findViewById(R.id.post_detail_scrollview);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
        setListViewPullListener();
    }

    @Override
    protected void initData() {
        fetchPost();
        fetchReply();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * [下拉加载下一页评论]
     * list view 下拉至底部监听与scrollview底部监听 冲突
     * 故改用scrollview 下拉加载下一页评论
     */
    private void setListViewPullListener() {
        // list view 下拉加载下一页文章
        scrollview.registerOnScrollViewScrollToBottom(new CustomScrollView.OnScrollBottomListener() {
            @Override
            public void scrollToBottom() {
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
        });
    }

    /**
     * [获取文章详情]
     */
    private void fetchPost() {
        String url = URL.Post.getPostDetail(postID);
        OkHttpUtils
                .get()
                .url(url)
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
                        loadMugshot(hm.get("mugshot").toString());
                        //TODO markdown图片无法加载
                        // RichText.fromMarkdown(hm.get("body").toString()).autoFix(true).into(postContentTxt);
                        Markwon.setMarkdown(postContentTxt, hm.get("body").toString());
                        Markwon.scheduleDrawables(postContentTxt);
                    }
                });
    }

    /**
     * [解析文章所属的评论信息]
     */
    private void fetchReply() {
        RefreshFlag = false;
        String url = URL.Post.getPostReply(postID);
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
                        Log.d(TAG, response);
                        ArrayList<HashMap<String, Object>> list = formatReplyJSON(response);
                        Log.d(TAG, list.toString());
                        hm.addAll(formatReplyJSON(response));
                        // 在请求第一页的时候初始化Adapter
                        // 其他时候更新Adapter即可
                        if (currentPage == 1) {
                            setReplyListAdapter(hm);
                            RefreshFlag = true;
                            return;
                        }
                        adapter.notifyDataSetChanged();
                        RefreshFlag = true;
                    }
                });
    }

    /**
     * [加载头像]
     *
     * @param url
     */
    private void loadMugshot(String url) {
        MugshotUrl.load(url, postAuthorMugshotImage);
    }

    /**
     * [解析文章JSON数据]
     *
     * @param response
     * @return hashMap
     */
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

    /**
     * [解析评论JSON数据]
     *
     * @param response
     * @return ArrayList
     */
    private ArrayList<HashMap<String, Object>> formatReplyJSON(String response) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject repliesObject = new JSONObject(response);
            JSONArray repliesArray = repliesObject.getJSONArray("data");
            currentPage = repliesObject.getInt("current_page");
            lastPage = repliesObject.getInt("last_page");
            for (int i = 0; i < repliesArray.length(); i++) {
                JSONObject replyObject = (JSONObject) repliesArray.opt(i);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put(HashMapField.ID, replyObject.get("id"));
                hashMap.put(HashMapField.COMMENT, currentPage + replyObject.getString("comment"));
                String time = replyObject.getString("submit_date");
                time = FormatDate.changeDate(time);
                hashMap.put(HashMapField.TIME, time);
                hashMap.put(HashMapField.LIKE, replyObject.getString("like_count"));

                JSONObject userObject = replyObject.getJSONObject("user");
                hashMap.put(HashMapField.USERID, userObject.getString("id"));
                hashMap.put(HashMapField.NICKNAME, userObject.getString("nickname"));
                resultList.add(hashMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private void setReplyListAdapter(final ArrayList<HashMap<String, Object>> list) {
        String[] from = {HashMapField.NICKNAME, HashMapField.TIME, HashMapField.COMMENT, HashMapField.LIKE};
        int[] to = {R.id.reply_nickname, R.id.reply_date, R.id.reply_detail, R.id.reply_like_count};
        adapter = new SimpleAdapter(PostActivity.this, list, R.layout.list_reply, from, to) {
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