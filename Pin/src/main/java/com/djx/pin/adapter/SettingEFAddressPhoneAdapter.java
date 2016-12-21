package com.djx.pin.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.djx.pin.R;
import com.djx.pin.beans.ContactPersonInfo;
import com.djx.pin.myview.CircleImageView;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class SettingEFAddressPhoneAdapter extends MyBaseAdapter<ContactPersonInfo>{
    public SettingEFAddressPhoneAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {

        ViewHolder viewHolder=null;
        if (converView==null){
            viewHolder=new ViewHolder();
            converView=inflater.inflate(R.layout.lv_item_sefromaddressphone,null);
            viewHolder.circleImageView= (CircleImageView) converView.findViewById(R.id.cmg_ContactPerson_SEFAPA);
            viewHolder.tv_ContactName_SEFAPA= (TextView) converView.findViewById(R.id.tv_ContactName_SEFAPA);
            viewHolder.tv_ContactPhoneNumber_SEFAPA= (TextView) converView.findViewById(R.id.tv_ContactPhoneNumber_SEFAPA);
            converView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) converView.getTag();
        }

        ContactPersonInfo info=getItem(position);

        if(info.bitmap!= null){
            viewHolder.circleImageView.setImageBitmap(info.bitmap);
        }else {
            viewHolder.circleImageView.setImageResource(R.mipmap.ic_defaultcontact);
        }

        viewHolder.tv_ContactName_SEFAPA.setText(info.name);
        viewHolder.tv_ContactPhoneNumber_SEFAPA.setText(info.phone_number);
        return converView;
    }


    public class ViewHolder{

        CircleImageView circleImageView;
        TextView tv_ContactName_SEFAPA;
        TextView tv_ContactPhoneNumber_SEFAPA;

    }
}
