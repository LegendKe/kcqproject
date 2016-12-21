package com.djx.pin.receiver;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.djx.pin.business.AppConstants;

import io.rong.imkit.RongIM;

/**
 * 接收未读消息的监听器。
 */
public class MyReceiveUnreadCountChangedListener implements RongIM.OnReceiveUnreadCountChangedListener {
    protected final static String TAG = MyReceiveUnreadCountChangedListener.class.getSimpleName();
//    private Context context;

//    public MyReceiveUnreadCountChangedListener(Context context) {
//        this.context = context;
//    }

    /**
     * @param count           未读消息数。
     */
    @Override
    public void onMessageIncreased(int count) {
        Log.i(TAG, "onMessageIncreased");
//        Intent intent = new Intent(AppConstants.INTENT_ACTION_UNREAD_MSGCOUNT);
//        intent.putExtra("count", count);
//        context.sendBroadcast(intent);
    }
}
