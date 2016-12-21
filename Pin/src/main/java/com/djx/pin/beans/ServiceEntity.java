package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/10/21 0021.
 */
public class ServiceEntity implements Serializable{

    public int total;
    public List<ServiceBean> list;
    public class ServiceBean implements Serializable{
        public String id;
        public String user_id;
        public String type;
        public String skill_descr;
        public long create_time;
        public double price;
        public List<MEDIA> media;
    }

    public class MEDIA implements Serializable{
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
