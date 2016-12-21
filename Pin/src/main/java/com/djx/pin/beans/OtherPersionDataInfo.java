package com.djx.pin.beans;

/**
 * Created by Administrator on 2016/7/20 0020.
 */
public class OtherPersionDataInfo {

    /**
     * {"message":"null",
     * "result":{
     * "birthday":"2016-10-05",
     * "nickname":"好多好多鱼?",
     * "province":"重庆市",
     * "gender":1,
     * "user_id":"2cfa560650854a94df9e",
     * "district":"枞阳县",
     * "portrait":"935412b45fd5364f1b7e",
     * "city":"重庆市"},
     * "code":0}
     */

    public String message;
    public Result result;
    public int code;

    public class Result {

        public String birthday;
        public String nickname;
        public String province;
        public String user_id;
        public String portrait;
        public String district;
        public String city;
        public int gender;//is_auth   credit   latitude   longitude
        public int is_auth;//1-已实名
        public int credit;
        public String zhima_open_id;
        public double latitude;
        public double longitude;
        public String wish;

        @Override
        public String toString() {
            return "Result{" +
                    "birthday='" + birthday + '\'' +
                    ", nickname='" + nickname + '\'' +
                    ", province='" + province + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", portrait='" + portrait + '\'' +
                    ", district='" + district + '\'' +
                    ", city='" + city + '\'' +
                    ", gender=" + gender +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "OtherPersionDataInfo{" +
                "message='" + message + '\'' +
                ", result=" + result +
                ", code=" + code +
                '}';
    }
}
