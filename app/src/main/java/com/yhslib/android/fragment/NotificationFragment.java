package com.yhslib.android.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.activity.MainActivity;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseFragment;
import com.yhslib.android.util.FormatDate;
import com.yhslib.android.util.NotificationRefreshListAdapter;
import com.yhslib.android.util.SimpleListView;
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

public class NotificationFragment extends BaseFragment implements SimpleListView.OnLoadListener, AdapterView.OnItemClickListener {
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6InVzZXIzIiwib3JpZ19pYXQiOjE1MjgxMDg5MTMsImV4cCI6MTUyODE5NTMxMywidXNlcl9pZCI6NCwiZW1haWwiOiJ1c2VyM0BleGFtcGxlLmNvbSJ9.AlkrsiMPBxFxrM0ozh7NxfyTLUj3fFrRiw41QRxabec";
    private String TAG = "NotificationFragment";
    private View view;
    private SimpleAdapter adapter;
    private SimpleListView listView;
    private TextView mComment;
    private ImageView mComment_under_line;
    private TextView mAtMe;
    private ImageView mAtMe_under_line;
    private TextView mNotice;
    private ImageView mNotice_under_line;
    private ListView mNotification_list_view;
    private ImageView mReplay_avatar;
    private TextView mReplay_name;
    private TextView mReplay_date;
    private TextView mReplay_text;
    private TextView mFinal_text_my_comment;
    private TextView mText_my_comment;
    private TextView mSee_details;
    public static final String ATME = "atme";
    public static final String COMMENT = "comment";
    public final String NOTICE = "notice";
    private View bar;
    private NotificationRefreshListAdapter mAdapter;
    private int mPage = 1;
    private int mIndex = 1;
    private View footer;
    EditText searchText;
    String type = COMMENT;
    private boolean isSearchTag = false;
    int lastPage = 5;
    boolean isTypeChange=false;
    public static NotificationFragment newInstance() {
        Bundle args = new Bundle();
        NotificationFragment fragment = new NotificationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_notification, container, false);
        return view;
    }


    @Override
    protected void setListener() {
        barListener();
        setListViewListener();
    }

    @Override
    protected void findView() {
        listView = view.findViewById(R.id.notification_list_view);
//        mReplay_avatar = (ImageView) view.findViewById(R.id.replay_avatar);
//        mReplay_name = (TextView) view.findViewById(R.id.replay_name);
//        mReplay_date = (TextView) view.findViewById(R.id.replay_date);
//        mReplay_text = (TextView) view.findViewById(R.id.replay_text);
//        mFinal_text_my_comment = (TextView) view.findViewById(R.id.final_text_my_comment);
//        mText_my_comment = (TextView) view.findViewById(R.id.text_my_comment);
//        mSee_details = (TextView) view.findViewById(R.id.see_details);
        footer = view.findViewById(R.id.footer_layout);
        listView.setFooter(footer);
        mComment = (TextView) view.findViewById(R.id.comment);
        mComment_under_line = (ImageView) view.findViewById(R.id.comment_under_line);
        mAtMe = (TextView) view.findViewById(R.id.atMe);
        mAtMe_under_line = (ImageView) view.findViewById(R.id.atMe_under_line);
        mNotice = (TextView) view.findViewById(R.id.notice);
        mNotice_under_line = (ImageView) view.findViewById(R.id.notice_under_line);
        bar = view.findViewById(R.id.bar);
    }

    @Override
    protected void init() {
        setComment();
        mAdapter = new NotificationRefreshListAdapter(getActivity(), COMMENT);
        listView.setAdapter(mAdapter);
        listView.setOnLoadListener(this);
        listView.setOnItemClickListener(this);
        onLoad(true);
    }

    private void setListViewListener() {
        assert ((MainActivity) getActivity()) != null;
//        SlideBar slideBar= new SlideBar(bar,((BottomNavigationView)getActivity().findViewById(R.id.navigation)),listView);
//        slideBar.SetSlideBar();
    }

    private void barListener() {
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoad(true);
                isTypeChange=true;
                setComment();
            }
        });
        mAtMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoad(true);
                isTypeChange=true;
                setAtMe();
            }
        });
        mNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoad(true);
                isTypeChange=true;
                setNotice();
            }
        });
    }

    private void setComment() {
//        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text", "text_my_comment"};
//        int[] to = {R.id.replay_avatar, R.id.replay_name, R.id.replay_date, R.id.replay_text, R.id.text_my_comment};
        type = COMMENT;
//        adapter = new SimpleAdapter(getActivity(), getData(), R.layout.notification_comment, from, to);
//        listView.setAdapter(adapter);
        mComment.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
        mComment_under_line.setVisibility(View.VISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mAtMe_under_line.setVisibility(View.INVISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mNotice_under_line.setVisibility(View.INVISIBLE);
    }

    private void setAtMe() {
//        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_article", "text_my_comment"};
//        int[] to = {R.id.replay_avatar, R.id.replay_name, R.id.replay_date, R.id.replay_article, R.id.text_my_comment};
        type = ATME;
//        adapter = new SimpleAdapter(getActivity(), getData(), R.layout.notification_atme, from, to);
//        listView.setAdapter(adapter);
        mComment.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mComment_under_line.setVisibility(View.INVISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
        mAtMe_under_line.setVisibility(View.VISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mNotice_under_line.setVisibility(View.INVISIBLE);
    }

    private void setNotice() {
//        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text"};
//        int[] to = {R.id.replay_avatar, R.id.replay_name, R.id.replay_date, R.id.replay_text};
        type = NOTICE;
//        adapter = new SimpleAdapter(getActivity(), getData(), R.layout.notification_notice, from, to);
//        listView.setAdapter(adapter);
        mComment.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mComment_under_line.setVisibility(View.INVISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorSecondaryText));
        mAtMe_under_line.setVisibility(View.INVISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryText));
        mNotice_under_line.setVisibility(View.VISIBLE);
    }

    public ArrayList<Map<String, Object>> getData() {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map;
        map = new HashMap<>();
//        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text", "text_my_comment"};
        if (type.equals(COMMENT)) {
            for (int i = 0; i < 15; i++) {
                String replay_avatar, replay_name, replay_date, replay_text, text_my_comment;
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
//            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_article", "text_my_comment"};
            for (int i = 0; i < 15; i++) {
                String replay_avatar, replay_name, replay_date, replay_article, text_my_comment;
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
            //            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text"};
            for (int i = 0; i < 15; i++) {
                String replay_avatar, replay_name, replay_date, replay_text;
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
                String replay_avatar, replay_name, replay_date, replay_text, replay_article;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                replay_text = "给你续一秒给你续一秒给你续一秒给你续一秒给你续一秒,苟利国家生死以，岂因祸福避趋之";
                map.put("replay_text", replay_text);
                replay_article = "苟利国家生死以，岂因祸福避趋之,苟利国家生死以，岂因祸福避趋之";
                map.put("replay_article", replay_article);
                data.add(map);
            }
            for (int i = 0; i < notificationArray.length(); i++) {
                map = new HashMap<>();
                String replay_date;
                JSONObject jsonNotification = notificationArray.getJSONObject(i);
                JSONObject jsonAuthor = jsonNotification.getJSONObject("actor");
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", jsonAuthor.getString("nickname"));
                replay_date= FormatDate.changeDate(jsonNotification.getString("timestamp"));
                map.put("replay_date", replay_date);
                JSONObject jsonReplay = jsonNotification.getJSONObject("reply");
                map.put("replay_text", jsonReplay.getString("comment"));
                JSONObject jsonPost = jsonNotification.getJSONObject("post");
                map.put("replay_article", jsonPost.getString("post_title"));
                data.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

    ArrayList<Map<String, Object>> data = new ArrayList<>();
    private ArrayList<Map<String, Object>> getNotification(String verb, int page) {
        String url = URL.Notification.getNotification();
        Log.d(TAG, url);
        GetBuilder builder = OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + TOKEN);
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
//                        String[] from = {"tittle", "name", "date", "tag", "image"};
//                        int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
//                        adapter = new SimpleAdapter(getActivity(), data, R.layout.article_list, from, to);
//                        mContentRlv.setAdapter(adapter);
//                        Log.d(TAG, "onResponse: " + data);
                    }
                });
        return data;
    }

    boolean flag = false;
    private void setNotification(final int page) {
        flag = false;
//        footer.setVisibility(View.VISIBLE);
        listView.postDelayed(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                cycleRun(page);
            }
        }, 500);
    }

    private void cycleRun(int page){
        List<RefreshListItem> data = new LinkedList<>();
        RefreshListItem item;
        ArrayList<Map<String, Object>> data1 = getNotification(null,-1);
        for (Map<String, Object> map : data1
                ) {
            item = new RefreshListItem();
            item.replay_avatar = String.valueOf(map.get("replay_avatar"));
            item.replay_name = String.valueOf(map.get("replay_name"));
            item.replay_date = String.valueOf(map.get("replay_date"));
            item.replay_text = String.valueOf(map.get("replay_text"));
            item.replay_article = String.valueOf(map.get("replay_article"));
            flag = true;
            mIndex++;
            data.add(item);
        }
        if (isTypeChange){
            mAdapter = new NotificationRefreshListAdapter(getActivity(), type);
            listView.setAdapter(mAdapter);
            isTypeChange=false;
        }
        mAdapter.setData(data, page == 1 ? true : false);
        listView.finishLoad(page == lastPage ? true : false);
        if (!flag) {//如果数据没有获取成功那么重新获取一次
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setNotification(page);
        }
    }

    @Override
    public void onLoad(boolean isRefresh) {
        if (isRefresh) {
            mPage = 1;
        } else {
            mPage++;
        }
        setNotification(mPage);
        //TODO
    }

    public static class RefreshListItem {
        public String replay_avatar;
        public String replay_name;
        public String replay_date;
        public String replay_text;
        public String replay_article;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), "点击了" + position + " ", Toast.LENGTH_SHORT).show();
    }
}
