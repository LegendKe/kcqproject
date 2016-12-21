package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;

/**
 * Created by lenovo on 2016/6/27.
 */
public class CityAdapter extends MyBaseAdapter<String> {
    public CityAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(null==converView){
            viewHolder=new ViewHolder();
            converView=inflater.inflate(R.layout.gv_item_city,null);
            viewHolder.tv_city= (TextView) converView.findViewById(R.id.tv_city);
            converView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) converView.getTag();
        }
        viewHolder.tv_city.setText(list.get(position)+"");
        return converView;
    }
    class ViewHolder{
        TextView tv_city;
    }
}
