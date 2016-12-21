package com.djx.pin.activity;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.djx.pin.R;
import com.djx.pin.adapter.SettingEFAddressPhoneAdapter;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.ContactPersonInfo;
import com.djx.pin.beans.StaticBean;

import java.io.InputStream;

/**
 * Created by Administrator on 2016/7/18 0018.
 */
public class SettingEmergencyFromAddressBookActivity extends OldBaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    LinearLayout ll_Back_SEFAPA;

    ListView lv_ContactMassage_SEFAPA;
    SettingEFAddressPhoneAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sefromabook);

        initView();
        initEvent();
        initData();
    }

    private void initData() {
        ContentResolver contentResolver = this.getContentResolver();
        //获取联系人的uri
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        // 这里是获取联系人表的电话里的信息  包括：名字，名字拼音，联系人id,电话号码,头像ID；
        // 然后在根据"sort-key"排序
        String[] projection = {"display_name", "sort_key", "contact_id",
                "data1", "photo_id"};
        Cursor cursor = contentResolver.query(uri, projection, null, null, "sort_key");
        adapter = new SettingEFAddressPhoneAdapter(this);
        if (cursor.moveToFirst()) {
            do {
                //手机号
                String phoneNumber = cursor.getString(3);
                //手机号id
                long phone_id = cursor.getLong(2);
                long photo_id = cursor.getLong(4);
                String name = cursor.getString(0);
                String sort_key = getSortKey(cursor.getString(1));
                //得到联系人头像Bitamp
                Bitmap contactPhoto = null;
                if (photo_id > 0) {
                    Uri photo_uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, photo_id);
                    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, photo_uri);
                    contactPhoto = BitmapFactory.decodeStream(input);
                } else {
                    //contactPhoto = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_defaultcontact);
                }
                ContactPersonInfo info = new ContactPersonInfo();
                if(contactPhoto != null){
                    info.bitmap = contactPhoto;
                }
                info.name = name;
                info.phone_number = phoneNumber;
                adapter.addData(info);
            } while (cursor.moveToNext());
        }
        cursor.close();
        lv_ContactMassage_SEFAPA.setAdapter(adapter);
    }

    private void initEvent() {
        ll_Back_SEFAPA.setOnClickListener(this);
        lv_ContactMassage_SEFAPA.setOnItemClickListener(this);

    }

    private void initView() {
        lv_ContactMassage_SEFAPA = (ListView) findViewById(R.id.lv_ContactMassage_SEFAPA);
        ll_Back_SEFAPA = (LinearLayout) findViewById(R.id.ll_Back_SEFAPA);
    }


    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKeyString 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private static String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_Back_SEFAPA:
                this.finish();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ContactPersonInfo info=adapter.getItem(position);
        String name=info.name;
        String PhoneNumber=info.phone_number;
        getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).edit().putString("name",name).commit();
        getSharedPreferences(StaticBean.ContactPerson, Context.MODE_PRIVATE).edit().putString("PhoneNumber",PhoneNumber).commit();
        this.finish();
    }
}
