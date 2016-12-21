package com.djx.pin.beans;

import java.util.List;

/**
 * Created by dujoy on 2016/9/5.
 */

public class UserBean extends Entity{

    public String getWish() {
        return wish;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }

    private String wish;

    private String user_id;


    private String nickname;


    private String portrait;


    private int gender;


    private String mobile;


    private String country_code;


    private String province;


    private String city;


    private double latitude;


    private double longitude;


    private int is_show_location;


    private String district;


    private String birthday;


    private int point;


    private int credit;


    private Float balance;


    private Float credit_balance;


    private int is_auth;

    private int rank;

    private String real_name;


    private String id_card;


    private String id_card_pic;


    private String emergency_name;


    private String emergency_country_code;


    private String emergency_mobile;


    private String session_id;


    private String rongyun_token;


    private String zhima_open_id;


    private List<SOSOngoingItem> sos_ongoing;

    public class SOSOngoingItem {
        private String id;
        private int status;

        public String getId() {
            return id;
        }
         public void setId(String id) {
             this.id = id;
         }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }


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

    public void setGender(int gender){

        this.gender = gender;

    }

    public int getGender(){

        return this.gender;

    }

    public void setMobile(String mobile){

        this.mobile = mobile;

    }

    public String getMobile(){

        return this.mobile;

    }

    public void setCountry_code(String country_code){

        this.country_code = country_code;

    }

    public String getCountry_code(){

        return this.country_code;

    }

    public void setProvince(String province){

        this.province = province;

    }

    public String getProvince(){

        return this.province;

    }

    public void setCity(String city){

        this.city = city;

    }

    public String getCity(){

        return this.city;

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

    public void setIs_show_location(int is_show_location){

        this.is_show_location = is_show_location;

    }

    public int getIs_show_location(){

        return this.is_show_location;

    }

    public void setDistrict(String district){

        this.district = district;

    }

    public String getDistrict(){

        return this.district;

    }

    public void setBirthday(String birthday){

        this.birthday = birthday;

    }

    public String getBirthday(){

        return this.birthday;

    }

    public void setPoint(int point){

        this.point = point;

    }

    public int getPoint(){

        return this.point;

    }

    public void setCredit(int credit){

        this.credit = credit;

    }

    public int getCredit(){

        return this.credit;

    }

    public void setBalance(Float balance){

        this.balance = balance;

    }

    public Float getBalance(){

        return this.balance;

    }

    public void setCredit_balance(Float credit_balance){

        this.credit_balance = credit_balance;

    }

    public Float getCredit_balance(){

        return this.credit_balance;

    }

    public void setIs_auth(int is_auth){

        this.is_auth = is_auth;

    }

    public int getIs_auth(){

        return this.is_auth;

    }

    public void setRank(int rank){

        this.rank = rank;

    }

    public int getRank(){

        return this.rank;

    }

    public void setReal_name(String real_name){

        this.real_name = real_name;

    }

    public String getReal_name(){

        return this.real_name;

    }

    public void setId_card(String id_card){

        this.id_card = id_card;

    }

    public String getId_card(){

        return this.id_card;

    }

    public void setId_card_pic(String id_card_pic){

        this.id_card_pic = id_card_pic;

    }

    public String getId_card_pic(){

        return this.id_card_pic;

    }

    public void setEmergency_name(String emergency_name){

        this.emergency_name = emergency_name;

    }

    public String getEmergency_name(){

        return this.emergency_name;

    }

    public void setEmergency_country_code(String emergency_country_code){

        this.emergency_country_code = emergency_country_code;

    }

    public String getEmergency_country_code(){

        return this.emergency_country_code;

    }

    public void setEmergency_mobile(String emergency_mobile){

        this.emergency_mobile = emergency_mobile;

    }

    public String getEmergency_mobile(){

        return this.emergency_mobile;

    }

    public void setSession_id(String session_id){

        this.session_id = session_id;

    }

    public String getSession_id(){

        return this.session_id;

    }

    public void setRongyun_token(String rongyun_token){

        this.rongyun_token = rongyun_token;

    }

    public String getRongyun_token(){

        return this.rongyun_token;

    }

    public void setZhima_open_id(String zhima_open_id){

        this.zhima_open_id = zhima_open_id;

    }

    public String getZhima_open_id(){

        return this.zhima_open_id;

    }

    public void setSos_ongoing(List<SOSOngoingItem> sos_ongoing){

        this.sos_ongoing = sos_ongoing;

    }

    public List<SOSOngoingItem> getSos_ongoing(){

        return this.sos_ongoing;

    }
}
