package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyPointItemBean {

    private String user_id;
    private String nickname;
    private String portrait;
    private int point;
    private int type;
    private String target_id;
    private int target_type;
    private long create_time;

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTarget_id() {
        return target_id;
    }

    public void setTarget_id(String target_id) {
        this.target_id = target_id;
    }

    public int getTarget_type() {
        return target_type;
    }

    public void setTarget_type(int target_type) {
        this.target_type = target_type;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

//    public String getThings() {
//        return things;
//    }
//
//    public void setThings(String things) {
//        this.things = things;
//    }
//
//    public String getGotWithdraw() {
//        return gotWithdraw;
//    }
//
//    public void setGotWithdraw(String gotWithdraw) {
//        this.gotWithdraw = gotWithdraw;
//    }
//
//    public String getTime() {
//        return time;
//    }
//
//    public void setTime(String time) {
//        this.time = time;
//    }
//
//    public String getReason() {
//        return reason;
//    }
//
//    public void setReason(String reason) {
//        this.reason = reason;
//    }
//
//    @Override
//    public String toString() {
//        return "MyPointDetailBean{" +
//                "point=" + point +
//                ", things='" + things + '\'' +
//                ", gotWithdraw='" + gotWithdraw + '\'' +
//                ", time='" + time + '\'' +
//                ", reason='" + reason + '\'' +
//                '}';
//    }
}
