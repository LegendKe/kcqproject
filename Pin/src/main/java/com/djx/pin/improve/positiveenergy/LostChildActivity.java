package com.djx.pin.improve.positiveenergy;

import android.view.View;

import com.djx.pin.R;
import com.djx.pin.improve.base.activity.BaseActivity;
import com.djx.pin.improve.positiveenergy.view.LostChildFragmentImpl;

import butterknife.OnClick;

/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class LostChildActivity extends BaseActivity {
    /**
     * @return 布局id
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_lost_child;
    }

    @Override
    protected void initWidget() {
        addFragment(R.id.framelayout,new LostChildFragmentImpl());
    }

    @OnClick(R.id.iv_back)
    void back(View v){
        finish();
    }
}
