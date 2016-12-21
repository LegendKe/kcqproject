package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyHelperInfo {

    private int avatarId;
    private String massage;
    private String reward;
    private String helperState;
    private String time;

    public MyHelperInfo() {
    }

    public MyHelperInfo(int avatarId, String massage, String reward, String helperState, String time) {
        this.avatarId = avatarId;
        this.massage = massage;
        this.reward = reward;
        this.helperState = helperState;
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

    public String getHelperState() {
        return helperState;
    }

    public void setHelperState(String helperState) {
        this.helperState = helperState;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "MyHelperInfo{" +
                "avatarId=" + avatarId +
                ", massage='" + massage + '\'' +
                ", reward='" + reward + '\'' +
                ", helperState='" + helperState + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
