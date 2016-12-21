package com.djx.pin.beans;

import java.io.Serializable;

/**
 * 帮人列表页   sos列表与帮人列表  叠加
 */
public class HelpPeopleEntity implements Serializable {

    public String id;
    public String user_id;
    public String nickname;
    public String portrait;
    public double price;
    public long start_time;
    public long end_time;
    public long stop_time;
    public double distance;
    public int status;
    public int process_status;
    public double latitude;
    public double longitude;

    /*帮人信息专用*/
    public int is_share;//是否为分享的条目，0-NO，1-YES
    public String share_user_id;//分享用户id，如果is_share=1，则这个字段是分享用户id，
                             // 如果is_share=0，则和user_id相同；抢单和分享求助时，对应于prev_user_id字段
    public String address;
    public String description;
    public int receiver_num;
    public int comment_num;
    public int share_num;
    public int receiver_limit;
    public String province;
    public String city;
    public String district;
    public double pin_distance;

    /*SOS专用*/
    public String location;//发单者最近更新地理位置
    public long cancel_time;//发单者点击我安全了按钮时间



}
