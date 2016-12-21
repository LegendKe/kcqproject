package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class HelpPeopleDetailEntity implements Serializable{

    public String id;
    public int type;//悬赏类型，1-照片悬赏，2-视频悬赏，3-文字悬赏（其他悬赏）
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
    public List<PIN> pin;
    public List<SHARE> share;


    public class MEDIA {
        public String media_id;
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
/******************************我的求助**************************************************************/
    public List<Partake> partake;
    public class Partake {
        public String user_id;
        public String nickname;
        public String portrait;
    }

    public List<Receiver> receiver;

    public class Receiver {
        public String id;
        public String receiver_id;
        public int status;
        public long publisher_evaluate;
        public long receiver_evaluate;
        public String publisher_appeal;
        public String receiver_appeal;
        public String content;
        public String nickname;
        public long book_time;
        public long stop_time;
        public String portrait;
        public List<MEDIA> media;
    }
}

