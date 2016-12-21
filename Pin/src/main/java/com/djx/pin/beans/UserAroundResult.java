package com.djx.pin.beans;

import java.util.List;

/**
 * Created by dujoy on 2016/9/2.
 */

public class UserAroundResult {
    private int total;


    private int size;


    private List<UserAround> list ;


    public void setTotal(int total){

        this.total = total;

    }

    public int getTotal(){

        return this.total;

    }

    public void setSize(int size){

        this.size = size;

    }

    public int getSize(){

        return this.size;

    }

    public void setList(List<UserAround> list){

        this.list = list;

    }

    public List<UserAround> getList(){

        return this.list;

    }
}
