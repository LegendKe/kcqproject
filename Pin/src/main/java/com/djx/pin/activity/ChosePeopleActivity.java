package com.djx.pin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.adapter.PopChosePeopleAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.ChosePeopleInfo;
import com.djx.pin.utils.ToastUtil;

/**
 * Created by Administrator on 2016/7/2 0002.
 */
public class ChosePeopleActivity extends OldBaseActivity implements View.OnClickListener, PopChosePeopleAdapter.OnChosePeople {

    private TextView tv_PeopleNumber_MHA;
    private ListView lv_PeopleList_MHA;
    private Button bt_Complete_MHA;
    private PopChosePeopleAdapter popAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupwindow_mha_chosepeople);

        initPopView();
        initPopEvent();

    }
    private void initPopEvent() {
        bt_Complete_MHA.setOnClickListener(this);
        popAdapter=new PopChosePeopleAdapter(this,this);
        initPopData();
        lv_PeopleList_MHA.setAdapter(popAdapter);
        tv_PeopleNumber_MHA.setText(popAdapter.getCount()+"");
    }


    private void initPopView() {
        tv_PeopleNumber_MHA = (TextView) findViewById(R.id.tv_PeopleNumber_MHA);
        lv_PeopleList_MHA = (ListView)findViewById(R.id.lv_PeopleList_MHA);
        bt_Complete_MHA = (Button) findViewById(R.id.bt_Complete_MHA);

    }
    private void initPopData() {
        ChosePeopleInfo info =new ChosePeopleInfo(R.mipmap.ic_defualtavater,"小明","北京杭州上海");
        popAdapter.addData(info);
        popAdapter.addData(info);
        popAdapter.addData(info);
        popAdapter.addData(info);
    }

    @Override
    public void setOnChosePeopleListener(View v) {
        ChosePeopleInfo info=popAdapter.getItem((Integer) v.getTag());
        ToastUtil.shortshow(this,"选择了第"+v.getTag()+"人");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_Complete_MHA:
                this.finish();
                break;
        }
    }
}
