package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 柯传奇 on 2016/11/29
 */
public class PageBean<T> implements Serializable {
    private List<T> list;

    public List<T> getList() {
        return list;
    }
}