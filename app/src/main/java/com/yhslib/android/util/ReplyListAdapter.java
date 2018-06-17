package com.yhslib.android.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yhslib.android.R;

import java.util.ArrayList;

public class ReplyListAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<Reply> replyArrayList;
    private LayoutInflater inflater;

    public ReplyListAdapter(Activity activity, ArrayList<Reply> replyList) {
        this.activity = activity;
        this.replyArrayList = replyList;
    }

    @Override
    public int getCount() {
        return replyArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return replyArrayList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return Long.parseLong(replyArrayList.get(position).getId() + "");
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        if (inflater == null) {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_reply, null);
        }
        ImageView mugshot = convertView.findViewById(R.id.reply_mugshot);
        TextView nickname = convertView.findViewById(R.id.reply_nickname);
        TextView date = convertView.findViewById(R.id.reply_date);
        TextView comment = convertView.findViewById(R.id.reply_detail);
        TextView like = convertView.findViewById(R.id.reply_like_count);

        Reply reply = replyArrayList.get(position);
        mugshot.setImageResource(reply.getMugshot());
        nickname.setText(reply.getNickname());
        date.setText(reply.getDate());
        comment.setText(reply.getComment());
        like.setText(reply.getLike());

        return convertView;
    }
}
