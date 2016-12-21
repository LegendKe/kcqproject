package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/6/29.
 */
public class TaskDetailInfo {

    private int avatarId;
    private String massage;
    private String location;
    private String taskState;
    private String taskTime;

    public TaskDetailInfo() {
    }

    public TaskDetailInfo(int avatarId, String massage, String location, String taskState, String taskTime) {
        this.avatarId = avatarId;
        this.massage = massage;
        this.location = location;
        this.taskState = taskState;
        this.taskTime = taskTime;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTaskState() {
        return taskState;
    }

    public void setTaskState(String taskState) {
        this.taskState = taskState;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    @Override
    public String toString() {
        return "TaskDetailInfo{" +
                "avatarId=" + avatarId +
                ", massage='" + massage + '\'' +
                ", location='" + location + '\'' +
                ", taskState='" + taskState + '\'' +
                ", taskTime='" + taskTime + '\'' +
                '}';
    }
}
