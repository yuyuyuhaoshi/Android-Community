package com.yhslib.android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseActivity;
import com.yhslib.android.util.CustomListView;
import com.yhslib.android.util.CustomScrollView;
import com.yhslib.android.util.FormatDate;
import com.yhslib.android.util.MugshotUrl;
import com.yhslib.android.util.Reply;
import com.yhslib.android.util.ReplyListAdapter;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    private LinearLayout commentPostLayout;
    private CustomListView listView;
    private CustomScrollView scrollview;

    private ReplyListAdapter adapter;
    private AlertDialog.Builder commentDialogBuilder;
    private AlertDialog commentDialog;

    private Boolean RefreshFlag = false; // 防止多次刷新标记
    private int currentPage = 1;
    private int lastPage = 1;
    private Boolean canLikeFlag = true;

    private ArrayList<Reply> hm = new ArrayList<>();

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
        Log.d(TAG, token);
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
        commentPostLayout = findViewById(R.id.comment_post_layout);
    }

    @Override
    protected void initView() {
        getSupportActionBar().hide();
        buildCommentDialog();
    }

    @Override
    protected void setListener() {
        setListViewPullListener();
        commentPostLayout.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        fetchPost();
        fetchReply();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_post_layout:
                commentDialog.show();
                break;
        }
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
     * 获取文章所属的评论信息
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
     * 请求点赞
     * flag 保证不会多次请求
     * 在请求开始标记为false
     * 在响应结束标记为true
     *
     * @param replyID  点赞评论的id
     * @param position 记录在ArrayList中的位置
     */
    private void handleLikeReply(String replyID, final int position) {
        Log.d(TAG, "like");
        if (!canLikeFlag) {
            Toast.makeText(PostActivity.this, "请勿多次请求!", Toast.LENGTH_SHORT).show();
            return;
        }
        canLikeFlag = false;
        String url = URL.Post.likeReply(replyID);
        final Reply reply = hm.get(position);
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        canLikeFlag = true;
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(PostActivity.this, "已经赞过!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 刷新ArrayList
                        String like = reply.getLike();
                        int l = Integer.parseInt(like) + 1;
                        reply.setLike(l + "");
                        hm.set(position, reply);
                        adapter.notifyDataSetChanged();

                        canLikeFlag = true;
                        Toast.makeText(PostActivity.this, "点赞成功!", Toast.LENGTH_SHORT).show();
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
     * @param response 相应数据
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
     * 解析评论JSON数据
     *
     * @param response 相应数据
     * @return ArrayList
     */
    private ArrayList<Reply> formatReplyJSON(String response) {
        ArrayList<Reply> resultList = new ArrayList<>();
        try {
            JSONObject repliesObject = new JSONObject(response);
            JSONArray repliesArray = repliesObject.getJSONArray("data");
            currentPage = repliesObject.getInt("current_page");
            lastPage = repliesObject.getInt("last_page");
            for (int i = 0; i < repliesArray.length(); i++) {
                JSONObject replyObject = (JSONObject) repliesArray.opt(i);

                Reply reply = new Reply();
                reply.setId(Integer.parseInt(replyObject.get("id").toString()));
                reply.setComment(replyObject.getString("comment"));

                // 设置随机头像
                Random r = new Random();
                int s = r.nextInt();
                reply.setMugshot(s % 3 == 0 ? R.mipmap.via : (s % 3 == 1 ? R.drawable.jerry_zheng : R.drawable.hand_image8));

                reply.setLike(replyObject.getString("like_count"));
                String time = replyObject.getString("submit_date");
                time = FormatDate.changeDate(time);
                reply.setDate(time);

                JSONObject userObject = replyObject.getJSONObject("user");
                reply.setUserID(Integer.parseInt(userObject.getString("id")));
                reply.setNickname(userObject.getString("nickname"));


                resultList.add(reply);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 自定义adapter重写
     *
     * @param list reply的ArrayList
     */
    private void setReplyListAdapter(final ArrayList<Reply> list) {
        adapter = new ReplyListAdapter(this, list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {
                view.findViewById(R.id.like_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleLikeReply(id + "", position);
                        Log.d(TAG, id + "");
                    }
                });
            }
        });
    }

    /**
     * 评论的对话框
     */
    private void buildCommentDialog() {
        commentDialogBuilder = new AlertDialog.Builder(PostActivity.this);
        commentDialogBuilder.setTitle("评论");
        final EditText editText = new EditText(PostActivity.this);
        commentDialogBuilder.setView(editText);
        commentDialogBuilder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handleCommentPost(editText.getText().toString());
            }
        });
        commentDialogBuilder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        commentDialogBuilder.setCancelable(true);
        commentDialog = commentDialogBuilder.create();
        commentDialog.setCanceledOnTouchOutside(true);
    }

    private void handleCommentPost(String comment) {
        String url = URL.Post.comment();
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addHeader("Content-Type", "application/json")
                .addParams("object_pk", postID)
                .addParams("comment", comment)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                        Toast.makeText(PostActivity.this, "评论失败!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, response);
                        hm.add(0, formatSingleCommentJSON(response));
                        adapter.notifyDataSetChanged();
                        Toast.makeText(PostActivity.this, "评论成功!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private Reply formatSingleCommentJSON(String response) {
        Reply reply = new Reply();
        try {
            JSONObject replyObject = new JSONObject(response);
            reply.setId(Integer.parseInt(replyObject.get("id").toString()));
            reply.setComment(replyObject.getString("comment"));

            // 设置随机头像
            Random r = new Random();
            int s = r.nextInt();
            reply.setMugshot(s % 3 == 0 ? R.mipmap.via : (s % 3 == 1 ? R.drawable.jerry_zheng : R.drawable.hand_image8));

            reply.setLike(0 + "");
            String time = replyObject.getString("submit_date");
            time = FormatDate.changeDate(time);
            reply.setDate(time);

            JSONObject userObject = replyObject.getJSONObject("user");
            reply.setUserID(Integer.parseInt(userObject.getString("id")));
            reply.setNickname(userObject.getString("nickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reply;
    }
}