package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/1 0001.
 */
public class LifeRewardOnlineDetailInfo implements Serializable{
    /**
     * {
     * "code": 0,
     * "message": "null",
     * "result": {
     * "id": "a85a8cb447c4bd943714",
     * "user_id": "a79b0bd6b33a915d9063",
     * "type": 2,
     * "description": "又一个网络悬赏",
     * "price": 20,
     * "receiver_limit": 3,
     * "start_time": 1468987791000,
     * "end_time": 1469073600000,
     * "stop_time": 1469006725000,
     * "status": 2,
     * "create_time": 1468992895000,
     * "process_status": 1,
     * "publisher_evaluate": 0,
     * "receiver_evaluate": 0,
     * "publisher_appeal": "",
     * "receiver_appeal": "",
     * "nickname": "小超人",
     * "portrait": "13k12j3k12kl31l2",
     * "comment_num": 0,
     * "share_num": 1,
     * "receiver_num": 4,
     * "confirm_num": 3,
     * "view_num": 1,
     * "media": [
     * {
     * "media_id": "ccbd1e04fcfb36182ead",
     * "media_type": 1
     * }],
     * "partake": [
     * {
     * "user_id": "d9a99c72be49afb16c22",
     * "nickname": "中超人",
     * "portrait": ""
     * }
     * ],
     * "share": [
     * {
     * "id": "12345",
     * "user_id": "a79b0bd6b33a915d9063",
     * "type": 1,
     * "create_time": 1468992935000,
     * "nickname": "小超人❤️",
     * "portrait": "13k12j3k12kl31l2"
     * }
     * ],
     * }
     * }
     */
    public int code;
    public String message;
    public Result result;

    public class Result implements Serializable{
        public String id;
        public String user_id;
        public int type;
        public String description;
        public double price;
        public int receiver_limit;
        public long start_time;
        public long end_time;
        public long stop_time;
        public int status;
        public long create_time;
        public int process_status;
        public int publisher_evaluate;
        public int receiver_evaluate;
        public String publisher_appeal;
        public String receiver_appeal;
        public String nickname;
        public String portrait;


        public int comment_num;
        public int share_num;
        public int receiver_num;
        public int confirm_num;
        public int view_num;

        public List<Media> media;

        public class Media implements Serializable{
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

        public List<Partake> partake;

        public class Partake implements Serializable{
            public String user_id;
            public String nickname;
            public String portrait;

            @Override
            public String toString() {
                return "Partake{" +
                        "user_id='" + user_id + '\'' +
                        ", nickname='" + nickname + '\'' +
                        ", portrait='" + portrait + '\'' +
                        '}';
            }
        }

        public List<Share> share;

        public class Share {
            public String id;
            public String user_id;
            public int type;
            public long create_time;
            public String nickname;
            public String portrait;

            @Override
            public String toString() {
                return "Share{" +
                        "id='" + id + '\'' +
                        ", user_id='" + user_id + '\'' +
                        ", type=" + type +
                        ", create_time=" + create_time +
                        ", nickname='" + nickname + '\'' +
                        ", portrait='" + portrait + '\'' +
                        '}';
            }

        }

        public List<Comment> comment;

        public class Comment {
            public String id;
            public String user_id;
            public String content;
            public long create_time;
            public String nickname;
            public String portrait;

            @Override
            public String toString() {
                return "Comment{" +
                        "id='" + id + '\'' +
                        ", user_id='" + user_id + '\'' +
                        ", content='" + content + '\'' +
                        ", create_time=" + create_time +
                        ", nickname='" + nickname + '\'' +
                        ", portrait='" + portrait + '\'' +
                        '}';
            }
        }

        /**
         *
         * {"code":0,"message":"null","result":{"id":"a895143e91ddd6a50638","user_id":"fc88fbb5b52bc54b607c",
         * "type":1,"description":"照片悬赏照片悬赏照片悬赏照片悬赏照片悬赏照片悬赏照片悬赏照片悬赏照片悬赏照片悬赏",
         * "price":3,
         * "receiver_limit":1,
         * "start_time":1470844800000,
         * "end_time":1473523200000,
         * "stop_time":1470896600000,
         * "status":1,
         * "create_time":1470896600000,
         * "process_status":0,
         * "publisher_evaluate":0,
         * "receiver_evaluate":0,
         * "publisher_appeal":"",
         * "receiver_appeal":"",
         * "nickname":"123",
         * "portrait":"f142e5b6478d8bf2272a",
         * "comment_num":14,
         * "share_num":11,"receiver_num":1,
         * "confirm_num":0,
         * "view_num":97,
         * "media":[{"media_id":"6379e97133b01421b714","media_type":1}],
         * "partake":[{"user_id":"2cfa560650854a94df9e","nickname":"好多好多鱼?","portrait":"935412b45fd5364f1b7e"}],"receiver":[{"id":"4ee138906101e3ac0fc5","receiver_id":"2cfa560650854a94df9e","status":0,"content":"","book_time":1470901932000,"stop_time":1470901932000,"nickname":"好多好多鱼?","portrait":"935412b45fd5364f1b7e",
         * "media":[{"media_id":"f37eafbc012824175098","media_type":1}]}]}}
         * */
        public List<Receiver> receiver;

        public class Receiver implements Serializable{
            public String id;
            public String receiver_id;
            public int status;
            public long publisher_evaluate;
            public long receiver_evaluate;
            public String publisher_appeal;
            public String receiver_appeal;
            public String content;
            public String nickname;
            public long book_time;
            public long stop_time;
            public String portrait;
            public List<Media> media;

            public class Media implements Serializable{
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
                return "Receiver{" +
                        "id='" + id + '\'' +
                        ", receiver_id='" + receiver_id + '\'' +
                        ", status=" + status +
                        ", publisher_evaluate=" + publisher_evaluate +
                        ", receiver_evaluate=" + receiver_evaluate +
                        ", publisher_appeal=" + publisher_appeal +
                        ", receiver_appeal=" + receiver_appeal +
                        ", content='" + content + '\'' +
                        ", nickname='" + nickname + '\'' +
                        ", book_time='" + book_time + '\'' +
                        ", stop_time='" + stop_time + '\'' +
                        ", portrait='" + portrait + '\'' +
                        ", media=" + media +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "Result{" +
                    "id='" + id + '\'' +
                    ", user_id='" + user_id + '\'' +
                    ", type=" + type +
                    ", description='" + description + '\'' +
                    ", price=" + price +
                    ", receiver_limit=" + receiver_limit +
                    ", start_time=" + start_time +
                    ", end_time=" + end_time +
                    ", stop_time=" + stop_time +
                    ", status=" + status +
                    ", create_time=" + create_time +
                    ", process_status=" + process_status +
                    ", publisher_evaluate=" + publisher_evaluate +
                    ", receiver_evaluate=" + receiver_evaluate +
                    ", publisher_appeal=" + publisher_appeal +
                    ", receiver_appeal=" + receiver_appeal +
                    ", nickname='" + nickname + '\'' +
                    ", portrait='" + portrait + '\'' +
                    ", comment_num=" + comment_num +
                    ", share_num=" + share_num +
                    ", receiver_num=" + receiver_num +
                    ", confirm_num=" + confirm_num +
                    ", view_num=" + view_num +
                    ", media=" + media +
                    ", partake=" + partake +
                    ", share=" + share +
                    ", comment=" + comment +
                    ", receiver=" + receiver +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LifeRewardOnlineDetailInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
