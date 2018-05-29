package com.yhslib.android.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.activity.MainActivity;
import com.yhslib.android.util.BaseFragment;
import com.yhslib.android.util.SlideBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationFragment extends BaseFragment {
    private String TAG = "NotificationFragment";
    private View view;
    private SimpleAdapter adapter;
    private ListView listView;
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
    final String ATME="atme";
    final String COMMENT="comment";
    final String NOTICE="notice";
    private View bar;

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
        listView=view.findViewById(R.id.notification_list_view);
//        mReplay_avatar = (ImageView) view.findViewById(R.id.replay_avatar);
//        mReplay_name = (TextView) view.findViewById(R.id.replay_name);
//        mReplay_date = (TextView) view.findViewById(R.id.replay_date);
//        mReplay_text = (TextView) view.findViewById(R.id.replay_text);
//        mFinal_text_my_comment = (TextView) view.findViewById(R.id.final_text_my_comment);
//        mText_my_comment = (TextView) view.findViewById(R.id.text_my_comment);
//        mSee_details = (TextView) view.findViewById(R.id.see_details);

        mComment = (TextView) view.findViewById(R.id.comment);
        mComment_under_line = (ImageView) view.findViewById(R.id.comment_under_line);
        mAtMe = (TextView) view.findViewById(R.id.atMe);
        mAtMe_under_line = (ImageView) view.findViewById(R.id.atMe_under_line);
        mNotice = (TextView) view.findViewById(R.id.notice);
        mNotice_under_line = (ImageView) view.findViewById(R.id.notice_under_line);
        mNotification_list_view = (ListView) view.findViewById(R.id.notification_list_view);
        bar=view.findViewById(R.id.bar);
    }

    @Override
    protected void init(){

        setComment();
    }

    private void setListViewListener() {
        assert ((MainActivity)getActivity()) != null;
        SlideBar slideBar= new SlideBar(bar,((MainActivity)getActivity()).navigation,listView);
        slideBar.SetSlideBar();
    }

    private void barListener(){
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setComment();
            }
        });
        mAtMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAtMe();
            }
        });
        mNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNotice();
            }
        });
    }

    private void setComment(){
        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text", "text_my_comment"};
        int[] to = {R.id.replay_avatar, R.id.replay_name, R.id.replay_date, R.id.replay_text, R.id.text_my_comment};
        adapter = new SimpleAdapter(getActivity(), getData(COMMENT), R.layout.notification_comment, from, to);
        listView.setAdapter(adapter);
        mComment.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryText));
        mComment_under_line.setVisibility(View.VISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorSecondaryText));
        mAtMe_under_line.setVisibility(View.INVISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorSecondaryText));
        mNotice_under_line.setVisibility(View.INVISIBLE);
    }
    private void setAtMe(){
        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_article", "text_my_comment"};
        int[] to = {R.id.replay_avatar, R.id.replay_name, R.id.replay_date, R.id.replay_article, R.id.text_my_comment};
        adapter = new SimpleAdapter(getActivity(), getData(ATME), R.layout.notification_atme, from, to);
        listView.setAdapter(adapter);
        mComment.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorSecondaryText));
        mComment_under_line.setVisibility(View.INVISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryText));
        mAtMe_under_line.setVisibility(View.VISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorSecondaryText));
        mNotice_under_line.setVisibility(View.INVISIBLE);
    }
    private void setNotice(){
        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text"};
        int[] to = {R.id.replay_avatar, R.id.replay_name, R.id.replay_date, R.id.replay_text};
        adapter = new SimpleAdapter(getActivity(), getData(NOTICE), R.layout.notification_notice, from, to);
        listView.setAdapter(adapter);
        mComment.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorSecondaryText));
        mComment_under_line.setVisibility(View.INVISIBLE);
        mAtMe.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorSecondaryText));
        mAtMe_under_line.setVisibility(View.INVISIBLE);
        mNotice.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorPrimaryText));
        mNotice_under_line.setVisibility(View.VISIBLE);
    }

    public ArrayList<Map<String, Object>> getData(String type) {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map ;
        map = new HashMap<>();
//        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text", "text_my_comment"};
        if (type.equals(COMMENT)){
            for (int i = 0; i < 15; i++) {
                String replay_avatar, replay_name, replay_date, replay_text, text_my_comment;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                replay_text="给你续一秒给你续一秒给你续一秒给你续一秒给你续一秒";
                if (replay_text.length()>=13){
                    replay_text=replay_text.substring(0,13)+"...";
                }
                map.put("replay_text", replay_text);
                text_my_comment="苟利国家生死以，岂因祸福避趋之";
                if (text_my_comment.length()>=12){
                    text_my_comment=text_my_comment.substring(0,12)+"...";
                }
                map.put("text_my_comment", text_my_comment);
                data.add(map);
            }
        }else if (type.equals(ATME)){
//            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_article", "text_my_comment"};
            for (int i = 0; i < 15; i++) {
                String replay_avatar, replay_name, replay_date, replay_article, text_my_comment;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                String tittle = getString(R.string.test_article_tittle);
                if (tittle.length()>=10){
                    tittle=tittle.substring(0,10)+"...";
                }
                map.put("replay_article", tittle);
                text_my_comment="苟利国家生死以，岂因祸福避趋之";
                if (text_my_comment.length()>=12){
                    text_my_comment=text_my_comment.substring(0,12)+"...";
                }
                map.put("text_my_comment", text_my_comment);
                data.add(map);
            }
        }else {
            //            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text"};
            for (int i = 0; i < 15; i++) {
                String replay_avatar, replay_name, replay_date, replay_text;
                map.put("replay_avatar", R.drawable.jerry_zheng);
                map.put("replay_name", "膜法师");
                map.put("replay_date", "5月20日");
                replay_text="苟利国家生死以，岂因祸福避趋之,苟利国家生死以，岂因祸福避趋之";
                if (replay_text.length()>=18){
                    replay_text=replay_text.substring(0,18)+"...";
                }
                map.put("replay_text",  replay_text);
                data.add(map);
            }
        }
        return data;
    }


}
