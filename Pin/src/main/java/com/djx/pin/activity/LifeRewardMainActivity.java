package com.djx.pin.activity;

import android.view.View;
import android.widget.RelativeLayout;

import com.djx.pin.R;
import com.djx.pin.fragment.OnlineFragment;
import com.djx.pin.improve.base.activity.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;


/**
 *
 */
public class LifeRewardMainActivity extends BaseActivity {

    public static int height_title;//标题栏高度,拱fragment使用

    @Bind(R.id.title)
    RelativeLayout title;

    @Override
    protected void initWidget() {
        addFragment(R.id.framelayout,new OnlineFragment());
    }

    @Override
    protected void initEvent() {
        title.post(new Runnable() {
            @Override
            public void run() {
                height_title = title.getBottom();
            }
        });
    }

    /**
     * @return 布局id
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_lifereward;
    }


    @OnClick(R.id.iv_back)
    void back(View v){finish();}

}
