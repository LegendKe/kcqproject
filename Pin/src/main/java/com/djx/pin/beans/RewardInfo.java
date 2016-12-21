package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/6/29.
 */
public class RewardInfo {
    /**
     * {
     * "code": 0,
     * "message": "null",
     * "result": {
     * "total": 2,
     * "size": 1,
     * "list": [
     * {
     * "id": "f895a097a556546a1eca",
     * "user_id": "a79b0bd6b33a915d9063",
     * "process_status": 5,
     * "status": 4,
     * "description": "Confirm流程",
     * "price": 30,
     * "content_type": 1,
     * "book_time": 1468919823000,
     * "sign_time": 1468919887000,
     * "complete_time": 1468919823000,
     * "stop_time": 1468920095000,
     * "publisher_appeal": "",
     * "receiver_appeal": "",
     * "publisher_evaluate": 0,
     * "receiver_evaluate": 0,
     * "publisher_appeal_time": 1470841356000,
     * "receiver_appeal_time": 1470841356000,
     * "nickname": "小超人❤️",
     * "portrait": "13k12j3k12kl31l2"
     * }
     * ]
     * }
     * }
     */
    public int code;
    public String message;
    public Result result;

    public class Result {
        public int total;
        public int size;
        public List<Lists> list;

        public class Lists {
            public String id;
            public String user_id;
            public int process_status;
            public int status;
            public String description;
            public double price;
            public int content_type;
            public long book_time;
            public long sign_time;
            public long complete_time;
            public long start_time;
            public long stop_time;
            public long publisher_evaluate;
            public long receiver_evaluate;
            public long publisher_appeal_time;
            public long receiver_appeal_time;
            public String publisher_appeal;
            public String receiver_appeal;
            public String nickname;
            public String portrait;

            @Override
            public String toString() {
                return "Lists{" +
                        "id='" + id + '\'' +
                        ", user_id='" + user_id + '\'' +
                        ", process_status=" + process_status +
                        ", status=" + status +
                        ", description='" + description + '\'' +
                        ", price=" + price +
                        ", content_type=" + content_type +
                        ", book_time=" + book_time +
                        ", sign_time=" + sign_time +
                        ", complete_time=" + complete_time +
                        ", stop_time=" + stop_time +
                        ", publisher_evaluate=" + publisher_evaluate +
                        ", receiver_evaluate=" + receiver_evaluate +
                        ", publisher_appeal_time=" + publisher_appeal_time +
                        ", receiver_appeal_time=" + receiver_appeal_time +
                        ", publisher_appeal='" + publisher_appeal + '\'' +
                        ", receiver_appeal='" + receiver_appeal + '\'' +
                        ", nickname='" + nickname + '\'' +
                        ", portrait='" + portrait + '\'' +
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
        return "RewardInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
}
