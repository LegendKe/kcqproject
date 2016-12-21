package com.djx.pin.activity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by eagle on 2016/8/1.
 */
public class SOSBtnContext {
    private static SOSBtnContext instance;
    private static final String TAG = SOSBtnContext.class.getSimpleName();
    int pressedcnt = 0;
    Timer timer;

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                Log.i(TAG, "timer out");
                SOSBtnContext.getInstance().clearPressedcnt();
                timer.cancel();
                timer = null;
            }
            super.handleMessage(msg);
        };
    };

    private SOSBtnContext() {}

    /**
     * 单一实例
     */
    public static SOSBtnContext getInstance() {
        if (instance == null) {
            instance = new SOSBtnContext();
        }
        return instance;
    }

    int getPressedcnt()
    {
        return pressedcnt;
    }

    void incPressedcnt()
    {
        pressedcnt++;
    }

    void clearPressedcnt()
    {
        pressedcnt = 0;
    }

    void cancelTimer()
    {
        timer.cancel();
        timer = null;
    }

    void setTimer()
    {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 2000, 2000); // 1s后执行task,经过1s再次执行
    }
}
