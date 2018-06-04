package com.yhslib.android.util;

import android.app.Activity;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhslib.android.R;
import com.yhslib.android.fragment.NotificationFragment;

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
            holder.get(R.id.replay_text, TextView.class).setText(item.replay_text);
            holder.get(R.id.text_my_comment, TextView.class).setText(item.text_my_comment);
        } else if (type.equals(NotificationFragment.ATME)) {
//            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_article", "text_my_comment"};
            holder.get(R.id.replay_avatar,  ImageView.class).setImageResource(Integer.parseInt(item.replay_avatar));
            holder.get(R.id.replay_name, TextView.class).setText(item.replay_name);
            holder.get(R.id.replay_date, TextView.class).setText(item.replay_date);
            holder.get(R.id.replay_article, TextView.class).setText(item.replay_article);
            holder.get(R.id.text_my_comment, TextView.class).setText(item.text_my_comment);
        } else {
            //            String[] from = {"replay_avatar", "replay_name", "replay_date", "replay_text"};
            holder.get(R.id.replay_avatar,  ImageView.class).setImageResource(Integer.parseInt(item.replay_avatar));
            holder.get(R.id.replay_name, TextView.class).setText(item.replay_name);
            holder.get(R.id.replay_date, TextView.class).setText(item.replay_date);
            holder.get(R.id.replay_text, TextView.class).setText(item.replay_text);
        }
    }
}