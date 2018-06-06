package com.yhslib.android.util;

import android.util.Base64;
import android.util.Log;

import com.yhslib.android.config.URL;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import okhttp3.Call;

public class JWTUtils {
    private static String TAG = "JWTUtils";

    public static Boolean storeToken(String JWTEncoded) {
        // TODO 将token存入数据库
        return false;
    }

    public static HashMap<String, Object> decoded(String JWTEncoded) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            String[] split = JWTEncoded.split("\\.");
            hashMap = formatJWTBody(getJson(split[1]));
            //Log.d("JWT_DECODED", hashMap.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }

    private static HashMap<String, Object> formatJWTBody(String json) {
        HashMap<String, Object> hashMap = new HashMap<>();
        try {
            JSONObject jsonobject = new JSONObject(json);
            hashMap.put("id", jsonobject.getString("user_id"));
            hashMap.put("username", jsonobject.getString("username"));
            hashMap.put("exp", jsonobject.getString("exp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    private static void handleRefreshToken(String oldJWTToken) {
        String url = URL.Auth.refreshJWTToken();

        OkHttpUtils
                .get()
                .url(url)
                .addHeader("Authorization", "Bearer " + oldJWTToken)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.d(TAG, e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // TODO 存入数据库
                        Log.d(TAG, formatNewToken(response));
                    }
                });
    }

    private static String formatNewToken(String json) {
        String newToken = "";
        try {
            JSONObject jsonobject = new JSONObject(json);
            newToken = jsonobject.getString("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newToken;
    }

    private static Boolean inspectToken(HashMap<String, Object> hashMap, String token) {
        long exp = Long.parseLong(hashMap.get("exp").toString());
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - exp > 3600 * 24) {
            // 24小时，需要重新登录
            // TODO 登出
            return false;
        } else if (currentTime - exp < 1800) {
            handleRefreshToken(token);
            return true;
        }
        return true;
    }
}
