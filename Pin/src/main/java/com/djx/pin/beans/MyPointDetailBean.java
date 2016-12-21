package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class MyPointDetailBean {

    private int total;
    private int size;
    private List<MyPointItemBean> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<MyPointItemBean> getList() {
        return list;
    }

    public void setList(List<MyPointItemBean> list) {
        this.list = list;
    }
}
