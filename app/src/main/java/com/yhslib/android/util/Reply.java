package com.yhslib.android.util;

public class Reply {
    private int id;  // replyID
    private int mugshot;  // 用户头像 资源id
    private String comment;  // 评论
    private String like;  // 点赞数量
    private String date; // 评论日期
    private int userID;  // 用户id
    private String nickname;  // 用户昵称

    public Reply() {

    }

    public Reply(int id, int mugshot, String comment, String like, String date, int userID, String nickname) {
        this.id = id;
        this.mugshot = mugshot;
        this.comment = comment;
        this.like = like;
        this.date = date;
        this.userID = userID;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMugshot() {
        return mugshot;
    }

    public void setMugshot(int mugshot) {
        this.mugshot = mugshot;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
