package com.djx.pin.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/8/14 0014.
 */
public class LostChildInfo {
    /**
     * {
     * "code": 0,
     * "message": "null",
     * "result": {
     * "total": 1,
     * "size": 1,
     * "list": [
     * {
     * "id": "123",
     * "user_id": "a79b0bd6b33a915d9063",
     * "mobile": "13112345667",
     * "description": "无",
     * "name": "超人",
     * "gender": 1,
     * "age": 10,
     * "height": 1.4,
     * "weight": 50,
     * "hair": "黑",
     * "birthday": 946684800000,
     * "lost_time": 1451606400000,
     * "lost_location": "上海",
     * "create_time": 1470989584000,
     * "nickname": "小超人",
     * "portrait": "13k12j3k12kl31l2",
     * "media": []
     * }
     * ]
     * }
     * }
     */
    public int code;
    public String message;
    public Result result;

    public class Result implements Serializable {
        public int total;
        private int size;
        public List<ChildInfo> list;

        public class ChildInfo implements Serializable{
            public String id;
            public String user_id;
            public String mobile;
            public String description;
            public String name;
            public int gender;
            public int age;
            public double height;
            public double weight;
            public String hair;
            public long birthday;
            public long lost_time;
            public String lost_location;
            public long create_time;
            public String nickname;
            public String portrait;
            public List<Media> media;

            public class Media {
               public String media_id;
               public String media_type;
            }
        }
    }

}
