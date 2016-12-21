package com.djx.pin.improve.helppeople;

import android.view.View;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.EventBeans;
import com.djx.pin.improve.base.fragment.BaseFragment;
import com.djx.pin.improve.helppeople.view.HelpPeopleOfflineFragment;
import com.djx.pin.improve.utils.DialogUtils;
import com.djx.pin.utils.myutils.ConstantUtils;

import butterknife.Bind;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by 柯传奇 on 2016/12/2 0002.
 */
public class HelpPeopleOfflineMainFragment extends BaseFragment{

    private HelpPeopleOfflineFragment helpPeopleOfflineFrament;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_help_people_offline;
    }

    @Bind(R.id.tv_order_all)
    TextView tv_order_all;
    @Bind(R.id.tv_order_distance)
    TextView tv_order_distance;
    @Bind(R.id.tv_order_intelligent)
    TextView tv_order_intelligent;
    @Bind(R.id.tv_order_price)
    TextView tv_order_price;

    @OnClick(R.id.ll_order_all)
    public void orderAll(View v){
        EventBus.getDefault().post(new EventBeans(ConstantUtils.EVENT_ORDER_CHANGE,0,0,0,0));
        colorChange(true,false,false,false);
        tv_order_all.setClickable(false);
    }
    @OnClick(R.id.ll_order_distance)
    public void orderDistance(View v){
        colorChange(false,false,false,true);
        DialogUtils.AddDistanceDialog(context, new DialogUtils.OnItemClickListener() {
            @Override
            public void onClick(int price_min, int price_max, int distance, int order) {
                EventBus.getDefault().post(new EventBeans(ConstantUtils.EVENT_ORDER_CHANGE,price_min,price_max,distance,order));
            }
        });

    }

    @OnClick(R.id.ll_order_intelligent)
    public void orderIntelligent(View v){
        colorChange(false,false,true,false);
        DialogUtils.AddIntelligentDialog(context, new DialogUtils.OnItemClickListener() {
            @Override
            public void onClick(int price_min,int price_max,int distance,int order) {
               //通知fragment
                EventBus.getDefault().post(new EventBeans(ConstantUtils.EVENT_ORDER_CHANGE,price_min,price_max,distance,order));
            }
        });
    }
    @OnClick(R.id.ll_order_price)
    public void orderPrice(View v){
        colorChange(false,true,false,false);
        DialogUtils.AddPriceDialog(context,  new DialogUtils.OnItemClickListener() {
            @Override
            public void onClick(int price_min, int price_max, int distance, int order) {
                EventBus.getDefault().post(new EventBeans(ConstantUtils.EVENT_ORDER_CHANGE,price_min,price_max,distance,order));
            }
        });
    }

    @Override
    protected void initView() {
        helpPeopleOfflineFrament = new HelpPeopleOfflineFragment();
        addFragment(R.id.framelayout,helpPeopleOfflineFrament);
        tv_order_all.setActivated(true);
    }

    public void colorChange(boolean b0,boolean b1,boolean b2,boolean b3){
        tv_order_all.setActivated(b0);
        tv_order_price.setActivated(b1);
        tv_order_intelligent.setActivated(b2);
        tv_order_distance.setActivated(b3);
    }

}
