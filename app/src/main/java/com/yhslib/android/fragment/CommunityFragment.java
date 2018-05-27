package com.yhslib.android.fragment;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.CutBitmap;
import com.yhslib.android.util.SlideBar;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Response;

public class CommunityFragment extends Fragment {
    private String TAG = "CommunityFragment";

    private View view;
    private View layoutSearch, layoutPopularTags;
    private ListView listViewArticle;
    private SearchView searchView;
    private View.OnClickListener tagsOnClickListener;
    private TextView tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8, textViewPopularArticles;
    private SimpleAdapter adapter;
    private View layoutSwipe;
    private TextView[] tags;
    private boolean searchIsOpen = false;

    public static CommunityFragment newInstance() {
        Bundle args = new Bundle();
        CommunityFragment fragment = new CommunityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_community, container, false);
        init();
        searchListener();
        setListViewListener();
        setPopularTags(tags);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, TAG);
    }

    private void init() {
        layoutPopularTags = view.findViewById(R.id.tags);
        layoutSearch = view.findViewById(R.id.search_view);
        listViewArticle = view.findViewById(R.id.list_article);
        searchView = layoutSearch.findViewById(R.id.search);
        layoutSwipe = view.findViewById(R.id.swipe);
        textViewPopularArticles = view.findViewById(R.id.popular_articles);
        searchView.clearFocus();
        layoutPopularTags.setVisibility(View.GONE);
        tag1 = layoutPopularTags.findViewById(R.id.tag1);
        tag2 = layoutPopularTags.findViewById(R.id.tag2);
        tag3 = layoutPopularTags.findViewById(R.id.tag3);
        tag4 = layoutPopularTags.findViewById(R.id.tag4);
        tag5 = layoutPopularTags.findViewById(R.id.tag5);
        tag6 = layoutPopularTags.findViewById(R.id.tag6);
        tag7 = layoutPopularTags.findViewById(R.id.tag7);
        tag8 = layoutPopularTags.findViewById(R.id.tag8);
        tags = new TextView[]{tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8};
        String[] from = {"tittle", "name", "date", "tag", "image"};
        int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
        adapter = new SimpleAdapter(getActivity(), getCommunityPosts(), R.layout.article_list, from, to);
        listViewArticle.setAdapter(adapter);
    }

    private void searchListener() {
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listViewArticle.setVisibility(View.VISIBLE);
                layoutPopularTags.setVisibility(View.GONE);
                textViewPopularArticles.setText(R.string.popular_articles);
                searchIsOpen = false;
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewArticle.setVisibility(View.GONE);
                layoutPopularTags.setVisibility(View.VISIBLE);
                textViewPopularArticles.setText(R.string.hot_tags);
                searchIsOpen = true;
            }
        });

        tagsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tag = (TextView) v;
                int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                EditText textView = searchView.findViewById(id);
                textView.setText(tag.getText());
            }
        };
        for (TextView tag : tags) {
            tag.setOnClickListener(tagsOnClickListener);
        }
    }

    public ArrayList<Map<String, Object>> resolvePostsJson(String response) {
        String[] from = {"tittle", "name", "date", "tag", "image"};
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>(); //这个new HashMap<>()不可以省略，否则会报空指针
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONTokener jsonTokener = new JSONTokener(jsonObject.getString("data"));
            JSONArray postsArray = (JSONArray) jsonTokener.nextValue();
            for (int i = 0; i < postsArray.length(); i++) {
                map = new HashMap<>();
                JSONObject jsonPost = postsArray.getJSONObject(i);
                map.put("tittle", jsonPost.getString("title"));
                JSONObject jsonAuthor = jsonPost.getJSONObject("author");
                map.put("name", jsonAuthor.getString("nickname"));
                map.put("date", changeDate(jsonPost.getString("created")));
                JSONArray tagsArry = jsonPost.getJSONArray("tags");
                String tag = "";
                for (int j = 0; j < tagsArry.length(); j++) {
                    if (j == 2) {
                        tag = tag + "...";
                        break;
                    }
                    if (j != 0) {
                        tag = tag + "·" + tagsArry.getJSONObject(j).getString("name");
                    } else {
                        tag = tag + tagsArry.getJSONObject(j).getString("name");
                    }
                }
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.articlc_image);
                map.put("tag", tag);
                map.put("image", R.drawable.articlc_image);
                data.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        for (int i = 0; i < 15; i++) {
//            map.put("tittle", "如何使用单反拍出好看的延时摄影？");
//            map.put("name", "膜法师");
//            map.put("date", "5月20日");
//            map.put("tag", "技术·摄影");
//            map.put("image", R.drawable.articlc_image);
//            data.add(map);
//        }
        return data;
    }

    private void setListViewListener() {
        SlideBar slideBar = new SlideBar(layoutSwipe, listViewArticle);
        slideBar.SetSlideBar();
    }

    ArrayList<Map<String, Object>> data = new ArrayList<>();

    private ArrayList<Map<String, Object>> getCommunityPosts() {
        String url = URL.Community.getPosts(1);
        Log.d(TAG, url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getCommunityPosts()" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        data = resolvePostsJson(response);
                        String[] from = {"tittle", "name", "date", "tag", "image"};
                        int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
                        adapter = new SimpleAdapter(getActivity(), data, R.layout.article_list, from, to);
                        listViewArticle.setAdapter(adapter);
                        Log.d(TAG, "onResponse: " + data);
                    }
                });
        return data;
    }


    public String changeDate(String date) {
        //格式化时间

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日");
        String[] strings = date.split("T");
        String[] strings1 = strings[1].split("\\.");
        date = strings[0] + " " + strings1[0];
        System.out.println(date + "ddededededededede");
        Date date1 = null, now;
        String result = "";
        long minutes, hour, days;
        try {
            now = new Date();
            date1 = sdf.parse(date);
            //计算差值，分钟数
            minutes = (now.getTime() - date1.getTime()) / (1000 * 60);
            //计算差值，小时数
            hour = (now.getTime() - date1.getTime()) / (1000 * 60 * 60);
            //计算差值，天数
            days = (now.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
            return "时间格式化出错";
        }
        if (minutes <= 1) {
            result = "刚刚";
        } else if (minutes <= 60) {
            result = minutes + "分钟前";
        } else if (hour <= 24) {
            result = hour + "小时前";
        } else if (days < 7) {
            result = days + "天前";
        } else if (date1.getYear() == now.getYear()) {
            result = String.valueOf(sdf2.format(date1));
        } else {
            result = String.valueOf(sdf3.format(date1));
        }
        return result;
    }

    /**
     * 日期格式转换yyyy-MM-dd'T'HH:mm:ss.SSSXXX  (yyyy-MM-dd'T'HH:mm:ss.SSSZ) TO  yyyy-MM-dd HH:mm:ss2016-09-03T00:00:00.000+08:00
     *
     * @throws ParseException
     */
    public static String dealDateFormat(String oldDateStr) throws ParseException {
        //此格式只有  jdk 1.7才支持  yyyy-MM-dd'T'HH:mm:ss.SSSXXX
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");  //yyyy-MM-dd'T'HH:mm:ss.SSSZ
        Date date = df.parse(oldDateStr);
        SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        Date date1 = df1.parse(date.toString());
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//  Date date3 =  df2.parse(date1.toString());
        return df2.format(date1);
    }

    private void setPopularTags(final TextView[] tags) {
        String url = URL.Community.getPopularTags();
        Log.d(TAG, url);
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, ".getPopularTags()" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONArray tagsJson = new JSONArray(response);
                            for (int i = 0; i < tagsJson.length(); i++) {
                                if (i == tags.length)
                                    return;
                                tags[i].setText(tagsJson.getJSONObject(i).getString("name"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
