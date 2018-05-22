package com.yhslib.android.fragment;

import android.nfc.Tag;
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

import com.yhslib.android.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

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
        initPersonalInfomation();

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

    private void initPersonalInfomation() {
        String url = "http://api.dj-china.org//rest-auth/login/";
        OkHttpUtils
                .post()
                .url(url)
                .addParams("username", "user0")
                .addParams("password", "yuhaoshi")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.d(TAG, response);
                    }

                });
    }
}
