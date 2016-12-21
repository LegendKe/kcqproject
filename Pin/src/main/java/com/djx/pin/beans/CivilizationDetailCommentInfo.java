package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/7/11 0011.
 */
public class CivilizationDetailCommentInfo implements Serializable{
    /**
     * {
     * "code": 0,
     * "message": "null",
     * "result": {
     * "id": "d7703abb76d8f4957af0",
     * "user_id": "a79b0bd6b33a915d9063",
     * "description": "看到迪士尼",
     * "location": "上海市浦东新区",
     * "latitude": 130,
     * "longitude": 121,
     * "create_time": 1467388618000,
     * "nickname": "小超人",
     * "portrait": "13k12j3k12kl31l2",
     * "comment_num": 0,
     * "share_num": 0,
     * "media": [
     * {
     * "media_id": "weqeqe21312313213ada",
     * "media_type": 2
     * },
     * {
     * "media_id": "ccbd1e04fcfb36182ead",
     * "media_type": 1
     * }
     * ],
     * "comment": []
     * }
     * }
     */
    public int code;
    public String message;
    public Result result;

    public class Result {
        public String id;
        public String user_id;
        public String description;
        public String location;
        public double latitude;
        public double longitude;
        public long create_time;
        public String nickname;
        public String portrait;
        public int comment_num;
        public int share_num;
        public List<Media> media;

        public class Media {
            public String media_id;
            public String media_type;

            @Override
            public String toString() {
                return "Media{" +
                        "media_id='" + media_id + '\'' +
                        ", media_type='" + media_type + '\'' +
                        '}';
            }
        }

        public List<Comment> comment;

        public class Comment {

            public String user_id;
            public String portrait;
            public String nickname;
            public long create_time;
            public String content;

            @Override
            public String toString() {
                return "Comment{" +
                        "user_id='" + user_id + '\'' +
                        ", portrait='" + portrait + '\'' +
                        ", nickname='" + nickname + '\'' +
                        ", create_time=" + create_time +
                        ", content='" + content + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Result{" +
                    "id='" + id + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", description='" + description + '\'' +
                    ", location='" + location + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", create_time=" + create_time +
                    ", nickname='" + nickname + '\'' +
                    ", portrait='" + portrait + '\'' +
                    ", comment_num=" + comment_num +
                    ", share_num=" + share_num +
                    ", media=" + media +
                    ", comment=" + comment +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "CivilizationDetailCommentInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
