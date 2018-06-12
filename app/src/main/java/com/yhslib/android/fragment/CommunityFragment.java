package com.yhslib.android.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.activity.PostActivity;
import com.yhslib.android.config.IntentFields;
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

    private View layoutSearch, layoutPopularTags;
    private SearchView searchView;
    private View.OnClickListener tagsOnClickListener;
    private TextView tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8, tag1Id, tag2Id, tag3Id, tag4Id, tag5Id, tag6Id, tag7Id, tag8Id, textViewPopularArticles;
    private SimpleAdapter adapter;
    private View layoutSwipe;
    private TextView[] tags, tagsId;
    private SimpleListView mContentRlv;
    private String mTag = "1";
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

    @Override
    protected void getDataFromBundle() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_community;
    }


    @Override
    protected void setListener() {
        setSearchListener();
        setListViewListener();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void findView() {
        layoutPopularTags = view.findViewById(R.id.tags);
        layoutSearch = view.findViewById(R.id.search_view);
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
    protected void initView() {
        init();
    }

    /**
     * [初始化Fragment]
     */
    protected void init() {
        searchView.clearFocus();
        layoutPopularTags.setVisibility(View.GONE);
        tags = new TextView[]{tag1, tag2, tag3, tag4, tag5, tag6, tag7, tag8};
        tagsId = new TextView[]{tag1Id, tag2Id, tag3Id, tag4Id, tag5Id, tag6Id, tag7Id, tag8Id};
        getPopularTags(tags);
        mContentRlv.setFooter(footer);
        mAdapter = new RefreshListAdapter(getActivity());
        mContentRlv.setAdapter(mAdapter);
        mContentRlv.setOnLoadListener(this);
        mContentRlv.setOnItemClickListener(this);
        onLoad(true);
    }

    /**
     * [设置搜索监听]
     */
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
                        mTag = id;
                        mAdapter = new RefreshListAdapter(getActivity());
                        mContentRlv.setAdapter(mAdapter);
                        getData(mPage, mTag);
                        isSearchTag = false;
                        break;
                    }
                }
            }
        };
        for (TextView tag : tags) {
            tag.setOnClickListener(tagsOnClickListener);
        }
    }

    /**
     * [搜索文章（未实现）]
     *
     * @param searchString (搜索的关键词)
     */
    private void doSearch(String searchString) {
        String[] from = {"tittle", "name", "date", "tag", "image"};
        int[] to = {R.id.articles_tittle, R.id.articles_name, R.id.articles_date, R.id.articles_tag, R.id.articles_image};
        adapter = new SimpleAdapter(getActivity(), getCommunityPosts(searchString, 1), R.layout.article_list, from, to);
        mContentRlv.setAdapter(adapter);
    }

    /**
     * [将API获取的json数据格式化]
     *
     * @param response (服务器给的json)
     */
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
                map.put("post_id", jsonPost.getString("id"));
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

    /**
     * [设置listView的滚动监听，使得搜索可以被折叠]
     */
    private void setListViewListener() {
        assert ((MainActivity) getActivity()) != null;
        SlideBar slideBar = new SlideBar(layoutSwipe, ((BottomNavigationView) getActivity().findViewById(R.id.navigation)), mContentRlv);
        slideBar.SetSlideBar();
    }

    ArrayList<Map<String, Object>> data = new ArrayList<>();

    /**
     * [从服务器获取文章列表]
     *
     * @param tagId (标签的id)
     * @param page  （获取第几页文章）
     */
    private ArrayList<Map<String, Object>> getCommunityPosts(String tagId, int page) {//使用OkHTTP获取服务器数据
        String url = URL.Community.getPosts();
        Log.d(TAG, url);
        GetBuilder builder = OkHttpUtils
                .get()
                .url(url);
        if (tagId != null) {
            builder.addParams("tags", tagId);
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
                        data = resolvePostsJson(response);
                    }
                });
        return data;
    }

    /**
     * [设置热门标签]
     *
     * @param tags (热门标签控件的数组)
     */
    private void getPopularTags(final TextView[] tags) {//获取热门标签
        String url = URL.Community.getPopularTags();
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

    static boolean flag = false;
    static int requests = 0;

    /**
     * [新建线程，向服务器请求数据]
     *
     * @param tag  (文章标签)
     * @param page （获取第几页文章）
     */
    private void getData(final int page, final String tag) {
        flag = false;
        mContentRlv.postDelayed(new Runnable() {
            @SuppressLint("ResourceType")
            @Override
            public void run() {
                requests++;
                cycleRun(page, tag);
            }
        }, 100);
    }

    /**
     * [将请求结果处理方法抽取出来，以便失败时，请求再次读取]
     *
     * @param tag  (文章标签)
     * @param page （获取第几页文章）
     */
    private void cycleRun(int page, String tag) {
        List<RefreshListItem> data = new LinkedList<>();
        RefreshListItem item;
        ArrayList<Map<String, Object>> data1 = getCommunityPosts(tag, page);
        if (requests > 10) {
            // 防止因为网络问题过多次请求
            return;
        }
        for (Map<String, Object> map : data1
                ) {
            item = new RefreshListItem();
            item.tittle = String.valueOf(map.get("tittle"));
            item.name = String.valueOf(map.get("name"));
            item.date = String.valueOf(map.get("date"));
            item.image = String.valueOf(map.get("image"));
            item.tag = String.valueOf(map.get("tag"));
            item.postId = String.valueOf(map.get("post_id"));
            requests = 0;
            flag = true;
            mIndex++;
            data.add(item);
        }
        mAdapter.setData(data, page == 1);
        mContentRlv.finishLoad(page == lastPage);
        if (!flag) {
            // 如果数据没有获取成功那么重新获取一次
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getData(page, tag);
        }
    }

    @Override
    public void onLoad(boolean isRefresh) {
        if (isRefresh) {
            mPage = 1;
        } else {
            mPage++;
        }
        getData(mPage, null);
    }

    static class RefreshListItem {
        String tittle, name, date, tag, image, postId;
    }


    static class RefreshListAdapter extends BaseAdapter<RefreshListItem> {

        public RefreshListAdapter(Activity context) {
            super(context);
        }

        @Override
        protected int getItemLayoutId(int itemViewType) {
            return R.layout.article_list;
        }

        @Override
        protected void handleItem(int itemViewType, int position, RefreshListItem item, ViewHolder holder, boolean reused) {
            holder.get(R.id.post_id, TextView.class).setText(item.postId);
            holder.get(R.id.articles_tittle, TextView.class).setText(item.tittle);
            holder.get(R.id.articles_name, TextView.class).setText(item.name);
            holder.get(R.id.articles_date, TextView.class).setText(item.date);
            holder.get(R.id.articles_tag, TextView.class).setText(item.tag);
            holder.get(R.id.articles_image, ImageView.class).setImageResource(Integer.parseInt(item.image));
        }
    }

    /**
     * [listView的点击监听，点击后跳转对应的文章列表]
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getContext(), "点击了" + position + " ", Toast.LENGTH_SHORT).show();
        int fetch = 0;
        final ListView mListView = mContentRlv.getmListView();
        if (mListView.getLastVisiblePosition() >= mListView.getChildCount())//get到的child只能是屏幕显示的，如第100个child，在屏幕里面当前是第2个，那么应当是第二个child而非100
        {
            fetch = mListView.getChildCount() - 1 - (mListView.getLastVisiblePosition() - position);
        } else {
            fetch = position;
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
        intent.putExtra(IntentFields.POSTID, id + "");
        startActivity(intent);
    }
}
