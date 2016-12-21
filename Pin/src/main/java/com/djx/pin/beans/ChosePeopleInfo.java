package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class ChosePeopleInfo {
    private int avatarId;
    private String userName;
    private String location;

    public ChosePeopleInfo() {

    }

    public ChosePeopleInfo(int avatarId, String userName, String location) {
        this.avatarId = avatarId;
        this.userName = userName;
        this.location = location;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ChosePeopleInfo{" +
                "avatarId=" + avatarId +
                ", userName='" + userName + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
