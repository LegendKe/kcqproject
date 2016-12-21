package com.djx.pin.activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.djx.pin.business.AppConstants;

public class MusicIntentReceiver extends BroadcastReceiver {
    private static final String TAG = "MusicIntentReceiver";
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            Log.i(TAG, "ACTION_MEDIA_BUTTON!");

            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(
                    Intent.EXTRA_KEY_EVENT);
            int keyAction = keyEvent.getAction();
            if (keyAction == 0) {
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_HEADSETHOOK:
//                    Toast.makeText(context, "hook",Toast.LENGTH_SHORT).show();
                        SOSBtnContext.getInstance().incPressedcnt();
                        if (SOSBtnContext.getInstance().getPressedcnt() == 3) {
                            Toast.makeText(context, "SOS", Toast.LENGTH_SHORT).show();
                            SOSBtnContext.getInstance().clearPressedcnt();
                            SOSBtnContext.getInstance().cancelTimer();
                            Intent outIntent = new Intent(AppConstants.INTENT_ACTION_SOS);
                            outIntent.putExtra("sossource", 2);
                            context.sendBroadcast(outIntent);
                        }
                        if (SOSBtnContext.getInstance().getPressedcnt() == 1) {
                            Log.i(TAG, "pressedcnt == 1");
                            SOSBtnContext.getInstance().setTimer();
                        }
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        Toast.makeText(context, "PLAY_PAUSE", Toast.LENGTH_SHORT).show();
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        Toast.makeText(context, "PLAY", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "KEYCODE_MEDIA_PLAY!");
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        Toast.makeText(context, "PAUSE", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "KEYCODE_MEDIA_PAUSE!");
                        break;

                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        Toast.makeText(context, "STOP", Toast.LENGTH_SHORT).show();
                        break;

                    case KeyEvent.KEYCODE_MEDIA_NEXT:
                        Toast.makeText(context, "NEXT", Toast.LENGTH_SHORT).show();
                        break;

                    case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                        Toast.makeText(context, "PREVIOUS", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    }
}