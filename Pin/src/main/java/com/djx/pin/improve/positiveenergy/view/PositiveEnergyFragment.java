package com.djx.pin.improve.positiveenergy.view;

import android.content.Intent;
import android.view.View;

import com.djx.pin.R;
import com.djx.pin.business.AppConstants;
import com.djx.pin.improve.base.fragment.BaseFragment;
import com.djx.pin.improve.positiveenergy.activity.WishPublishActivity;

import butterknife.OnClick;
/**
 * Created by 柯传奇 on 2016/12/8 0008.
 */
public class PositiveEnergyFragment extends BaseFragment {

    @Override
    protected int setLayoutId() {
        return R.layout.civilizationfragment;
    }
    @Override
    protected void initView() {
        addFragment(R.id.framelayout,new WishTreeFragmentImpl());
    }

    @OnClick(R.id.iv_user_avatar)
    void openDrawer(View v){
        Intent intent = new Intent(AppConstants.INTENT_ACTION_OPENDRAWER);
        getActivity().sendBroadcast(intent);
    }
    @OnClick(R.id.tv_i_wish)
    void wish(View v){
        startActivity(WishPublishActivity.class);
    }

}
