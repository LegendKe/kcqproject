package com.djx.pin.sina;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.djx.pin.R;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.ToastUtil;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler.Response;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * 接收分享后微博返回的数据
 * 
 * @author 高树平
 * 
 */
public class WBShareActivity extends Activity implements Response {

	private IWeiboShareAPI mWeiboShareAPI;
	Context CONTEXT=WBShareActivity.this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wbshare);

		if (getIntent() != null) {
			LogUtil.e("intent!=null");
		}

		onNewIntent(getIntent());
	}

	// 实现WeiBohandler Response接口,接收分享后微博返回的数据
	protected void onNewIntent(Intent intent) {
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, SinaConstants.APP_KEY);
		mWeiboShareAPI.handleWeiboResponse(intent, this);
	}

	// 接收微博客户端分享后返回的结果
	@Override
	public void onResponse(BaseResponse baseResp) {
		// TODO 自动生成的方法存根
		switch (baseResp.errCode) {
		// 分享正常
		case WBConstants.ErrorCode.ERR_OK:
			LogUtil.e("分享正常");
			ToastUtil.shortshow(CONTEXT,R.string.toast_sina_share_success);
			break;
		// 用户取消分享
		case WBConstants.ErrorCode.ERR_CANCEL:
			LogUtil.e("用户取消分享");
			ToastUtil.shortshow(CONTEXT,R.string.toast_sina_share_cancle);
			break;
		// 分享失败
		case WBConstants.ErrorCode.ERR_FAIL:
			ToastUtil.shortshow(CONTEXT,R.string.toast_sina_share_fail);
			LogUtil.e("分享失败");
			break;
		}
		this.finish();
	}
}
