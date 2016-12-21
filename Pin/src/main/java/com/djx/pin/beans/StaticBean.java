package com.djx.pin.beans;

/**
 * Created by lenovo on 2016/6/18.
 */
public class StaticBean {
    /**
     * 已经在判断代码中转小写,这里不需要加上大写的格式
     */
    public static String[] VIDEO_FORMAT={".3gp",".mp4",".avi"};
    public static String[] PIC_FORMAT={".jpg",".jpeg",".png",".gif",".GIF",".JPG",".JPEG",".PNG"};

    /**上传视屏的最少时长,单位毫秒MS*/
    public static int VIDEO_MIN_DURATION=60000;
    /**上传视屏的最长时长,单位毫秒MS*/
    public static int VIDEO_MAX_DURATION=1800000;

    /**保存用户信息的SharedPreferences名字*/
    public static String USER_INFO="UserInfo";

    /**省份*/
    public static String[] PROVINCE= {"京", "津", "冀", "晋", "蒙", "辽", "吉", "黑", "沪",
            "苏", "浙", "皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂",
            "琼","渝","川", "贵", "云", "藏", "陕", "甘", "青","宁","新" };
    /**车牌号中的城市代码*/
    public static String[] CITYCODE={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};

    /**修改头像时保存的七牛图片key*/

    public static String KEY="key";

    /**从手机联系人中获取名字和手机号*/

    public static String ContactPerson="ContactPerson";

    /**帮人界面中SOS的描述*/
    public static String SOSDescription="我发送了sos紧急求助信号。如您在附近，可来。";
}
