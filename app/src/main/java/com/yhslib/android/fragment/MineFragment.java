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
import android.widget.TextView;
import android.widget.Toast;

import com.yhslib.android.R;
import com.yhslib.android.activity.MyInfoActivity;
import com.yhslib.android.activity.MyPostsActivity;
import com.yhslib.android.config.URL;
import com.yhslib.android.util.MugshotUrl;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;

public class MineFragment extends Fragment {
    private String TAG = "MineFragment";
    private View view;

    private ImageView myViaImage;
    private TextView nicknameTxt;
    private TextView numberOfMemberTxt;
    private TextView signatureTxt;
    private TextView followingCountTxt;
    private TextView followerCountTxt;
    private TextView myFavoriteTxt;
    private TextView myPostsTxt;
    private TextView myCheckinTxt;
    private TextView checkinTxt;

    private final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvcmlnX2lhdCI6MTUyODIyODE1OSwidXNlcm5hbWUiOiJ1c2VyMCIsInVzZXJfaWQiOjEsImV4cCI6MTUyODMxNDU1OSwiZW1haWwiOiJ1c2VyMEBleGFtcGxlLmNvbSJ9.o9akAdWDX7NZOIX0lBh1IwexZnEKfG3RRKwINpVzMYk";
    private final String USERID = "1";
    private String nickname = "";
    private String mugshot_url = "";
    private Boolean ClickFlag = false; // 当数据加载完才能点击

    public static MineFragment newInstance() {
        Bundle args = new Bundle();
        MineFragment fragment = new MineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Log.d(TAG, TAG);
        findView();
        setListener();
        initView();
    }

    private void findView() {
        myViaImage = view.findViewById(R.id.mine_via);
        nicknameTxt = view.findViewById(R.id.mine_nickname);
        //numberOfMemberTxt = view.findViewById(R.id.numberOfMember);
        //signatureTxt = view.findViewById(R.id.mine_signature);
        //followingCountTxt = view.findViewById(R.id.mine_following);
        //followerCountTxt = view.findViewById(R.id.mine_follower);
        myFavoriteTxt = view.findViewById(R.id.mine_my_favorite);
        myPostsTxt = view.findViewById(R.id.mine_my_posts);
        myCheckinTxt = view.findViewById(R.id.mine_my_checkin);
        checkinTxt = view.findViewById(R.id.checkin);
    }

    private void initView() {
        fetchPersonalInformation();
    }

    private void setListener() {
        myPostsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickFlag) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MyPostsActivity.class);
                intent.putExtra("userID", USERID);
                intent.putExtra("token", TOKEN);
                startActivity(intent);
            }
        });

        nicknameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ClickFlag) {
                    return;
                }
                Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                intent.putExtra("userID", USERID);
                intent.putExtra("token", TOKEN);
                intent.putExtra("mugshot_url", mugshot_url);
                intent.putExtra("nickname", nickname);
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
                        Toast.makeText(getActivity().getApplicationContext(), "签到失败", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Toast.makeText(getActivity().getApplicationContext(), "签到成功", Toast.LENGTH_SHORT).show();
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
}
