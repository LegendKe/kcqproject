package com.djx.pin.beans;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/14.
 */
//定义序列化实体类将要使用Bundle传递LifeRewarInfo对象
public class  LifeRewarInfo implements Serializable{
    private String userName;
    private int avatarID;
    private String time;
    private String reward;
    private int imageNumber;
    private String shareNumber;
    private String commentNumber;
    private String helperType;
    private String helperAbout;
    private String location;
    public LifeRewarInfo() {

    }

    public LifeRewarInfo( String userName, int avatarID,String time, String reward, int imageNumber, String location, String helperType, String commentNumber, String shareNumber, String helperAbout) {
        this.avatarID = avatarID;
        this.userName = userName;
        this.time = time;
        this.reward = reward;
        this.imageNumber = imageNumber;
        this.location = location;
        this.helperType = helperType;
        this.commentNumber = commentNumber;
        this.shareNumber = shareNumber;
        this.helperAbout = helperAbout;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public void setAvatarID(int avatarID) {
        this.avatarID = avatarID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public int getImageNumber() {
        return imageNumber;
    }

    public void setImageNumber(int imageNumber) {
        this.imageNumber = imageNumber;
    }

    public String getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(String shareNumber) {
        this.shareNumber = shareNumber;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    @Override
    public String toString() {
        return "LifeRewarInfo{" +
                "userName='" + userName + '\'' +
                ", avatarID=" + avatarID +
                ", time='" + time + '\'' +
                ", reward='" + reward + '\'' +
                ", imageNumber=" + imageNumber +
                ", shareNumber='" + shareNumber + '\'' +
                ", commentNumber='" + commentNumber + '\'' +
                ", helperType='" + helperType + '\'' +
                ", helperAbout='" + helperAbout + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    public String getHelperType() {
        return helperType;
    }

    public void setHelperType(String helperType) {
        this.helperType = helperType;
    }

    public String getHelperAbout() {
        return helperAbout;
    }

    public void setHelperAbout(String helperAbout) {
        this.helperAbout = helperAbout;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
