package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/10/26 0026.
 */
public class SkillSearchEntity implements Serializable {

    public int total,size;
    public List<SkillBean> list;
    public class SkillBean implements Serializable{
        public String id;
        public String user_id;
        public String nickname;
        public String portrait;
        public double latitude;
        public double longitude;
        public String type;
        public String skill_descr;
        public double price;
        public long create_time;
        public List<MEDIA> media;
        public double distance;
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
