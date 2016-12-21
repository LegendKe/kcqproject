package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/10/13 0013.
 */
public class LifeRewardOnlineEntity implements Serializable {

    public int total;
    public int size;
    public List<OnlineBean> list;
    public class OnlineBean {
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
        String media_id;
        int media_type;
        public String getMedia_id() {
            return media_id;
        }

        public int getMedia_type() {
            return media_type;
        }
    }
}