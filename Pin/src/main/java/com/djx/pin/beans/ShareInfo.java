package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/15.
 */
public class ShareInfo {
    private int avatarID;
    private String userName;
    private String time;
    private String where;

    public ShareInfo() {
    }

    public ShareInfo(int avatarID, String userName, String time, String where) {
        this.avatarID = avatarID;
        this.userName = userName;
        this.time = time;
        this.where = where;
    }

    @Override
    public String toString() {
        return "ShareInfo{" +
                "avatarID=" + avatarID +
                ", userName='" + userName + '\'' +
                ", time='" + time + '\'' +
                ", where='" + where + '\'' +
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

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }
}
