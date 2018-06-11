package com.yhslib.android.util;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

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

    /**
     * 解析token值 取出body中的值
     *
     * @param JWTEncoded token值
     * @return hashMap token中的关键信息
     */
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

    /**
     * 检测token是否过期
     * 过期 return false 需要登出
     * 未过期 return true 继续用旧的token
     *
     * @param hashMap 需要包含exp 过期时间
     * @param context 上下文 数据库操作需要context
     * @return Boolean
     */
    public static Boolean inspectToken(HashMap<String, Object> hashMap, Context context) {
        long exp = Long.parseLong(hashMap.get(HashMapField.EXP).toString());
        long currentTime = System.currentTimeMillis() / 1000;
        Log.d(TAG, "currentTime" + currentTime + "exp" + exp);
        if ((exp - currentTime < 1800) && (exp - currentTime >= 0)) {
            // 在即将过期的半小时内
            // 刷新token
            handleRefreshToken(hashMap.get(HashMapField.TOKEN).toString(), context);
            return true;
        } else if (currentTime < exp) {
            // 暂不刷新token
            return true;
        }
        return false;
    }

    /**
     * 请求新的token
     *
     * @param oldJWTToken 旧的token
     * @param context     上下文
     */
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

    /**
     * 格式化返回的token
     *
     * @param json json键值对
     * @return newToken 取出新的token
     */
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
