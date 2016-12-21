package com.djx.pin.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.base.OldBaseActivity;

/**
 * Created by Administrator on 2016/7/19 0019.
 */
public class PurseCreditSesameActivity extends OldBaseActivity implements View.OnClickListener {

    LinearLayout ll_Back_PA;
    TextView tv_ZhiMa_Score,tv_ZhiMa_Evaluate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pursecreditsesame);
        initView();
        initEvent();
        Bundle bundle=getIntent().getExtras();
        int score=bundle.getInt("score");
        tv_ZhiMa_Score.setText(score+"");
        if (score>350&&score<550){
            tv_ZhiMa_Evaluate.setText("信用较差");
        }else if (score>550&&score<600){
            tv_ZhiMa_Evaluate.setText("信用中等");
        }else if (score>600&&score<650){
            tv_ZhiMa_Evaluate.setText("信用良好");
        }else if (score>650&&score<700){
            tv_ZhiMa_Evaluate.setText("信用优秀");
        }else if (score>700&&score<950){
            tv_ZhiMa_Evaluate.setText("信用极好");
        }
    }

    private void initEvent() {
        ll_Back_PA.setOnClickListener(this);
    }

    private void initView() {
        ll_Back_PA= (LinearLayout) findViewById(R.id.ll_Back_PA);
        tv_ZhiMa_Score= (TextView) findViewById(R.id.tv_ZhiMa_Score);
        tv_ZhiMa_Evaluate= (TextView) findViewById(R.id.tv_ZhiMa_Evaluate);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_Back_PA:
                this.finish();
                break;
        }
    }
}
