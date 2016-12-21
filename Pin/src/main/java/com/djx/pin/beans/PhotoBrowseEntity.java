package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by dujoy on 2016/9/3.
 */

public class PhotoBrowseEntity implements Serializable {


    private int positon;//记录点击位置
    private List<LifeRewardOnlineDetailInfo.Result.Receiver> receiverList;


    public PhotoBrowseEntity(int positon, List<LifeRewardOnlineDetailInfo.Result.Receiver> receiverList) {
        this.positon = positon;
        this.receiverList = receiverList;
    }

    public int getPositon() {
        return positon;
    }

    public List<LifeRewardOnlineDetailInfo.Result.Receiver> getReceiverList() {
        return receiverList;
    }
}
