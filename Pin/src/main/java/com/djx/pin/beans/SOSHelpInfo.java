package com.djx.pin.beans;

import java.io.Serializable;

/**
 * 该类用来存储HelperPeopleFragment中listview相关信息
 * Created by lifel on 2016/7/28.
 */
public class SOSHelpInfo extends Entity implements Serializable {
    public boolean isSOS;
    public String id;
    public String share_user_id;//求助信息专用,SOS信息用户到此参数
    public String user_id;
    public String nickname;
    public String portrait;
    public String description;
    public String address;
    public double distance;
    public double price;
    public long start_time;
    public long end_time;
    public long stop_time;
    public double latitude;
    public double longitude;




}
