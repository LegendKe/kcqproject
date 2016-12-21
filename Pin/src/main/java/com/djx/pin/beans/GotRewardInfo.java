package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/29.
 */
public class GotRewardInfo {

    private int avatarId;
    private String massage;
    private String reward;
    private String rewardState;
    private String Time;

    public GotRewardInfo() {

    }

    public GotRewardInfo(int avatarId, String massage, String reward, String rewardState, String time) {
        this.avatarId = avatarId;
        this.massage = massage;
        this.reward = reward;
        this.rewardState = rewardState;
        Time = time;
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

    public String getRewardState() {
        return rewardState;
    }

    public void setRewardState(String rewardState) {
        this.rewardState = rewardState;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    @Override
    public String toString() {
        return "GotRewardInfo{" +
                "avatarId=" + avatarId +
                ", massage='" + massage + '\'' +
                ", reward='" + reward + '\'' +
                ", rewardState='" + rewardState + '\'' +
                ", Time='" + Time + '\'' +
                '}';
    }
}
