package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class HelpPeopleDetailShareInfo {

    public String id;
    public String user_id;
    public String description;
    public double price;
    public int receiver_limit;
    public String province;
    public String city;
    public String district;
    public String address;
    public double latitude;
    public double longitude;
    public long start_time;
    public long end_time;
    public long stop_time;
    public long create_time;
    public int status;
    public int process_status;
    public String nickname;
    public String portrait;
    public int comment_num;
    public int share_num;
    public List<PIN> pin;
    public List<MEDIA> media;
    public List<SHARE> share;

    public int getProcess_status() {
        return process_status;
    }

    public class MEDIA {
        public String media_id;
        public int media_type;
    }

    /**
     * pin实体类
     */
    public class PIN{
        public String urser_id;
        public String nickname;
        public String     portrait;
        public long create_time;
    }


    /**
     * 分享实体类
     */
    public class SHARE {
        public String id;
        public String user_id;
        public String portrait;
        public String nickname;
        public long create_time;
        public int type;
        public String content;
    }
}

