package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/6/14.
 */
//定义序列化实体类将要使用Bundle传递LifeRewarInfo对象
public class LifeRewardInfo implements Serializable {
    /**
     * "code": 0,
     * "message": "null",
     * "result": {
     * "total": 1,
     * "size": 1,
     * "list": [
     * {
     * "id": "62e456db7bc0febafa01",
     * "user_id": "a79b0bd6b33a915d9063",
     * "nickname": "小超人",
     * "portrait": "13k12j3k12kl31l2",
     * "location": "上海市徐汇区",
     * "description": "看到一个祖传贴膜",
     * "latitude": 130,
     * "longitude": 120,
     * "create_time": 1467386858000,
     * "distance": 7922.9507014742785,
     * "comment_num": 0,
     * "share_num": 0,
     * "media": [
     * {
     * "media_id": "ccbd1e04fcfb36182ead",
     * "media_type": 1
     * }
     * ]
     * }
     * ]
     * }
     */

    public int code;
    public String message;
    public Result result;

    public class Result  {
        public int total;
        public int size;
        public List<Lists> list;

        public class Lists   {
            public String id;
            public String user_id;
            public String nickname;
            public String portrait;
            public String location;
            public String description;
            public double latitude;
            public double longitude;
            public long create_time;
            public double distance;
            public int comment_num;
            public int share_num;
            public List<Media> media;

            public class Media  {
                public String media_id;
                public int media_type;

                @Override
                public String toString() {
                    return "Media{" +
                            "media_id='" + media_id + '\'' +
                            ", media_type=" + media_type +
                            '}';
                }
            }

            @Override
            public String toString() {
                return "Lists{" +
                        "id='" + id + '\'' +
                        ", user_id='" + user_id + '\'' +
                        ", nickname='" + nickname + '\'' +
                        ", portrait='" + portrait + '\'' +
                        ", location='" + location + '\'' +
                        ", description='" + description + '\'' +
                        ", latitude=" + latitude +
                        ", longitude=" + longitude +
                        ", create_time=" + create_time +
                        ", distance=" + distance +
                        ", comment_num=" + comment_num +
                        ", share_num=" + share_num +
                        ", media=" + media +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Result{" +
                    "total=" + total +
                    ", size=" + size +
                    ", list=" + list +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LifeRewardInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
