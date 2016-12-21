package com.djx.pin.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016/7/12 0012.
 */
public class TurnIntoTime {

    /**
     * 将时间戳转换成时间
     * 会显示几小时前,几分钟前
     */
    public static String getCreateTime(Long create_time) {

        String create_Time = "";
        //系统当前时间戳
        long timeCurrentMillis;
        //服务器放回时间戳
        timeCurrentMillis = System.currentTimeMillis();


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss",
                Locale.getDefault());

        String asHoursTime = sdf.format(create_time);
        String ctime = sdf.format(timeCurrentMillis);


        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault());

        String timeCurrent = sdf2.format(timeCurrentMillis);
        String timeCreate = sdf2.format(create_time);


        //凌晨的时间
        String time = "2016-07-09 00:00:00";

        //计算时间差
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {

            Date currentTime = df.parse(timeCurrent);
            Date createTime = df.parse(timeCreate);

            Date lingTime = df.parse(time);
            //和服务器返回时间的时间差
            long diff = currentTime.getTime() - createTime.getTime();
            //和凌晨时间差
            long lingDiff = currentTime.getTime() - lingTime.getTime();
            //现在距离凌晨多少小时
            long lingHours = (lingDiff - ((lingDiff / (1000 * 60 * 60 * 24)) * (1000 * 60 * 60 * 24))) / (1000 * 60 * 60);

            long days = diff / (1000 * 60 * 60 * 24);

            //现在距离服务器多少小时
            long hours = (diff - (days * (1000 * 60 * 60 * 24))) / (1000 * 60 * 60);

            long minutes = (diff - (days * (1000 * 60 * 60 * 24)) - hours * (1000 * 60 * 60)) / (1000 * 60);


            //如果距离凌晨的小时大于距离服务器小时,并且时间小于12小时
            if (days == 0l && lingHours > hours && hours < 12l && hours >= 1l) {
                create_Time = hours + "小时前";
                return create_Time;
            }
            //如果距离凌晨的小时大于距离服务器小时,并且时间大于12小时
            else if (days == 0l && lingHours > hours && hours > 12l) {
                create_Time = "今天" + asHoursTime;
                return create_Time;
            }
            //如果距离凌晨的小时大于距离服务器小时,并且时间大于12小时
            else if (days == 0l && hours <= 0l) {
                create_Time = minutes + "分钟前";
                return create_Time;
            }
            //如果距离凌晨的小时小于距离服务器小时
            else if (days == 0l && lingHours < hours) {
                create_Time = "昨天" + asHoursTime;
                return create_Time;
            }
            //如果距离服务器一天
            else if (days == 1l) {
                create_Time = "昨天" + asHoursTime;
                return create_Time;
            }
            //如果距离服务器两天
            else if (days == 2l) {
                create_Time = "前天" + asHoursTime;
                return create_Time;
            } else {
                create_Time = timeCreate;
                return create_Time;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        return create_Time;
    }

    /**
     * 将时间戳转换成时间
     * 2016-09-01 00:00:00
     */
    public static String getLifeTime(Long create_time) {
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        String timeCreate = sdf2.format(create_time);
        return timeCreate;
    }
}
