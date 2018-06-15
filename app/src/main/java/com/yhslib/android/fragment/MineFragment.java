package com.yhslib.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.activity.MyInfoActivity;
import com.yhslib.android.activity.MyPostsActivity;
import com.yhslib.android.activity.MyReplyActivity;
import com.yhslib.android.config.IntentFields;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.BaseFragment;
import com.yhslib.android.util.CoinType;
import com.yhslib.android.util.MugshotUrl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class MineFragment extends BaseFragment {
    private String TAG = "MineFragment";

    private ImageView myViaImage;
    private TextView numberOfMemberTxt;
    private TextView signatureTxt;
    private TextView followingCountTxt;
    private TextView followerCountTxt;
    private TextView checkinTxt;
    private TextView nicknameTxt;
    private TextView copperCoinTxt;

    private String TOKEN;
    private String USERID;
    private String nickname = "";
    private String mugshot_url = "";
    private Boolean ClickFlag = false; // 当数据加载完才能点击

    private LinearLayout myPostsLayout, myCheckinLayout, nicknameLayout, myReplyLayout;

    public static MineFragment newInstance(String userid, String token) {
        Bundle args = new Bundle();
        args.putString(IntentFields.USERID, userid);
        args.putString(IntentFields.TOKEN, token);
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void getDataFromBundle() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            USERID = bundle.getString(IntentFields.USERID);
            TOKEN = bundle.getString(IntentFields.TOKEN);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void findView() {
        myViaImage = view.findViewById(R.id.mine_via);
        nicknameTxt = view.findViewById(R.id.mine_nickname);
        nicknameLayout = view.findViewById(R.id.mine_info);
        //numberOfMemberTxt = view.findViewById(R.id.numberOfMember);
        //signatureTxt = view.findViewById(R.id.mine_signature);
        //followingCountTxt = view.findViewById(R.id.mine_following);
        //followerCountTxt = view.findViewById(R.id.mine_follower);
        myPostsLayout = view.findViewById(R.id.mine_my_posts);
        myCheckinLayout = view.findViewById(R.id.mine_my_checkin);
        checkinTxt = view.findViewById(R.id.checkin);
        copperCoinTxt = view.findViewById(R.id.copper_coin);
        myReplyLayout = view.findViewById(R.id.mine_my_reply);
    }

    @Override
    protected void initView() {
        fetchPersonalInformation();
        fetchBalance();
    }

    @Override
    protected void setListener() {
        myReplyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickFlag) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MyReplyActivity.class);
                intent.putExtra(IntentFields.USERID, USERID);
                intent.putExtra(IntentFields.TOKEN, TOKEN);
                startActivity(intent);
            }
        });

        myPostsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickFlag) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MyPostsActivity.class);
                intent.putExtra(IntentFields.USERID, USERID);
                intent.putExtra(IntentFields.TOKEN, TOKEN);
                startActivity(intent);
            }
        });

        nicknameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickFlag) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                intent.putExtra(IntentFields.USERID, USERID);
                intent.putExtra(IntentFields.TOKEN, TOKEN);
                intent.putExtra(IntentFields.MUGSHOTURL, mugshot_url);
                intent.putExtra(IntentFields.NICKNAME, nickname);
                startActivity(intent);
            }
        });

        checkinTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckin();
            }
        });
    }

    @Override
    protected void initData() {

    }

    private void handleCheckin() {
        String url = URL.User.checkin(USERID);
        OkHttpUtils
                .post()
                .url(url)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(getActivity(), "签到失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Toast.makeText(getActivity(), "签到成功", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response);
                    }
                });
    }

    private void fetchPersonalInformation() {
        String url = URL.User.detail(USERID);
        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + TOKEN)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // Log.d(TAG, formatUserInfoJSON(response).toString());
                        HashMap<String, Object> hm = formatUserInfoJSON(response).get(0);
                        nickname = hm.get("nickname").toString();
                        nicknameTxt.setText(nickname);
                        mugshot_url = hm.get("mugshot_url").toString();
                        loadMugshot(mugshot_url);
                        ClickFlag = true;
                    }
                });
    }

    private void fetchBalance() {
        String url = URL.User.getBalance(USERID);
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
                        HashMap<String, Object> hashMap = formatBalance(response);
                        copperCoinTxt.setText(hashMap.get("copper").toString());
                    }
                });
    }

    private void loadMugshot(String url) {
        url = URL.host + url;
        MugshotUrl.load(url, myViaImage);
    }

    private ArrayList<HashMap<String, Object>> formatUserInfoJSON(String response) {
        ArrayList<HashMap<String, Object>> resultList = new ArrayList<>();
        try {
            JSONObject jsonobject = new JSONObject(response);
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("id", jsonobject.getLong("id"));
            hashMap.put("username", jsonobject.getString("username"));
            hashMap.put("nickname", jsonobject.getString("nickname"));
            hashMap.put("mugshot_url", jsonobject.getString("mugshot_url"));
            resultList.add(hashMap);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultList;
    }

    private HashMap<String, Object> formatBalance(String response) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            String copper = CoinType.formatCoinNumber(jsonObject.getString("amount__sum"));
            hashMap.put("copper", copper);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }
}
