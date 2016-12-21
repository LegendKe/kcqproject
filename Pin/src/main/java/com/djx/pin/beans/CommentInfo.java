package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/15.
 */
public class CommentInfo {
    private int avatarID;
    private String userName;
    private String time;
    private String comment;

    public CommentInfo() {
    }

    public CommentInfo(int avatarID, String userName, String time, String comment) {
        this.avatarID = avatarID;
        this.userName = userName;
        this.time = time;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "CommentInfo{" +
                "avatarID=" + avatarID +
                ", userName='" + userName + '\'' +
                ", time='" + time + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    public int getAvatarID() {
        return avatarID;
    }

    public void setAvatarID(int avatarID) {
        this.avatarID = avatarID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
