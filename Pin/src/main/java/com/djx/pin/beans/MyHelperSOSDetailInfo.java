package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyHelperSOSDetailInfo {
    public String id;
    public String user_id;
    public String portrait;
    public String nickname;
    public String country_code;
    public String mobile;
    public double price;
    public double pay_price;
    public String location;
    public double latitude;
    public double longitude;
    public int status;
    public int process_status;
    public int publisher_evaluate;
    public int receiver_evaluate;
    public String publisher_appeal;
    public String receiver_appeal;
    public long start_time;
    public long end_time;
    public long cancel_time;
    public long stop_time;
    public List<MEDIA> meida;
    public ReceiverList receiverList;

    public class ReceiverList {
        public int total;
        public int size;
        public List<LIST> list;
    }

    public class LIST {

        public String receiver_id;
        public String nickname;
        public String portrait;
        public int status;
        public int publisher_evaluate;
        public int receiver_evaluate;

        public String publisher_appeal;
        public String receiver_appeal;

        public long start_time;
        public long complete_time;
        public long stop_time;
        public String location;
        public double latitude;
        public double longitude;
    }

    public class MEDIA {
        public String id;
        public String media_type;
    }

}
