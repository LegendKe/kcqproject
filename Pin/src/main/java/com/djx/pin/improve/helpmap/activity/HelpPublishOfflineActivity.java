package com.djx.pin.improve.helpmap.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.djx.pin.R;
import com.djx.pin.adapter.SearchPositionAdapter;
import com.djx.pin.improve.base.activity.BaseToolbarActivity;
import com.djx.pin.improve.helpmap.fragment.HelpPublishFragment;
import com.djx.pin.utils.myutils.ConstantUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 柯传奇 on 2016/11/29 0029.
 */
public class HelpPublishOfflineActivity extends BaseToolbarActivity implements OnGetSuggestionResultListener, View.OnClickListener, TextWatcher, AdapterView.OnItemClickListener {

    @Bind(R.id.tv_location)
    public TextView tv_location;

    private AlertDialog.Builder builder;//位置选择对话框
    private ListView lv_position;
    private SearchPositionAdapter adapter;//位置选择适配器
    private List<SuggestionResult.SuggestionInfo> list = null;
    private ImageView iv_clear;
    private SuggestionSearch suggestionSearch;
    private EditText et_position;
    private AlertDialog alertDialog;
    private ReverseGeoCodeResult.AddressComponent addressDetail;

    public double currentlongitude;
    public double currentlatitude;
    public String currentCity;
    public String currentDistrict;
    public String currentProvince;

    /**
     * @return 布局id
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_help_offline_publish;
    }

    @Override
    protected boolean initBundle(Bundle bundle) {
        addressDetail = bundle.getParcelable("addressDetail");
        currentlatitude = bundle.getDouble("latitude");
        currentlongitude = bundle.getDouble("longitude");
        currentCity = addressDetail.city;
        currentDistrict = addressDetail.district;
        currentProvince = addressDetail.province;
        return true;
    }

    @Override
    protected void initWidget() {
        HelpPublishFragment helpPublishFragment = new HelpPublishFragment();
        Bundle bundle = new Bundle();
        bundle.putString("HELP_TYPE", ConstantUtils.HELP_OFFLINE);
        helpPublishFragment.setArguments(bundle);
        addFragment(R.id.framelayout, helpPublishFragment);
        tv_location.setText(addressDetail.city + "  " + addressDetail.street + "  " + addressDetail.streetNumber);
        initLocationDialog(this);
    }

    @Override
    protected void initEvent() {
        suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(this);
        lv_position.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        suggestionSearch.destroy();
    }

    /**
     * 选择位置
     *
     * @param v
     */
    @OnClick(R.id.tv_location)
    public void selectLocation(View v) {
        //位置检索对话框
        if(alertDialog == null){
            alertDialog = builder.show();
            Window window = alertDialog.getWindow();
            window.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_backgound_roundcorner));
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.9);
            windowAttributes.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.8);
            windowAttributes.gravity = Gravity.CENTER;
            window.setAttributes(windowAttributes);
        }
        alertDialog.show();
    }

    public void initLocationDialog(Context mContext) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_dialog_searchposition, null);
        et_position = (EditText) view.findViewById(R.id.et_SearchPosition);
        lv_position = (ListView) view.findViewById(R.id.lv_Position);
        iv_clear = (ImageView) view.findViewById(R.id.iv_Clear);
        iv_clear.setOnClickListener(this);
        view.findViewById(R.id.iv_Back).setOnClickListener(this);

        et_position.addTextChangedListener(this);
        builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        builder.create();

        list = new ArrayList<>();
        adapter = new SearchPositionAdapter(this);
        lv_position.setAdapter(adapter);
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        list.clear();
        if (suggestionResult != null && suggestionResult.getAllSuggestions() != null) {
            for (SuggestionResult.SuggestionInfo e :
                    suggestionResult.getAllSuggestions()) {
                if (e.uid.length() == 0 || e.city.length() == 0 || e.district.length() == 0) {
                    continue;
                }
                list.add(e);
            }
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_Clear:
                et_position.setText("");
                break;
            case R.id.iv_Back:
                alertDialog.dismiss();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(currentCity) || TextUtils.isEmpty(s)) {
            iv_clear.setVisibility(View.INVISIBLE);
            list.clear();
            adapter.notifyDataSetChanged();
            return;
        }
        iv_clear.setVisibility(View.VISIBLE);
        suggestionSearch.requestSuggestion((new SuggestionSearchOption()).city(currentCity).keyword(s.toString()));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SuggestionResult.SuggestionInfo suggestionInfo = adapter.getItem(position);
        tv_location.setText(suggestionInfo.city + "  " + suggestionInfo.district + "  " + suggestionInfo.key);

        currentDistrict = suggestionInfo.district;
        currentProvince = "";
        currentCity = suggestionInfo.city;
        currentlatitude = suggestionInfo.pt.latitude;
        currentlongitude = suggestionInfo.pt.longitude;

        alertDialog.dismiss();
    }
}
