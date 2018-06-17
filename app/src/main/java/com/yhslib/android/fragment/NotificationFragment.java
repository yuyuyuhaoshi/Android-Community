package com.yhslib.android.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.activity.MainActivity;
import com.yhslib.android.activity.PostActivity;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseFragment;
import com.yhslib.android.util.FormatDate;
import com.yhslib.android.util.NotificationRefreshListAdapter;
import com.yhslib.android.util.SimpleListView;
import com.yhslib.android.util.SlideBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class NotificationFragment extends BaseFragment implements SimpleListView.OnLoadListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private String TAG = "NotificationFragment";
    private final char FLING_CLICK = 0;
    private char flingState = FLING_CLICK;
    private SimpleAdapter adapter;
    private SimpleListView listView;
    private TextView mComment;
    private ImageView mComment_under_line;
    private TextView mAtMe;
    private ImageView mAtMe_under_line;
    private TextView mNotice;
    private ImageView mNotice_under_line;
    private ImageView dot_comment, dot_Atme, dot_notice;
    private TextView unread_comment, unread_Atme, unread_notice;
    public static final String ATME = "atme";
    public static final String COMMENT = "comment";
    public final String NOTICE = "notice";
    private View bar;
    private NotificationRefreshListAdapter mAdapter;
    private int mPage = 1;
    private int mIndex = 1;
    private View footer;
    private EditText searchText;
    private String type = COMMENT;
    private boolean isSearchTag = false;
    private int lastPage = 5;
    boolean isTypeChange = false;
    private View foreground;
    private String token;
    private ArrayList<Map<String, Object>> data = new ArrayList<>();

    private boolean flag = false; // 是否请求成功的标记
    private static int requests = 0;

    public static NotificationFragment newInstance(String token) {
        Bundle args = new Bundle();
        args.putString(IntentFields.TOKEN, token);
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void getDataFromBundle() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notification;
    }


    @Override
    protected void setListener() {
        barListener();
        setListViewListener();
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            token = bundle.getString(IntentFields.TOKEN);
        }
    }

    @Override
    protected void findView() {
        listView = view.findViewById(R.id.notification_list_view);
        footer = view.findViewById(R.id.footer_layout);
        listView.setFooter(footer);
        mComment = view.findViewById(R.id.comment);
        mComment_under_line = view.findViewById(R.id.comment_under_line);
        mAtMe = view.findViewById(R.id.atMe);
        mAtMe_under_line = view.findViewById(R.id.atMe_under_line);
        mNotice = view.findViewById(R.id.notice);
        mNotice_under_line = view.findViewById(R.id.notice_under_line);
        bar = view.findViewById(R.id.bar);
        dot_comment = view.findViewById(R.id.dot_comment);
        dot_Atme = view.findViewById(R.id.dot_atMe);
        dot_notice = view.findViewById(R.id.dot_notice);
        unread_comment = view.findViewById(R.id.unread_comment);
        unread_Atme = view.findViewById(R.id.unread_atMe);
        unread_notice = view.findViewById(R.id.unread_notice);
    }

    @Override
    protected void initView() {
        setComment();
        mAdapter = new NotificationRefreshListAdapter(getActivity(), COMMENT);
        listView.setAdapter(mAdapter);
        listView.setOnLoadListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        onLoad(true);
        getNotification("like");
        getNotification("reply");
    }

    /**
     * [设置列表的监听（因多次调整，只剩下滑动时向上移动的监听）]
     */
    private void setListViewListener() {
        assert ((MainActivity) getActivity()) != null;
        SlideBar slideBar = new SlideBar(bar, ((BottomNavigationView) getActivity().findViewById(R.id.navigation)), listView);
        slideBar.SetSlideBar();
    }

    /**
     * [设置上面的选择标签的监听]
     */
    private void barListener() {
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTypeChange = true;
                setComment();
                onLoad(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onLoad(true);
            }
        });
        mAtMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTypeChange = true;
                setAtMe();
                onLoad(true);
            }
        });
        mNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTypeChange = true;
                setNotice();
                onLoad(true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onLoad(true);
            }
        });
    }

    /**
     * [初始化评论页面]
     */
    private void setComment() {
        type = COMMENT;
        mComment.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
        mComment_under_line.setVisibility(View.VISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mAtMe_under_line.setVisibility(View.INVISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mNotice_under_line.setVisibility(View.INVISIBLE);
    }

    /**
     * [初始化@我页面]
     */
    private void setAtMe() {
        type = ATME;
        mComment.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mComment_under_line.setVisibility(View.INVISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
        mAtMe_under_line.setVisibility(View.VISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mNotice_under_line.setVisibility(View.INVISIBLE);
    }

    /**
     * [初始化通知页面]
     */
    private void setNotice() {
        type = NOTICE;
        mComment.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mComment_under_line.setVisibility(View.INVISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mAtMe_under_line.setVisibility(View.INVISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
        mNotice_under_line.setVisibility(View.VISIBLE);
    }

    /**
     * [此方法废弃]
     */
    public ArrayList<Map<String, Object>> getData() {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map;
        map = new HashMap<>();
        if (type.equals(COMMENT)) {
            for (int i = 0; i < 15; i++) {
                String replay_text, text_my_comment;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                replay_text = "给你续一秒给你续一秒给你续一秒给你续一秒给你续一秒";
                if (replay_text.length() >= 13) {
                    replay_text = replay_text.substring(0, 13) + "...";
                }
                map.put("replay_text", replay_text);
                text_my_comment = "苟利国家生死以，岂因祸福避趋之";
                if (text_my_comment.length() >= 12) {
                    text_my_comment = text_my_comment.substring(0, 12) + "...";
                }
                map.put("text_my_comment", text_my_comment);
                data.add(map);
            }
        } else if (type.equals(ATME)) {
            for (int i = 0; i < 15; i++) {
                String text_my_comment;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                String tittle = getString(R.string.test_article_tittle);
                if (tittle.length() >= 10) {
                    tittle = tittle.substring(0, 10) + "...";
                }
                map.put("replay_article", tittle);
                text_my_comment = "苟利国家生死以，岂因祸福避趋之";
                if (text_my_comment.length() >= 12) {
                    text_my_comment = text_my_comment.substring(0, 12) + "...";
                }
                map.put("text_my_comment", text_my_comment);
                data.add(map);
            }
        } else {
            for (int i = 0; i < 15; i++) {
                String replay_text;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                replay_text = "苟利国家生死以，岂因祸福避趋之,苟利国家生死以，岂因祸福避趋之";
                if (replay_text.length() >= 18) {
                    replay_text = replay_text.substring(0, 18) + "...";
                }
                map.put("replay_text", replay_text);
                data.add(map);
            }
        }
        return data;
    }

    /**
     * [对从服务器获取的通知的json数据进行解析]
     *
     * @param response（从服务器获取的json数据）
     * @return date (解析后的数据)
     */
    public ArrayList<Map<String, Object>> resolveNotificationJson(String response) {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONTokener jsonTokener = new JSONTokener(jsonObject.getString("data"));
            JSONArray notificationArray = (JSONArray) jsonTokener.nextValue();
            lastPage = jsonObject.getInt("last_page");
            for (int i = 0; i < 2; i++) {
                map = new HashMap<>();
                String replay_text, replay_article;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                if (i == 1) {
                    map.put("replay_avatar", R.drawable.hand_image9);
                }
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                replay_text = "给你续一秒给你续一秒给你续一秒给你续一秒给你续一秒,苟利国家生死以，岂因祸福避趋之";
                map.put("replay_text", replay_text);
                replay_article = "苟利国家生死以，岂因祸福避趋之,苟利国家生死以，岂因祸福避趋之";
                map.put("replay_article", replay_article);
                map.put("unread", false);
                map.put("id", "999999");
                map.put("post_id", "0000");
                data.add(map);
            }
            for (int i = 0; i < notificationArray.length(); i++) {
                map = new HashMap<>();
                String replay_date;
                JSONObject jsonNotification = notificationArray.getJSONObject(i);
                JSONObject jsonAuthor = jsonNotification.getJSONObject("actor");
                switch (i % 10) {
                    case 0:
                        map.put("replay_avatar", R.drawable.jerry_zheng);
                        break;
                    case 1:
                        map.put("replay_avatar", R.drawable.hand_image1);
                        break;
                    case 2:
                        map.put("replay_avatar", R.drawable.hand_image2);
                        break;
                    case 3:
                        map.put("replay_avatar", R.drawable.hand_image3);
                        break;
                    case 4:
                        map.put("replay_avatar", R.drawable.hand_image4);
                        break;
                    case 5:
                        map.put("replay_avatar", R.drawable.hand_image5);
                        break;
                    case 6:
                        map.put("replay_avatar", R.drawable.hand_image5);
                        break;
                    case 7:
                        map.put("replay_avatar", R.drawable.hand_image6);
                        break;
                    case 8:
                        map.put("replay_avatar", R.drawable.hand_image7);
                        break;
                    case 9:
                        map.put("replay_avatar", R.drawable.hand_image8);
                        break;
                    case 10:
                        map.put("replay_avatar", R.drawable.hand_image9);
                        break;
                    default:
                        map.put("replay_avatar", R.drawable.jerry_zheng);
                        break;
                }
                map.put("replay_name", jsonAuthor.getString("nickname"));
                replay_date = FormatDate.changeDate(jsonNotification.getString("timestamp"));
                map.put("replay_date", replay_date);
                JSONObject jsonReplay = jsonNotification.getJSONObject("reply");
                map.put("replay_text", jsonReplay.getString("comment"));
                JSONObject jsonPost = jsonNotification.getJSONObject("post");
                map.put("replay_article", jsonPost.getString("post_title"));
                map.put("unread", jsonNotification.getBoolean("unread"));
                map.put("id", jsonNotification.getString("id"));
                map.put("post_id", jsonPost.getString("post_id"));
                data.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * [从服务器获取通知的json数据]
     *
     * @param verb (通知类型)
     * @param page （获取第几页通知）
     * @return date (解析后的数据)
     */
    private ArrayList<Map<String, Object>> getNotification(String verb, int page) {
        String url = URL.Notification.getNotification();
        GetBuilder builder = OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token);
        if (verb != null) {
            builder.addParams("verb", verb);
        }
        if (page != -1) {
            builder.addParams("page", String.valueOf(page));
        }
        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getCommunityPosts()" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        data = resolveNotificationJson(response);
                    }
                });
        return data;
    }

    /**
     * [新建线程，向服务器请求数据]
     *
     * @param verb (通知类型)
     * @param page （获取第几页通知）
     */
    private void setNotification(final int page, final String verb) {
        flag = false;
        listView.postDelayed(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                requests++;
                cycleRun(page, verb);
            }
        }, 500);
    }

    /**
     * [将请求结果处理方法抽取出来，以便失败时，请求再次读取]
     *
     * @param verb (通知类型)
     * @param page （获取第几页通知）
     */
    private void cycleRun(int page, String verb) {
        List<RefreshListItem> data = new LinkedList<>();
        RefreshListItem item;
        Log.d(TAG, "cycleRun: " + requests);
        if (requests > 10) {//防止因为网络问题过多次请求
            return;
        }
        ArrayList<Map<String, Object>> data1 = getNotification(verb, -1);
        for (Map<String, Object> map : data1) {
            item = new RefreshListItem();
            item.replay_avatar = String.valueOf(map.get("replay_avatar"));
            item.replay_name = String.valueOf(map.get("replay_name"));
            item.replay_date = String.valueOf(map.get("replay_date"));
            item.replay_text = String.valueOf(map.get("replay_text"));
            item.replay_article = String.valueOf(map.get("replay_article"));
            item.isUnread = (boolean) map.get("unread");
            item.id = String.valueOf(map.get("id"));
            item.postId = String.valueOf(map.get("post_id"));
            requests = 0;
            flag = true;
            mIndex++;
            data.add(item);
        }
        if (isTypeChange) {
            mAdapter = new NotificationRefreshListAdapter(getActivity(), type);
            listView.setAdapter(mAdapter);
            isTypeChange = false;
        }
        mAdapter.setData(data, page == 1 ? true : false);
        listView.finishLoad(page == lastPage ? true : false);
        if (!flag) {//如果数据没有获取成功那么重新获取一次
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setNotification(page, verb);
        }
    }

    @Override
    public void onLoad(boolean isRefresh) {
        if (isRefresh) {
            mPage = 1;
        } else {
            mPage++;
        }
        if (type.equals(COMMENT)) {
            setNotification(mPage, "reply");
        } else {
            setNotification(mPage, "like");
        }
        getNotification("like");
        getNotification("reply");
    }

    public static class RefreshListItem {
        public String replay_avatar;
        public String replay_name;
        public String replay_date;
        public String replay_text;
        public String replay_article;
        public String id;
        public String postId;
        public boolean isUnread;
    }

    /**
     * [复写点击方法，在点击item时候，跳转到对应的文章页面]
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        Toast.makeText(getContext(), "点击了" + pos + " ", Toast.LENGTH_SHORT).show();
        Log.v("MY_TAG", "onItemClick: state=" + flingState + ", pos=" + pos);
        int fetch = 0;
        final ListView mListView = listView.getmListView();
        if (mListView.getLastVisiblePosition() >= mListView.getChildCount())//get到的child只能是屏幕显示的，如第100个child，在屏幕里面当前是第2个，那么应当是第二个child而非100
        {
            fetch = mListView.getChildCount() - 1 - (mListView.getLastVisiblePosition() - pos);
        } else {
            fetch = pos;
        }
        final View item;
        item = mListView.getChildAt(fetch);
        TextView notificationId;
        notificationId = item.findViewById(R.id.post_id);
        showPostDetail(Long.valueOf(notificationId.getText().toString()));
    }

    /**
     * [启动文章详情页]
     *
     * @param id
     */
    private void showPostDetail(Long id) {
        Log.d(TAG, id + "");
        Intent intent = new Intent(getContext(), PostActivity.class);
        intent.putExtra(IntentFields.TOKEN, token);
        intent.putExtra(IntentFields.POSTID, id + "");
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (foreground != null) {
            closeMenu(foreground);//如果已经有菜单被展开那么关闭它
        }
        int fetch = 0;
        final ListView mListView = listView.getmListView();
        if (mListView.getLastVisiblePosition() >= mListView.getChildCount())//get到的child只能是屏幕显示的，如第100个child，在屏幕里面当前是第2个，那么应当是第二个child而非100
        {
            fetch = mListView.getChildCount() - 1 - (mListView.getLastVisiblePosition() - position);
        } else {
            fetch = position;
        }
        final View item;
        item = mListView.getChildAt(fetch);
        foreground = item.findViewById(R.id.foreground);
        Animation open = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_open_menu);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(open);
        animationSet.setFillAfter(true);
        foreground.startAnimation(animationSet);

        final TextView read, delete, notificationId;
        notificationId = item.findViewById(R.id.id);
        read = item.findViewById(R.id.read_notification);
        delete = item.findViewById(R.id.delete_notification);
        read.setClickable(true);
        delete.setClickable(true);
        final int finalFetch = fetch;
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu(foreground);
                read.setClickable(false);
                delete.setClickable(false);
                item.findViewById(R.id.red_dot).setVisibility(View.INVISIBLE);
                foreground = null;
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu(foreground);
                read.setClickable(false);
                delete.setClickable(false);
                deleteNotification(notificationId.getText().toString());
                View[] views = new View[]{mListView.getChildAt(finalFetch + 1), mListView.getChildAt(finalFetch + 2), mListView.getChildAt(finalFetch + 3)};
                NewThread newThread = new NewThread(item, views);
                newThread.start();
                listView.getmOnLoadListener().onLoad(true);
                foreground = null;
            }
        });
        return true;
    }

    /**
     * [收起菜单的的效果（即移动菜单前面的前景）]
     *
     * @param foreground （菜单前面需要收起的前景）
     */
    private void closeMenu(View foreground) {
        Animation close = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_close_menu);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(close);
        animationSet.setFillAfter(true);
        if (foreground != null)
            foreground.startAnimation(animationSet);
    }

    /**
     * [删除时的效果（向上移动）]
     *
     * @param view （删除的item）
     */
    private void deleteAnim(View view) {
        Animation close = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_delete);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(close);
        if (view != null)
            view.startAnimation(animationSet);
    }

    /**
     * [删除时的效果（向下移动）]
     *
     * @param view （删除的item）
     */
    private void deleteAnimUp(View view) {
        Animation up = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_delete_up);
        Animation down = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_delete_down);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(up);
        animationSet.addAnimation(down);
        if (view != null)
            view.startAnimation(animationSet);
    }

    /**
     * [设置未读通知的的个数]
     */
    private void getNotification(String verb) {
        String url = URL.Notification.getNotification();
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .addParams("unread", "true")
                .addParams("verb", verb)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, ".setUnread()" + e.getMessage());
                        unread_comment.setVisibility(View.INVISIBLE);
                        dot_comment.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int countComment = jsonObject.getInt("count");
                            if (countComment > 0) {
                                unread_comment.setVisibility(View.VISIBLE);
                                dot_comment.setVisibility(View.VISIBLE);
                                if (countComment > 100) {
                                    unread_comment.setText("99+");
                                } else
                                    unread_comment.setText(String.valueOf(countComment));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            unread_comment.setVisibility(View.INVISIBLE);
                            dot_comment.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    /**
     * [删除通知]
     *
     * @param id （删除的通知的id）
     */
    private void deleteNotification(String id) {
        String url = URL.Notification.deleteNotification(id);
        OkHttpUtils
                .delete()
                .url(url)
                .addHeader("Authorization", "Bearer " + token)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, ".setUnread()" + e.getMessage());
                        unread_comment.setVisibility(View.INVISIBLE);
                        dot_comment.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(getContext(), "删除成功" + response + ":" + id, Toast.LENGTH_SHORT).show();
                        onLoad(true);
                    }
                });
    }

    /**
     * [开启一个新线程，用以播放删除动画]
     */
    class NewThread extends Thread {
        View view;
        View[] views;

        public NewThread(View view, View[] views) {
            this.view = view;
            this.views = views;
        }

        public void run() {
            deleteAnim(view);
            for (View v : views
                    ) {
                deleteAnimUp(v);
            }
        }
    }
}
