package com.yhslib.android.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.yhslib.android.config.URL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

public class MugshotUrl {
    public static final String TAG = "MugshotUrl";
    public static void load(String url, final ImageView imageView) {
        final String TAG = "";
        OkHttpUtils
                .get()//
                .url(url)//
                .build()//
                .execute(new BitmapCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        String mTAG = TAG;
                        Log.d(mTAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(Bitmap response, int id) {
                        ImageView mImageView = imageView;
                        mImageView.setImageBitmap(response);
                    }
                });
    }
}