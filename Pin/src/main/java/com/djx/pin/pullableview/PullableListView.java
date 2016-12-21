package com.djx.pin.pullableview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class PullableListView extends ListView implements Pullable {

    private boolean isPullDown = true, isPullUp = true;

    public PullableListView(Context context) {
        super(context);
    }

    public PullableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PullableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean canPullDown() {
        if (!isPullDown) {
            //如果isPullDown为false,则不允许下拉刷新.用户可设.方便用户使用,反之则正常使用
            return false;
        }
        if (getCount() == 0) {
            // 没有item的时候也可以下拉刷新
            return true;
        } else if (getFirstVisiblePosition() == 0
                && getChildAt(0).getTop() >= 0) {
            // 滑到ListView的顶部了
            return true;
        } else
            return false;
    }

    @Override
    public boolean canPullUp() {
        if (!isPullUp) {
            //如果isPullUp为false,则不允许上拉加载.用户可设.方便用户使用,反之则正常使用,
            return false;
        }

        if (getCount() == 0) {
            // 没有item的时候也可以上拉加载
            return true;
        } else if (getLastVisiblePosition() == (getCount() - 1)) {
            // 滑到底部了
            if (getChildAt(getLastVisiblePosition() - getFirstVisiblePosition()) != null
                    && getChildAt(
                    getLastVisiblePosition()
                            - getFirstVisiblePosition()).getBottom() <= getMeasuredHeight())
                return true;
        }
        return false;
    }


    /**
     * 设置是否可以下拉刷新
     *
     * @param isPullDown
     */
    public void setCanPullDown(boolean isPullDown) {
        this.isPullDown = isPullDown;

    }

    /**
     * 设置是否可以上拉加载更多
     */
    public void setCanPullUp(boolean isPullUp) {
        this.isPullUp = isPullUp;

    }
}
