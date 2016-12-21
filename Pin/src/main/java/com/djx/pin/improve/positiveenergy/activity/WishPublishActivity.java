package com.djx.pin.improve.positiveenergy.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
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

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.djx.pin.R;
import com.djx.pin.adapter.SearchPositionAdapter;
import com.djx.pin.application.PinApplication;
import com.djx.pin.improve.base.activity.BasePermissionActivity;
import com.djx.pin.improve.helpmap.adapter.AddPicAdapter;
import com.djx.pin.improve.utils.DialogUtils;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.myutils.ConstantUtils;
import com.djx.pin.widget.MyRecyclerView;
import com.foamtrace.photopicker.PhotoPickerActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by 柯传奇 on 2016/12/9 0009.
 */
public class WishPublishActivity extends BasePermissionActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    @Bind(R.id.recyclerView)
    MyRecyclerView recyclerView;
    @Bind(R.id.tv_location)
    TextView tv_location;
    @Bind(R.id.et_content)
    EditText et_content;
    private WishPublishPresenter presenter;
    private AddPicAdapter adapter;
    private ArrayList<String> img_paths;
    private EditText dialog_position;
    private ListView dialog_listview;
    private ImageView dialog_iv_clear;
    private AlertDialog.Builder builder;
    private ArrayList<SuggestionResult.SuggestionInfo> suggestionInfos;
    private SuggestionSearch suggestionSearch;
    private SearchPositionAdapter positionAdapter;//位置选择dialog
    private AlertDialog alertDialog;
    private String currentCity;
    private double currentlatitude,currentlongitude;

    /**
     * @return 布局id
     */
    @Override
    protected int getContentViewId() {
        return R.layout.activity_wish_publish;
    }

    @Override
    protected void initWidget() {
        recyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3,GridLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new AddPicAdapter(this);
        recyclerView.setAdapter(adapter);
        img_paths = new ArrayList<>();

        presenter = new WishPublishPresenter(this);
        BDLocation bdLocation = PinApplication.getMyApp().getBdLocation();
        currentlatitude = bdLocation.getLatitude();
        currentlongitude = bdLocation.getLongitude();
        initLocationDialog();
    }

    public void initLocationDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.layout_dialog_searchposition, null);
        dialog_position = (EditText) view.findViewById(R.id.et_SearchPosition);
        dialog_listview = (ListView) view.findViewById(R.id.lv_Position);
        dialog_iv_clear = (ImageView) view.findViewById(R.id.iv_Clear);
        dialog_iv_clear.setOnClickListener(this);
        view.findViewById(R.id.iv_Back).setOnClickListener(this);

        dialog_position.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(currentCity) || TextUtils.isEmpty(s)) {
                    dialog_iv_clear.setVisibility(View.INVISIBLE);
                    suggestionInfos.clear();
                    adapter.notifyDataSetChanged();
                    return;
                }
                dialog_iv_clear.setVisibility(View.VISIBLE);
                suggestionSearch.requestSuggestion((new SuggestionSearchOption()).city(currentCity).keyword(s.toString()));
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.create();
        suggestionInfos = new ArrayList<>();
        positionAdapter = new SearchPositionAdapter(this);
        dialog_listview.setAdapter(positionAdapter);
    }

    @Override
    protected void initEvent() {
        adapter.setAddPidListener(new AddPicAdapter.AddPicListener() {
            @Override
            public void addPic() {
                /**添加图片对话框相关控件*/
                DialogUtils.AddImageDialog(WishPublishActivity.this, new DialogUtils.SlectAlbumListener() {
                    @Override
                    public void onClick() {//相册
                        selectPhotoFromAlbum();
                    }
                }, new DialogUtils.SlectCameraListener() {
                    @Override
                    public void onClick() {//相机
                        takePhotoByPermission();
                    }
                });
            }
        });
        adapter.setDeletePicListener(new AddPicAdapter.DeletePicListener() {
            @Override
            public void deletePic(int pos) {
                img_paths.remove(pos);
                imagePaths.remove(pos);
            }
        });
        suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                suggestionInfos.clear();
                if (suggestionResult != null && suggestionResult.getAllSuggestions() != null) {
                    for (SuggestionResult.SuggestionInfo e :
                            suggestionResult.getAllSuggestions()) {
                        if (e.uid.length() == 0 || e.city.length() == 0 || e.district.length() == 0) {
                            continue;
                        }
                        suggestionInfos.add(e);
                    }
                    positionAdapter.setList(suggestionInfos);
                    positionAdapter.notifyDataSetChanged();
                }
            }
        });
        dialog_listview.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        suggestionSearch.destroy();
    }
    @OnClick(R.id.tv_send)
    void send(View v){//发送
        presenter.sendData(et_content.getText().toString().trim(),currentlatitude,currentlongitude,tv_location.getText().toString().trim(),img_paths);
    }
    @OnClick(R.id.tv_cance)
    void cance(View v){//取消
        finish();
    }
    @OnClick(R.id.tv_location)
    void selectLocation(View v){//取消
        //位置检索对话框
        if(alertDialog == null){
            alertDialog = builder.show();
            Window window = alertDialog.getWindow();
            //window.setBackgroundDrawable(getResources().getDrawable(R.drawable.dialog_backgound_roundcorner));
            WindowManager.LayoutParams windowAttributes = window.getAttributes();
            windowAttributes.width = (int) (getWindowManager().getDefaultDisplay().getWidth() * 0.9);
            windowAttributes.height = (int) (getWindowManager().getDefaultDisplay().getHeight() * 0.8);
            windowAttributes.gravity = Gravity.CENTER;
            window.setAttributes(windowAttributes);
        }
        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.e("onActivityResult"+requestCode);
        if (resultCode == Activity.RESULT_OK ) {
            switch (requestCode) {
                case ConstantUtils.INTENT_IMAGE_CAPTURE:
                    img_paths.add(path_Camera);
                    imagePaths.add(path_Camera);
                    adapter.addData(path_Camera);
                    break;
                case ConstantUtils.INTENT_READ_ALBUM:
                    if(null != data){
                        ArrayList<String> photos = data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT);
                        img_paths.clear();
                        img_paths.addAll(photos);
                        adapter.addAll(img_paths);
                        imagePaths.clear();
                        imagePaths.addAll(img_paths);
                    }
                    break;
            }
        }
    }

    /**
     * 设置location信息
     */
    public void setLocation(String location) {
        tv_location.setText(location);
    }

    /**
     * presenter反编码后初始化设置location信息
     */
    public void initAddressInfo(String currentCity) {
        this.currentCity = currentCity;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_Clear:
                dialog_position.setText("");
                break;
            case R.id.iv_Back:
                alertDialog.dismiss();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SuggestionResult.SuggestionInfo suggestionInfo = positionAdapter.getItem(position);
        tv_location.setText(suggestionInfo.city + "  " + suggestionInfo.district + "  " + suggestionInfo.key);
        currentCity = suggestionInfo.city;
        currentlatitude = suggestionInfo.pt.latitude;
        currentlongitude = suggestionInfo.pt.longitude;
        alertDialog.dismiss();
    }
}
