package com.yhslib.android.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.util.BaseAdapter;
import com.yhslib.android.R;
import com.yhslib.android.util.SimpleListView;
import com.yhslib.android.activity.MainActivity;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseFragment;
import com.yhslib.android.util.FormatDate;
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

public class CommunityFragment extends BaseFragment implements SimpleListView.OnLoadListener, AdapterView.OnItemClickListener {
    private String TAG = "CommunityFragment";

    private View view;
    private View layoutSearch, layoutPopularTags;
    //    private ListView mContentRlv;
    private SearchView searchView;
    private View.OnClickListener tagsOnClickListener;
    private TextView tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8, tag1Id, tag2Id, tag3Id, tag4Id, tag5Id, tag6Id, tag7Id, tag8Id, textViewPopularArticles;
    private SimpleAdapter adapter;
    private View layoutSwipe;
    private TextView[] tags, tagsId;
    private SimpleListView mContentRlv;
    private String mTag = "1";
    //    private View mEmptyView;
    private RefreshListAdapter mAdapter;
    private int mPage = 1;
    private int mIndex = 1;
    private View footer;
    EditText searchText;
    private boolean isSearchTag = false;
    int lastPage = 0;

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
        return view;
    }


    @Override
    protected void setListener() {
        setSearchListener();
        setListViewListener();
    }

    @Override
    protected void findView() {
        layoutPopularTags = view.findViewById(R.id.tags);
        layoutSearch = view.findViewById(R.id.search_view);
//        listViewArticle = view.findViewById(R.id.content_rlv);
        searchView = layoutSearch.findViewById(R.id.search);
        layoutSwipe = view.findViewById(R.id.swipe);
        textViewPopularArticles = view.findViewById(R.id.popular_articles);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        searchText = searchView.findViewById(id);
        tag1 = layoutPopularTags.findViewById(R.id.tag1);
        tag2 = layoutPopularTags.findViewById(R.id.tag2);
        tag3 = layoutPopularTags.findViewById(R.id.tag3);
        tag4 = layoutPopularTags.findViewById(R.id.tag4);
        tag5 = layoutPopularTags.findViewById(R.id.tag5);
        tag6 = layoutPopularTags.findViewById(R.id.tag6);
        tag7 = layoutPopularTags.findViewById(R.id.tag7);
        tag8 = layoutPopularTags.findViewById(R.id.tag8);

        tag1Id = layoutPopularTags.findViewById(R.id.tag1_id);
        tag2Id = layoutPopularTags.findViewById(R.id.tag2_id);
        tag3Id = layoutPopularTags.findViewById(R.id.tag3_id);
        tag4Id = layoutPopularTags.findViewById(R.id.tag4_id);
        tag5Id = layoutPopularTags.findViewById(R.id.tag5_id);
        tag6Id = layoutPopularTags.findViewById(R.id.tag6_id);
        tag7Id = layoutPopularTags.findViewById(R.id.tag7_id);
        tag8Id = layoutPopularTags.findViewById(R.id.tag8_id);

        footer = view.findViewById(R.id.footer_layout);
        mContentRlv = (SimpleListView) view.findViewById(R.id.content_rlv);
    }

    @Override
    protected void init() {
        searchView.clearFocus();
        layoutPopularTags.setVisibility(View.GONE);
        tags = new TextView[]{tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8};
        tagsId = new TextView[]{tag1Id, tag2Id, tag3Id, tag4Id, tag5Id, tag6Id, tag7Id, tag8Id};
        setPopularTags(tags);
        refreshDate(null);
        mContentRlv.setFooter(footer);
//        mEmptyView = findViewById(R.id.empty_rl);
//        mContentRlv.setEmptyView(mEmptyView);
        mAdapter = new RefreshListAdapter(getActivity());
        mContentRlv.setAdapter(mAdapter);
        mContentRlv.setOnLoadListener(this);
        mContentRlv.setOnItemClickListener(this);
        onLoad(true);
//        footer.setVisibility(View.GONE);
    }

    private void refreshDate(String tagId) {
        System.out.println("---------------------------------------" + tagId);
        String[] from = {"tittle", "name", "date", "tag", "image"};
        int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
        adapter = new SimpleAdapter(getActivity(), getCommunityPosts(tagId, 1), R.layout.article_list, from, to);
        mContentRlv.setAdapter(adapter);
    }

    private void setSearchListener() {
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mContentRlv.setVisibility(View.VISIBLE);
                layoutPopularTags.setVisibility(View.GONE);
                textViewPopularArticles.setText(R.string.popular_articles);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContentRlv.setVisibility(View.GONE);
                layoutPopularTags.setVisibility(View.VISIBLE);
                textViewPopularArticles.setText(R.string.hot_tags);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mContentRlv.setVisibility(View.VISIBLE);
                layoutPopularTags.setVisibility(View.GONE);
                textViewPopularArticles.setText(R.string.popular_articles);
                if (isSearchTag)
                    return false;
                else {
                    doSearch(query);
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        tagsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tag = (TextView) v;
                searchText.setText(tag.getText());
                String id;
                for (int i = 0; i < tags.length; i++) {
                    if (v.getId() == tags[i].getId()) {
                        id = tagsId[i].getText().toString();
                        isSearchTag = true;
                        mTag = id;
                        onLoad(true);
//                        isSearchTag=false;
//                        refreshDate(id);
                        System.out.println("ddddssdsdsdsdsdsdsdsds" + id);
                        break;
                    }
                }
            }
        };
        for (TextView tag : tags) {
            tag.setOnClickListener(tagsOnClickListener);
        }
    }

    private void doSearch(String searchString) {
        String[] from = {"tittle", "name", "date", "tag", "image"};
        int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
//        ArrayList<Map<String, Object>> data=new ArrayList<>();
//        System.out.println("788787788："+lastPage);
//        for (int i = 0; i < lastPage; i++) {
//            ArrayList<Map<String, Object>> dataFrom=getCommunityPosts(null,i);
//            System.out.println("啦啦啦");
//            for (Map<String, Object> item: dataFrom
//                 ) {
//                String tittle =item.get("tittle").toString();
//                System.out.println(tittle+tittle.contains(searchString));
//                if (tittle.contains(searchString)){
//                    data.add(item);
//                }
//            }
//        }
        adapter = new SimpleAdapter(getActivity(), getCommunityPosts(searchString, 1), R.layout.article_list, from, to);
        mContentRlv.setAdapter(adapter);
    }

    public ArrayList<Map<String, Object>> resolvePostsJson(String response) {
        String[] from = {"tittle", "name", "date", "tag", "image"};
        ArrayList<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> map;
        try {
            map = new HashMap<>();
            map.put("tittle", "如何使用单反拍出好看的延时摄影？");
            map.put("name", "膜法师");
            map.put("date", FormatDate.changeDate("2018-05-28T14:33:00.000+08:00"));
            map.put("tag", "技术·摄影");
            map.put("image", R.drawable.articlc_image);
            data.add(map);
            map = new HashMap<>();
            map.put("tittle", "JAVA对比C#各有什么优势？");
            map.put("name", "PHP是最好的语言");
            map.put("date", FormatDate.changeDate("2016-09-03T00:00:00.000+08:00"));
            map.put("tag", "JAVA·程序");
            map.put("image", R.drawable.article_image2);
            data.add(map);
            map = new HashMap<>();
            map.put("tittle", "谈一谈，中国进5年的发展速度到底有多迅猛。");
            map.put("name", "中国牛逼");
            map.put("date", FormatDate.changeDate("2018-05-27T00:00:00.000+08:00"));
            map.put("tag", "社会·历史");
            map.put("image", R.drawable.article_image3);
            data.add(map);

            JSONObject jsonObject = new JSONObject(response);
            JSONTokener jsonTokener = new JSONTokener(jsonObject.getString("data"));
            JSONArray postsArray = (JSONArray) jsonTokener.nextValue();
            lastPage = jsonObject.getInt("last_page");
            for (int i = 0; i < postsArray.length(); i++) {
                map = new HashMap<>();
                JSONObject jsonPost = postsArray.getJSONObject(i);
                map.put("tittle", jsonPost.getString("title"));
                JSONObject jsonAuthor = jsonPost.getJSONObject("author");
                map.put("name", jsonAuthor.getString("nickname"));
                map.put("date", FormatDate.changeDate(jsonPost.getString("created")));
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
        return data;
    }

    private void setListViewListener() {
        assert ((MainActivity) getActivity()) != null;
        SlideBar slideBar = new SlideBar(layoutSwipe, ((BottomNavigationView) getActivity().findViewById(R.id.navigation)), mContentRlv);
        slideBar.SetSlideBar();
    }

    ArrayList<Map<String, Object>> data = new ArrayList<>();

    private ArrayList<Map<String, Object>> getCommunityPosts(String tagId, int page) {

        String url = URL.Community.getPosts(page);
        Log.d(TAG, url);
        GetBuilder builder = OkHttpUtils
                .get()
                .url(url);
        if (tagId != null) {
            builder.addParams("tags", tagId);
        }
        builder.build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, "getCommunityPosts()" + e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        data = resolvePostsJson(response);
//                        String[] from = {"tittle", "name", "date", "tag", "image"};
//                        int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
//                        adapter = new SimpleAdapter(getActivity(), data, R.layout.article_list, from, to);
//                        mContentRlv.setAdapter(adapter);
//                        Log.d(TAG, "onResponse: " + data);
                    }
                });
        return data;
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
                                tagsId[i].setText(tagsJson.getJSONObject(i).getString("id"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    boolean flag = false;

    private void getData(final int page, final String tag) {
        flag = false;
//        footer.setVisibility(View.VISIBLE);

        mContentRlv.postDelayed(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                List<RefreshListItem> data = new LinkedList<>();
                RefreshListItem item;
                ArrayList<Map<String, Object>> data1 = getCommunityPosts(tag, page);
                for (Map<String, Object> map : data1
                        ) {
                    item = new RefreshListItem();
                    item.tittle = String.valueOf(map.get("tittle"));
                    item.name = String.valueOf(map.get("name"));
                    item.date = String.valueOf(map.get("date"));
                    item.image = String.valueOf(map.get("image"));
                    item.tag = String.valueOf(map.get("tag"));
                    flag = true;
                    mIndex++;
                    data.add(item);
                }
                mAdapter.setData(data, page == 1 ? true : false);
                mContentRlv.finishLoad(page == lastPage ? true : false);
                if (!flag) {//如果数据没有获取成功那么重新获取一次
                    getData(page, tag);
                }
            }
        }, 500);

    }

    @Override
    public void onLoad(boolean isRefresh) {
        if (isRefresh) {
            mPage = 1;
        } else {
            mPage++;
        }
        if (isSearchTag)
            getData(mPage, mTag);
        else
            getData(mPage, null);
    }

    private static class RefreshListItem {
        String tittle, name, date, tag, image;
    }


    private static class RefreshListAdapter extends BaseAdapter<RefreshListItem> {

        public RefreshListAdapter(Activity context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int itemViewType) {
            return R.layout.article_list;
        }

        @Override
        protected void handleItem(int itemViewType, int position, RefreshListItem item, ViewHolder holder, boolean reused) {
//            int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
            holder.get(R.id.articles_tittle, TextView.class).setText(item.tittle);
            holder.get(R.id.articles_name, TextView.class).setText(item.name);
            holder.get(R.id.articles_date, TextView.class).setText(item.date);
            holder.get(R.id.articles_tag, TextView.class).setText(item.tag);
            holder.get(R.id.articles_image, ImageView.class).setImageResource(Integer.parseInt(item.image));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), "点击了" + position + " ", Toast.LENGTH_SHORT).show();
    }
}
