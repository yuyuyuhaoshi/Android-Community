package com.yhslib.android.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.yhslib.android.activity.Welcome;
import com.yhslib.android.config.HashMapField;
import com.yhslib.android.config.URL;
import com.yhslib.android.db.UserDao;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.Call;

public class JWTUtils {
    private static String TAG = "JWTUtils";

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
            hashMap.put(HashMapField.USERID, jsonobject.getString("user_id"));
            hashMap.put(HashMapField.USERNAME, jsonobject.getString("username"));
            hashMap.put(HashMapField.EXP, jsonobject.getString("exp"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public static Boolean inspectToken(HashMap<String, Object> hashMap, Context context) {
        long exp = Long.parseLong(hashMap.get(HashMapField.EXP).toString());
        long currentTime = System.currentTimeMillis() / 1000;
        if (currentTime - exp > 3600 * 24) {
            // 过期一天过期
            return false;
        } else if (currentTime - exp < 1800) {
            handleRefreshToken(hashMap.get(HashMapField.TOKEN).toString(), context);
            return true;
        }
        return true;
    }

    private static void handleRefreshToken(String oldJWTToken, final Context context) {
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
                        String newToken = formatNewToken(response);

                        HashMap<String, Object> hashMap = decoded(newToken);
                        String userid = hashMap.get(HashMapField.USERID).toString();
                        String exp = hashMap.get(HashMapField.EXP).toString();

                        Date day = new Date();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String time = df.format(day);

                        UserDao dao = new UserDao(context);
                        dao.updateUser(userid, newToken, time, exp);
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
}
