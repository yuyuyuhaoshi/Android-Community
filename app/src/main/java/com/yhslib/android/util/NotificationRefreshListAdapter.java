package com.yhslib.android.util;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.fragment.NotificationFragment;

import java.io.UnsupportedEncodingException;

/**
 * Created by jerryzheng on 2018/6/1.
 */

public class NotificationRefreshListAdapter extends BaseAdapter<NotificationFragment.RefreshListItem> {
    String type;
    public NotificationRefreshListAdapter(Activity context, String type) {
        super(context);
        this.type = type;
    }

    @Override
    protected int getItemLayoutId(int itemViewType) {
        if (type.equals(NotificationFragment.COMMENT)) {
            return R.layout.notification_comment;
        } else if (type.equals(NotificationFragment.ATME)) {
            return R.layout.notification_atme;
        } else {
            return R.layout.notification_notice;
        }
    }

    @Override
    protected void handleItem(int itemViewType, int position, NotificationFragment.RefreshListItem item, ViewHolder holder, boolean reused) {
        if (type.equals(NotificationFragment.COMMENT)) {
            //        String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text", "text_my_comment"};
            holder.get(R.id.replay_avatar,  ImageView.class).setImageResource(Integer.parseInt(item.replay_avatar));
            holder.get(R.id.replay_name, TextView.class).setText(item.replay_name);
            holder.get(R.id.replay_date, TextView.class).setText(item.replay_date);
            String replay_article=item.replay_article;
            byte[] buf;
            int num = 0;
            boolean isSub=false;
            try {
                int n = 20;
                buf=replay_article.getBytes("GBK");
                num = trimGBK(buf,n);
                if (buf.length>n)
                    isSub=true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            replay_article=replay_article.substring(0,num);
            if (isSub)
                replay_article=replay_article+"...";
            holder.get(R.id.replay_article, TextView.class).setText(replay_article);
            String replay_text=item.replay_text;
            num = 0;
            isSub=false;
            try {
                int n = 36;
                buf=replay_text.getBytes("GBK");
                num = trimGBK(buf,n);
                if (buf.length>n)
                    isSub=true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            replay_text=replay_text.substring(0,num);
            if (isSub)
                replay_text=replay_text+"...";
            holder.get(R.id.replay_text, TextView.class).setText(replay_text);
        } else if (type.equals(NotificationFragment.ATME)) {
//            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_article", "text_my_comment"};
            holder.get(R.id.replay_avatar,  ImageView.class).setImageResource(Integer.parseInt(item.replay_avatar));
            holder.get(R.id.replay_name, TextView.class).setText(item.replay_name);
            holder.get(R.id.replay_date, TextView.class).setText(item.replay_date);
            holder.get(R.id.replay_article, TextView.class).setText(item.replay_article);
            holder.get(R.id.text_my_comment, TextView.class).setText(item.replay_text);
        } else {
            //            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text"};
            holder.get(R.id.replay_avatar,  ImageView.class).setImageResource(Integer.parseInt(item.replay_avatar));
            holder.get(R.id.replay_name, TextView.class).setText(item.replay_name);
            holder.get(R.id.replay_date, TextView.class).setText(item.replay_date);
            String replay_text=item.replay_text;
            byte[] buf;
            int num = 0;
            boolean isSub=false;
            try {
                int n = 74;
                buf=replay_text.getBytes("GBK");
                num = trimGBK(buf,n);
                if (buf.length>n)
                    isSub=true;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            replay_text=replay_text.substring(0,num);
            if (isSub)
                replay_text=replay_text+"...";
            holder.get(R.id.replay_text, TextView.class).setText(replay_text);
        }
    }

    public static int trimGBK(byte[] buf,int n){//输入字节数，返回有几个字符
        int num = 0;
        boolean bChineseFirstHalf = false;
        for(int i=0; i<n&&i<buf.length; i++){
            if(buf[i]<0 && !bChineseFirstHalf){ //是中文的情况,num不用++
                bChineseFirstHalf = true;
            }else{
                num++;
                bChineseFirstHalf = false;
            }
        }
        return num;
    }
}