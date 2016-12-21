package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.djx.pin.R;

/**
 * 用户搜索位置是使用的Adapter
 * Created by lifel on 2016/6/6.
 */
public class SearchPositionAdapter extends MyBaseAdapter<SuggestionResult.SuggestionInfo>{

    public SearchPositionAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHolder viewHolder=null;
        if(null==converView){
            viewHolder=new ViewHolder();
            converView=inflater.inflate(R.layout.layout_listview_searchposition,null);
            viewHolder.tv_Position= (TextView) converView.findViewById(R.id.tv_Position);
            viewHolder.tv_District= (TextView) converView.findViewById(R.id.tv_District);
            converView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) converView.getTag();
        }
        viewHolder.tv_Position.setText(list.get(position).key);
        viewHolder.tv_District.setText(list.get(position).city+"  "+list.get(position).district);
        return converView;
    }
    class ViewHolder{
        TextView tv_Position;//联想的位置
        TextView tv_District;//联想位置所在的市区
    }

}
