package com.djx.pin.beans;

import java.util.List;

/**
 * Created by lifel on 2016/8/12.
 */
public class SourceInfo {
    public int size;
    public List<LIST> list;
    public class LIST{
        public String user_id;
        public long create_time;
        public int role;
        public String nickname;
        public String portrait;
        public String province;
        public String city;
        public String district;
    }




}
