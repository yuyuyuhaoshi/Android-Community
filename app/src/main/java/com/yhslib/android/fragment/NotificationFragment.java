package com.yhslib.android.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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

public class NotificationFragment extends BaseFragment implements SimpleListView.OnLoadListener, AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener {
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6InVzZXIzQGV4YW1wbGUuY29tIiwiZXhwIjoxNTI4MzY5MjQ4LCJvcmlnX2lhdCI6MTUyODI4Mjg0OCwidXNlcl9pZCI6NCwidXNlcm5hbWUiOiJ1c2VyMyJ9.JsV7AxMa968nHykMV_RWLdG9WhCdSePa16ijKMxliaM";
    private String TAG = "NotificationFragment";
    private final char FLING_CLICK = 0;
    private final char FLING_LEFT = 1;
    private final char FLING_RIGHT = 2;
    private char flingState = FLING_CLICK;
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
    EditText searchText;
    String type = COMMENT;
    private boolean isSearchTag = false;
    int lastPage = 5;
    boolean isTypeChange = false;
    private View foreground;

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
        footer = view.findViewById(R.id.footer_layout);
        listView.setFooter(footer);
        mComment = (TextView) view.findViewById(R.id.comment);
        mComment_under_line = (ImageView) view.findViewById(R.id.comment_under_line);
        mAtMe = (TextView) view.findViewById(R.id.atMe);
        mAtMe_under_line = (ImageView) view.findViewById(R.id.atMe_under_line);
        mNotice = (TextView) view.findViewById(R.id.notice);
        mNotice_under_line = (ImageView) view.findViewById(R.id.notice_under_line);
        bar = view.findViewById(R.id.bar);
        dot_comment = view.findViewById(R.id.dot_comment);
        dot_Atme = view.findViewById(R.id.dot_atMe);
        dot_notice = view.findViewById(R.id.dot_notice);
        unread_comment = view.findViewById(R.id.unread_comment);
        unread_Atme = view.findViewById(R.id.unread_atMe);
        unread_notice = view.findViewById(R.id.unread_notice);
    }

    @Override
    protected void init() {
        setComment();
        mAdapter = new NotificationRefreshListAdapter(getActivity(), COMMENT);
        listView.setAdapter(mAdapter);
        listView.setOnLoadListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        onLoad(true);
        setUnread();
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
                isTypeChange = true;
                setComment();
            }
        });
        mAtMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoad(true);
                isTypeChange = true;
                setAtMe();
            }
        });
        mNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoad(true);
                isTypeChange = true;
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
                map.put("unread", false);
                map.put("id","999999");
                data.add(map);
            }
            for (int i = 0; i < notificationArray.length(); i++) {
                map = new HashMap<>();
                String replay_date;
                JSONObject jsonNotification = notificationArray.getJSONObject(i);
                JSONObject jsonAuthor = jsonNotification.getJSONObject("actor");
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", jsonAuthor.getString("nickname"));
                replay_date = FormatDate.changeDate(jsonNotification.getString("timestamp"));
                map.put("replay_date", replay_date);
                JSONObject jsonReplay = jsonNotification.getJSONObject("reply");
                map.put("replay_text", jsonReplay.getString("comment"));
                JSONObject jsonPost = jsonNotification.getJSONObject("post");
                map.put("replay_article", jsonPost.getString("post_title"));
                map.put("unread", jsonNotification.getBoolean("unread"));
                map.put("id",jsonNotification.getString("id"));
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
                    }
                });
        return data;
    }

    boolean flag = false;

    private void setNotification(final int page, final String verb) {
        flag = false;
//        footer.setVisibility(View.VISIBLE);
        listView.postDelayed(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                cycleRun(page, verb);
            }
        }, 500);
    }

    private void cycleRun(int page, String verb) {
        List<RefreshListItem> data = new LinkedList<>();
        RefreshListItem item;
        ArrayList<Map<String, Object>> data1 = getNotification(verb, -1);
        for (Map<String, Object> map : data1
                ) {
            item = new RefreshListItem();
            item.replay_avatar = String.valueOf(map.get("replay_avatar"));
            item.replay_name = String.valueOf(map.get("replay_name"));
            item.replay_date = String.valueOf(map.get("replay_date"));
            item.replay_text = String.valueOf(map.get("replay_text"));
            item.replay_article = String.valueOf(map.get("replay_article"));
            item.isUnread = (boolean) map.get("unread");
            item.id= String.valueOf(map.get("id"));
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
        setUnread();
    }

    public static class RefreshListItem {
        public String replay_avatar;
        public String replay_name;
        public String replay_date;
        public String replay_text;
        public String replay_article;
        public String id;
        public boolean isUnread;
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        Toast.makeText(getContext(), "点击了" + pos + " ", Toast.LENGTH_SHORT).show();
        // TODO Auto-generated method stub
        Log.v("MY_TAG", "onItemClick: state=" + flingState + ", pos=" + pos);

//        switch(flingState) {
//            // 处理左滑事件
//            case FLING_LEFT:
//                Toast.makeText( getContext(), "Fling Left:"+pos, Toast.LENGTH_SHORT).show();
//                flingState = FLING_CLICK;
//                break;
//            // 处理右滑事件
//            case FLING_RIGHT:
//                Toast.makeText( getContext(), "Fling Right:"+pos, Toast.LENGTH_SHORT).show();
//                flingState = FLING_CLICK;
//                break;
//            // 处理点击事件
//            case FLING_CLICK:
//                switch(pos) {
//                    case 0:break;
//                    case 1:break;
//                }
//                Toast.makeText( getContext(), "Click Item:"+pos, Toast.LENGTH_SHORT).show();
//                break;
//        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (foreground!=null){
            closeMenu(foreground);//如果已经有菜单被展开那么关闭它
        }
        Toast.makeText(getContext(), "长按了" + position + " ", Toast.LENGTH_SHORT).show();
        int fetch = 0;
        final ListView mListView=listView.getmListView();
        if (mListView.getLastVisiblePosition() >= mListView.getChildCount())//get到的child只能是屏幕显示的，如第100个child，在屏幕里面当前是第2个，那么应当是第二个child而非100
        {
            fetch = mListView.getChildCount() - 1 - (mListView.getLastVisiblePosition() - position);
        } else {
            fetch = position;
        }
        final View item;
        item=mListView.getChildAt(fetch);
        foreground = item.findViewById(R.id.foreground);
//        foreground.setClickable(false);
        Animation open = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_open_menu);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(open);
        animationSet.setFillAfter(true);
        foreground.startAnimation(animationSet);

        final TextView read,delete,notificationId;
        notificationId=item.findViewById(R.id.id);
        read=item.findViewById(R.id.read_notification);
        delete=item.findViewById(R.id.delete_notification);
        read.setClickable(true);
        delete.setClickable(true);
        final int finalFetch = fetch;
        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu(foreground);
                read.setClickable(false);
                delete.setClickable(false);
                foreground=null;
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeMenu(foreground);
                read.setClickable(false);
                delete.setClickable(false);
                deleteNotification(notificationId.getText().toString());
                View[] views=new View[]{mListView.getChildAt(finalFetch+1),mListView.getChildAt(finalFetch+2),mListView.getChildAt(finalFetch+3)};
                NewThread newThread = new NewThread(item,views);
                newThread.start();
                listView.getmOnLoadListener().onLoad(true);
                foreground=null;
            }
        });
        return true;
    }

    private void closeMenu(View foreground){
        Animation close = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_close_menu);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(close);
        animationSet.setFillAfter(true);
        if (foreground!=null)
            foreground.startAnimation(animationSet);
    }
    private void deleteAnim(View view){
        Animation close = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_delete);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(close);
        if (view!=null)
            view.startAnimation(animationSet);
    }

    private void deleteAnimUp(View view){
        Animation up = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_delete_up);
        Animation down = AnimationUtils.loadAnimation(getContext(), R.anim.list_view_delete_down);
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(up);
        animationSet.addAnimation(down);
        if (view!=null)
            view.startAnimation(animationSet);
    }


    private void setUnread() {
        String url = URL.Notification.getNotification();
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addParams("unread", "true")
                .addParams("verb", "reply")
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

        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .addParams("unread", "true")
                .addParams("verb", "like")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, ".setUnread()" + e.getMessage());
                        unread_notice.setVisibility(View.INVISIBLE);
                        dot_notice.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int countNotice = jsonObject.getInt("count");
                            if (countNotice > 0) {
                                unread_notice.setVisibility(View.VISIBLE);
                                dot_notice.setVisibility(View.VISIBLE);
                                if (countNotice > 100) {
                                    unread_notice.setText("99+");
                                } else
                                    unread_notice.setText(String.valueOf(countNotice));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            unread_notice.setVisibility(View.INVISIBLE);
                            dot_notice.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void deleteNotification(String id) {
        String url = URL.Notification.deleteNotification(id);
        OkHttpUtils
                .delete()
                .url(url)
                .addHeader("Authorization", "Bearer " + TOKEN)
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
                        Toast.makeText(getContext(),"删除成功"+response+":"+id,Toast.LENGTH_SHORT).show();
                        onLoad(true);
                    }
                });
    }

    class NewThread extends Thread{
        View view;
        View[] views;
        public NewThread(View view,View[] views){
            this.view=view;
            this.views=views;
        }
        public void run() {
            deleteAnim(view);
            for (View v:views
                 ) {
                deleteAnimUp(v);
            }
        }
    }
}
