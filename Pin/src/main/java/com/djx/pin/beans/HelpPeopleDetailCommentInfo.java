package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class HelpPeopleDetailCommentInfo {

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
    public List<MEDIA> media;
    public List<COMMENT> comment;


    public class MEDIA {
        public String meida_id;
        public int media_type;
    }

    /**
     * 评论实体类
     */
    public class COMMENT {
        public String id;
        public String user_id;
        public String portrait;
        public String nickname;
        public long create_time;
        public int type;
        public String content;
    }

}

