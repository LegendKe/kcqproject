package com.djx.pin.beans;

import java.util.List;

public class WithdrawLogResultBean {

    private int total;
    private int size;
    private List<WithdrawLogItemBean> list;
    public void setTotal(int total) {
         this.total = total;
     }
     public int getTotal() {
         return total;
     }

    public void setSize(int size) {
         this.size = size;
     }
     public int getSize() {
         return size;
     }

    public void setList(List<WithdrawLogItemBean> list) {
         this.list = list;
     }
     public List<WithdrawLogItemBean> getList() {
         return list;
     }
}

