package com.yhslib.android.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.activity.LoginActivity;
import com.yhslib.android.activity.MyInfoActivity;
import com.yhslib.android.activity.MyPostsActivity;
import com.yhslib.android.config.URL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MineFragment extends Fragment {
    private String TAG = "MineFragment";
    private View view;

    private ImageView myVia;
    private TextView nicknameTxt;
    private TextView numberOfMemberTxt;
    private TextView signatureTxt;
    private TextView followingCountTxt;
    private TextView followerCountTxt;
    private TextView myFavoriteTxt;
    private TextView myPostsTxt;
    private TextView myCheckinTxt;

    private final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1Mjc0MTQzMDEsIm9yaWdfaWF0IjoxNTI3MzI3OTAxLCJlbWFpbCI6InVzZXIwQGV4YW1wbGUuY29tIiwidXNlcl9pZCI6MSwidXNlcm5hbWUiOiJ1c2VyMCJ9.z5iFhYm8Nc14x8YzFjIxJJbzkjwdLac5UGJ_zk27ZsM";
    private final String USERID = "1";

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
        initView();
        fetchPersonalInformation();

    }

    private void findView() {
        myVia = view.findViewById(R.id.mine_via);
        nicknameTxt = view.findViewById(R.id.mine_nickname);
        //numberOfMemberTxt = view.findViewById(R.id.numberOfMember);
        //signatureTxt = view.findViewById(R.id.mine_signature);
        //followingCountTxt = view.findViewById(R.id.mine_following);
        //followerCountTxt = view.findViewById(R.id.mine_follower);
        myFavoriteTxt = view.findViewById(R.id.mine_my_favorite);
        myPostsTxt = view.findViewById(R.id.mine_my_posts);
        myCheckinTxt = view.findViewById(R.id.mine_my_checkin);
    }

    private void initView() {
        myPostsTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyPostsActivity.class);
                intent.putExtra("userID", USERID);
                intent.putExtra("token", TOKEN);
                startActivity(intent);
            }
        });

        nicknameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyInfoActivity.class);
                intent.putExtra("userID", USERID);
                intent.putExtra("token", TOKEN);
                startActivity(intent);
            }
        });
    }

    private void fetchPersonalInformation() {
        String url = URL.User.detail(USERID);
        Log.d(TAG, url);

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
                        nicknameTxt.setText(hm.get("nickname").toString());
                        loadMugshotUrl(hm.get("mugshot_url").toString());
                    }

                });
    }

    private void loadMugshotUrl(String url) {
        url = URL.host + url;
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        myVia.setImageBitmap(response);
                    }
                });
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
