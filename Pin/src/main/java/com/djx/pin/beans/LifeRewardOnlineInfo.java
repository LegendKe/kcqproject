package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/6/14.
 */
//定义序列化实体类将要使用Bundle传递LifeRewarInfo对象
public class LifeRewardOnlineInfo implements Serializable {

    public int total;
    public int size;
    public List<LIST> list;
    public class LIST {
        public String id;
        public String user_id;
        public int type;
        public String description;
        public double price;
        public int receiver_limit;
        public int status;
        public long start_time;
        public long end_time;
        public long stop_time;
        public long create_time;
        public int comment_num;
        public int share_num;
        public String nickname;
        public String portrait;
        public String view_num;
        public int process_status;
        public List<MEDIA> media;

    }

    public class MEDIA{
        String media;
        int media_type;

        public String getMedia() {
            return media;
        }

        public int getMedia_type() {
            return media_type;
        }
    }
}
