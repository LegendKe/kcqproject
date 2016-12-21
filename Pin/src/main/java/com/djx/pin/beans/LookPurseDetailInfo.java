package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/29.
 */
public class LookPurseDetailInfo {

    private int avatarId;
    private String massage;
    private String reward;
    private String time;

    public LookPurseDetailInfo() {

    }

    public LookPurseDetailInfo(int avatarId, String massage, String reward, String time) {
        this.avatarId = avatarId;
        this.massage = massage;
        this.reward = reward;
        this.time = time;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public String getMassage() {
        return massage;
    }

    public void setMassage(String massage) {
        this.massage = massage;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "LookPurseDetailInfo{" +
                "avatarId=" + avatarId +
                ", massage='" + massage + '\'' +
                ", reward='" + reward + '\'' +
                ", Time='" + time + '\'' +
                '}';
    }
}
