package com.djx.pin.myview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by lenovo on 2016/6/19.
 */
public class MyRelativeLayout_CivizationFragment extends RelativeLayout{


    boolean isDispatch=false;
    DispatchTouchEventListener dispatchTouchEventListener;

    public MyRelativeLayout_CivizationFragment(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        dispatchTouchEventListener.dispatchTouchEventListener(ev);
        return isDispatch;
    }

    public interface DispatchTouchEventListener{
        void dispatchTouchEventListener(MotionEvent ev);
    }

    public void setDispatchTouchEventListener(DispatchTouchEventListener dispatchTouchEventListener){
        this.dispatchTouchEventListener=dispatchTouchEventListener;
    }


    public void setIsDispatch(boolean isDispatch){
        this.isDispatch=isDispatch;
    }
    public boolean getIsDispatch(){
        return  isDispatch;
    }
}
