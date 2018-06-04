package com.yhslib.android.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FormatDate {
    public static String changeDate(String date) {
        //格式化时间

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf2 = new SimpleDateFormat("MM月dd日");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日");
        String[] strings = date.split("T");
        String[] strings1 = strings[1].split("\\.");
        date = strings[0] + " " + strings1[0];
        Date date1 = null, now;
        String result = "";
        long minutes, hour, days;
        try {
            now = new Date();
            date1 = sdf.parse(date);
            //计算差值，分钟数
            minutes = (now.getTime() - date1.getTime()) / (1000 * 60);
            //计算差值，小时数
            hour = (now.getTime() - date1.getTime()) / (1000 * 60 * 60);
            //计算差值，天数
            days = (now.getTime() - date1.getTime()) / (1000 * 60 * 60 * 24);
        } catch (ParseException e) {
            e.printStackTrace();
            return "时间格式化出错";
        }
        if (minutes <= 1) {
            result = "刚刚";
        } else if (minutes <= 60) {
            result = minutes + "分钟前";
        } else if (hour <= 24) {
            result = hour + "小时前";
        } else if (days < 7) {
            result = days + "天前";
        } else if (date1.getYear() == now.getYear()) {
            result = String.valueOf(sdf2.format(date1));
        } else {
            result = String.valueOf(sdf3.format(date1));
        }
        return result;
    }
}
