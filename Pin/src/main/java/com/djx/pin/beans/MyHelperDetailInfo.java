package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/7/21 0021.
 */
public class MyHelperDetailInfo {


    public String nickname;

    public MyHelperDetailInfo(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "MyHelperDetailInfo{" +
                "nickname='" + nickname + '\'' +
                '}';
    }
}
