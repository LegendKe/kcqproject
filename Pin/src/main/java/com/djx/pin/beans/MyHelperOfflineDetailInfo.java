package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyHelperOfflineDetailInfo {

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
    public long create_time;
    public long end_time;
    public long stop_time;
    public long pay_time;
    public int pay_type;
    public int status;
    public String nickname;
    public String portrait;
    public int receiver_num;
    public ReceiverList receiverList;
    public List<Media> media;

    public class ReceiverList {
        public int total;
        public int size;
        public List<LIST> list;
    }


    public class LIST {

        public String id;
        public String receiver_id;
        public long book_time;
        public long sign_time;
        public long complete_time;
        public long confirm_time;
        public long stop_time;
        public long publisher_appeal_time;
        public long receiver_appeal_time;
        public int status;
        public int publisher_evaluate;
        public int receiver_evaluate;
        public String publisher_appeal;
        public String receiver_appeal;
        public String nickname;
        public String portrait;
        public String province;
        public String city;
        public String district;
    }

    public class Media {
        public String media_id;
        public String media_type;

        @Override
        public String toString() {
            return "Media{" +
                    "media_id='" + media_id + '\'' +
                    ", media_type='" + media_type + '\'' +
                    '}';
        }
    }


}
