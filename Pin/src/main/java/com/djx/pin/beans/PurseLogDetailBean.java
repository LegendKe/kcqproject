package com.djx.pin.beans;

import java.util.List;

/**
 * Created by dujoy on 2016/8/5.
 */

public class PurseLogDetailBean {
    private int total;
    private int size;
    private List<PurseLogItemBean> list;

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

    public List<PurseLogItemBean> getList() {
        return list;
    }

    public void setList(List<PurseLogItemBean> list) {
        this.list = list;
    }
}
