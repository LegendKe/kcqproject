package com.djx.pin.beans;

import java.util.List;

/**
 * Created by lifel on 2016/7/27.
 */
public class SOSInfo {
    public int total;
    public int size;
    public List<LIST> list;

    public class LIST {


        public String id;
        public String user_id;
        public String portrait;
        public String nickname;
        public double price;
        public String location;
        public double latitude;
        public double longitude;
        public int status;
        public int process_status;
        public long start_time;
        public long end_time;
        public long stop_time;
        public double distance;
    }
}
