package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyIntegralInfo {

    private int avatarId;
    private String things;
    private String gotWithdraw;
    private String time;
    private String reason;
    public MyIntegralInfo() {

    }
    public MyIntegralInfo(int avatarId, String things, String gotWithdraw, String time, String reason) {
        this.avatarId = avatarId;
        this.things = things;
        this.gotWithdraw = gotWithdraw;
        this.time = time;
        this.reason = reason;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public String getThings() {
        return things;
    }

    public void setThings(String things) {
        this.things = things;
    }

    public String getGotWithdraw() {
        return gotWithdraw;
    }

    public void setGotWithdraw(String gotWithdraw) {
        this.gotWithdraw = gotWithdraw;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "MyIntegralInfo{" +
                "avatarId=" + avatarId +
                ", things='" + things + '\'' +
                ", gotWithdraw='" + gotWithdraw + '\'' +
                ", time='" + time + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
