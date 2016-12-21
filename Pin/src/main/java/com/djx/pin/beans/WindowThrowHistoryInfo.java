package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/23.
 */
public class WindowThrowHistoryInfo {


    public String total;
    public String size;
    public List<ListEle> list;

    public class ListEle {
        public String location;
        public Long event_time;
        public int checked;
    }
}
