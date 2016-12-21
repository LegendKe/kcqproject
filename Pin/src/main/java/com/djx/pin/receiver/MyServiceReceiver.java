package com.djx.pin.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.util.Log;

import com.djx.pin.utils.ToastUtil;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class MyServiceReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        try {
            /**android.content.BroadcastReceiver.getResultCode()方法 */
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    /**发送短信成功 */
                    Log.e("lmn", "----发送短信成功---------------------------");
                    ToastUtil.shortshow(context,"短信发送成功");
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                    /***发送短信失败 */
                case SmsManager.RESULT_ERROR_RADIO_OFF:
                case SmsManager.RESULT_ERROR_NULL_PDU:

                default:
                    Log.e("lmn", "----发送短信失败---------------------------");
                    ToastUtil.shortshow(context,"发送短信失败");
                    break;
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}
