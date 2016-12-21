package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyHelperListInfo {
    public int total;
    public int size;
    public List<LIST> list;

    public class LIST {
        public String id;
        public String user_id;
        public String description;
        public Double price;
        public int receiver_limit;
        public int status;
        public int content_type;
        public long start_time;
        public long end_time;
        public long stop_time;
        public long create_time;
        public String nickname;
        public String portrait;

    }

}
