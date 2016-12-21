package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/8/1 0001.
 */
public class LifeRewardOnlineCommentDetailInfo {
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
     * "comment": [
     * {
     * "id": "1234567",
     * "user_id": "a79b0bd6b33a915d9063",
     * "content": "",
     * "create_time": 1468993604000,
     * "nickname": "小超人❤️",
     * "portrait": "13k12j3k12kl31l2"
     * },
     * ]
     * }
     * }
     */
    public int code;
    public String message;
    public Result result;

    public class Result {
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
        public int publisher_appeal;
        public int receiver_appeal;
        public String nickname;
        public String portrait;


        public int comment_num;
        public int share_num;
        public int receiver_num;
        public int confirm_num;
        public int view_num;

        public List<Media> media;

        public class Media {
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

        public class Partake {
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

        public List<Comment> comment;

        public class Comment {
            public String id;
            public String user_id;
            public int content;
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
                    ", comment=" + comment +
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
