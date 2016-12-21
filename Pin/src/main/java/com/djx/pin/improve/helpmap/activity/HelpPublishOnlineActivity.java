package com.djx.pin.improve.helpmap.activity;

import android.os.Bundle;

import com.djx.pin.R;
import com.djx.pin.improve.base.activity.BaseToolbarActivity;
import com.djx.pin.improve.helpmap.fragment.HelpPublishFragment;
import com.djx.pin.utils.myutils.ConstantUtils;

/**
 * Created by 柯传奇 on 2016/11/29 0029.
 */
public class HelpPublishOnlineActivity extends BaseToolbarActivity{

    /**
     * @return 布局id
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_help_online_pub;
    }

    @Override
    protected void initWidget() {
        HelpPublishFragment helpPublishFragment = new HelpPublishFragment();
        Bundle bundle = new Bundle();
        bundle.putString("HELP_TYPE", ConstantUtils.HELP_ONLINE);
        helpPublishFragment.setArguments(bundle);
        addFragment(R.id.framelayout,helpPublishFragment);
    }
}
