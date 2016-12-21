package com.djx.pin.beans;

/**
 * Created by dujoy on 2016/9/2.
 */

public class UserAround {
    private String user_id;


    private String nickname;


    private String portrait;


    private double latitude;


    private double longitude;


    private double distance;


    public void setUser_id(String user_id){

        this.user_id = user_id;

    }

    public String getUser_id(){

        return this.user_id;

    }

    public void setNickname(String nickname){

        this.nickname = nickname;

    }

    public String getNickname(){

        return this.nickname;

    }

    public void setPortrait(String portrait){

        this.portrait = portrait;

    }

    public String getPortrait(){

        return this.portrait;

    }

    public void setLatitude(double latitude){

        this.latitude = latitude;

    }

    public double getLatitude(){

        return this.latitude;

    }

    public void setLongitude(double longitude){

        this.longitude = longitude;

    }

    public double getLongitude(){

        return this.longitude;

    }

    public void setDistance(double distance){

        this.distance = distance;

    }

    public double getDistance(){

        return this.distance;

    }
}
