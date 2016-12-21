package com.djx.pin.beans;
import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class HelperPeopleInfo {
    public int total;
    public int size;
    public List<LIST> list;
    public class LIST {


        public String id;
        public int is_share;
        public String share_user_id;
        public double pin_distance;
        public String user_id;
        public String nickname;
        public String portrait;
        public double price;
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
        public double distance;
        public int receiver_num;
        public int comment_num;
        public int share_num;

        public List<Media> media;
        public class Media {
            public String media_id;
            public int media_type;
        }

    }

}

