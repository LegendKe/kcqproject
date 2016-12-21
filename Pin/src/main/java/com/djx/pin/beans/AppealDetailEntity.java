package com.djx.pin.beans;

import java.util.List;

/**
 * Created by Administrator on 2016/9/18 0018.
 */
public class AppealDetailEntity {


    private int code;
    private String message;
    private ResultBean result;

    public int getCode() {
        return code;
    }



    public String getMessage() {
        return message;
    }



    public ResultBean getResult() {
        return result;
    }



    public static class ResultBean {
        private String id;
        private String process_id;
        private String publisher_id;
        private String publisher_appeal;
        private long publisher_appeal_time;
        private String receiver_id;
        private String receiver_appeal;
        private long receiver_appeal_time;
        private long appeal_judge_time;
        private int appeal_result;
        private double price;
        private String publisher_nickname;
        private String receiver_nickname;

        private List<AppealMediaBean> publisher_appeal_media;
        private List<AppealMediaBean> receiver_appeal_media;

        public String getId() {
            return id;
        }
        public String getProcess_id() {
            return process_id;
        }

        public String getPublisher_id() {
            return publisher_id;
        }

        public String getPublisher_appeal() {
            return publisher_appeal;
        }

        public long getPublisher_appeal_time() {
            return publisher_appeal_time;
        }
        public String getReceiver_id() {
            return receiver_id;
        }
        public String getReceiver_appeal() {
            return receiver_appeal;
        }
        public long getReceiver_appeal_time() {
            return receiver_appeal_time;
        }
        public long getAppeal_judge_time() {
            return appeal_judge_time;
        }
        public int getAppeal_result() {
            return appeal_result;
        }
        public double getPrice() {
            return price;
        }
        public String getPublisher_nickname() {
            return publisher_nickname;
        }
        public String getReceiver_nickname() {
            return receiver_nickname;
        }

        public List<AppealMediaBean> getPublisher_appeal_media() {
            return publisher_appeal_media;
        }
        public List<AppealMediaBean> getReceiver_appeal_media() {
            return receiver_appeal_media;
        }


        public static class AppealMediaBean {
            private String media_id;
            private int media_type;

            public String getMedia_id() {
                return media_id;
            }

            public int getMedia_type() {
                return media_type;
            }
        }
    }
}
