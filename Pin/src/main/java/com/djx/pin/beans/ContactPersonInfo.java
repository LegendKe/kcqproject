package com.djx.pin.beans;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class ContactPersonInfo {

    public Bitmap bitmap;
    public String name;
    public String phone_number;

    public ContactPersonInfo() {
    }

    public ContactPersonInfo(Bitmap bitmap, String name, String phone_number) {
        this.bitmap = bitmap;
        this.name = name;
        this.phone_number = phone_number;
    }

    public ContactPersonInfo(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    @Override
    public String toString() {
        return "ContactPersonInfo{" +
                "bitmap=" + bitmap +
                ", name='" + name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                '}';
    }
}
