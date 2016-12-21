package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class HelperPeopleDetailInfo {
    public int total;
    public int size;
    public List<LIST> list;

    public class LIST {
        public String id;
        public String user_id;
        public String nickname;
        public String portrait;
        public double price;
        public int is_share;
        public int receiver_limit;
        public String description;
        public double latitude;
        public double longitude;
        public long start_time;
        public long end_time;
        public long stop_time;
        public long create_time;
        public String province;
        public String city;
        public String district;
        public String address;
        public int status;
        public int process_status;
        public int publisher_evaluate;
        public int receiver_evaluate;
        public int publisher_appeal;
        public int receiver_appeal;
        public int comment_num;
        public int share_num;
        public List<MEDIA> media;
        public List<SHARE> share;
        public List<COMMENT> comment;

    }

    public class MEDIA {
        public String meida_id;
        public int media_type;
    }

    /**
     * 分享实体类
     */
    public class SHARE {
        public String user_id;
        public String portrait;
        public String nickname;
        public String create_time;
        public int type;
        public String content;
    }

    /**
     * 评论实体类
     */
    public class COMMENT {
        public String user_id;
        public String portrait;
        public String nickname;
        public String create_time;
        public int type;
        public String content;
    }

}

