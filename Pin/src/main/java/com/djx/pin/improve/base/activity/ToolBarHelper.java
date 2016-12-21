package com.djx.pin.improve.base.activity;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.djx.pin.R;
import com.djx.pin.utils.myutils.ScreenTools;

/**
 * Created by 柯传奇 on 2016/11/29 0029.
 */
public class ToolBarHelper {

    private Context mContext;
    private FrameLayout mContentView;
    /*用户定义的view*/
    private View mUserView;
    private Toolbar mToolBar;
    private LayoutInflater mInflater;
    /*
    * 两个属性
    * 1、toolbar是否悬浮在窗口之上
    * 2、toolbar的高度获取
    * */
    private static int[] ATTRS = {
            R.attr.windowActionBarOverlay,
            R.attr.actionBarSize
    };

    public ToolBarHelper(Context context, int layoutId) {
        this.mContext = context;
        mInflater = LayoutInflater.from(mContext);
        /*初始化整个内容*/
        initContentView();
        /*初始化用户定义的布局*/
        initUserView(layoutId);
        /*初始化toolbar*/
        initToolBar();
    }

    private void initContentView() {
        /*直接创建一个帧布局，作为视图容器的父容器*/
        mContentView = new FrameLayout(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);

    }

    private void initToolBar() {
        View view = mInflater.inflate(R.layout.toolbar, mContentView);
        mToolBar = (Toolbar) view.findViewById(R.id.id_tool_bar);
    }

    private void initUserView(int id) {
        mUserView = mInflater.inflate(id, null);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //TypedArray typedArray = mContext.getTheme().obtainStyledAttributes(ATTRS);
        /*获取主题中定义的悬浮标志*/
        //boolean overly = typedArray.getBoolean(0, false);
        /*获取主题中定义的toolbar的高度*/
        //int toolBarSize = (int) typedArray.getDimension((int) mContext.getResources().getDimension(R.dimen.abc_action_bar_default_height_material), 1);
        //typedArray.recycle();
        /*如果是悬浮状态，则不需要设置间距*/
        params.topMargin = ScreenTools.instance(mContext).dip2px(50);
        mContentView.addView(mUserView, params);

    }

    public FrameLayout getContentView() {
        return mContentView;
    }

    public Toolbar getToolBar() {
        return mToolBar;
    }
}

