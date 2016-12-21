package com.djx.pin.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.baidu.platform.comapi.map.C;
import com.djx.pin.R;
import com.djx.pin.activity.MainActivity;
import com.djx.pin.application.PinApplication;
import com.djx.pin.base.OldBaseActivity;
import com.djx.pin.beans.StaticBean;
import com.djx.pin.beans.UserBean;
import com.djx.pin.beans.UserInfo;
import com.djx.pin.business.AppConstants;
import com.djx.pin.serverapiconfig.ServerAPIConfig;
import com.djx.pin.utils.AndroidAsyncHttp;
import com.djx.pin.utils.FileUtil;
import com.djx.pin.utils.LogUtil;
import com.djx.pin.utils.MemoryManager;
import com.djx.pin.utils.ToastUtil;
import com.djx.pin.weixin.WXConstants;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class WXEntryActivity extends OldBaseActivity implements IWXAPIEventHandler {

	private IWXAPI api;
	private Context CONTEXT=WXEntryActivity.this;
	SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxentry);
		LogUtil.e(this, "enter WXEntryActivity");
		sp=getSharedPreferences(StaticBean.USER_INFO,MODE_PRIVATE);

		// 通过WXAPIFactory工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this, WXConstants.APP_ID,
				false);
		api.registerApp(WXConstants.APP_ID);

		api.handleIntent(getIntent(), this);

	}

	@Override
	public void onReq(BaseReq req) {
		// TODO 自动生成的方法存根

	}

	// 拉起微信,返回的结果
	@Override
	public void onResp(BaseResp resp) {

		//判断回调返回类型
		switch (resp.getType()){
			//微信登录回调
			case ConstantsAPI.COMMAND_SENDAUTH:
				// 判断返回结果码
				switch (resp.errCode) {
					// 用户同意
					case BaseResp.ErrCode.ERR_OK:
						// 获取所需的code
						String code = ((SendAuth.Resp) resp).code;

						LogUtil.e(this, "enter WXEntryActivity---2----"+code);
						// 使用参数拼接url
						String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
								+ WXConstants.APP_ID
								+ "&secret="
								+ WXConstants.APP_SECRET
								+ "&code=" + code + "&grant_type=authorization_code";
						getWXAccess_Token(url);
						WXEntryActivity.this.finish();
						break;
					// 用户拒绝授权
					case BaseResp.ErrCode.ERR_AUTH_DENIED:
						ToastUtil.shortshow(CONTEXT,R.string.toast_weixin_login_refuse);
						WXEntryActivity.this.finish();
						break;
					// 用户取消
					case BaseResp.ErrCode.ERR_USER_CANCEL:
						ToastUtil.shortshow(CONTEXT,R.string.toast_weixin_login_cancle);
						WXEntryActivity.this.finish();
						break;
				}
				break;
			//微信分享回调
			case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
				// 判断返回结果码
				switch (resp.errCode) {
					// 分享成功
					case BaseResp.ErrCode.ERR_OK:
						ToastUtil.shortshow(WXEntryActivity.this,"分享成功");
						WXEntryActivity.this.finish();
						break;
					// 拒绝分享
					case BaseResp.ErrCode.ERR_AUTH_DENIED:
						ToastUtil.shortshow(CONTEXT,R.string.toast_weixin_share_refuse);
						WXEntryActivity.this.finish();
						break;
					// 取消分享
					case BaseResp.ErrCode.ERR_USER_CANCEL:
						ToastUtil.shortshow(CONTEXT,R.string.toast_weixin_share_cancle);
						WXEntryActivity.this.finish();
						break;
				}
				break;
			//微信支付回调
			case ConstantsAPI.COMMAND_PAY_BY_WX:
				break;
		}


	}

	// 获取微信的Access_Token
	public void getWXAccess_Token(String url) {
		// 获取用户的access_token
		AndroidAsyncHttp.get(url, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
								  JSONObject response) {
				// TODO 自动生成的方法存根
				super.onSuccess(statusCode, headers, response);
				LogUtil.e(CONTEXT, "getWXAccess_Token=----3------"+response);
				try {
					String access_token = response.getString("access_token");
					// 判断access_token是否存在,如果不存在直接返回
					if (null == access_token || access_token.length() == 0) {
						LogUtil.e(WXEntryActivity.this, "微信access_token异常");
						return;
					}
					String openid = response.getString("openid");
					RequestParams params=new RequestParams();
					params.put("access_token",access_token);
					params.put("expires_in",600);
					params.put("refresh_token","");//此处可传空字符串
					params.put("openid",openid);
					params.put("unionid","");//此处可传空字符串
					String JPush_RegistrationID = sp.getString("JPush_RegistrationID", null);
					LogUtil.e(CONTEXT, "JPush_RegistrationID========" + JPush_RegistrationID);
					if (null != JPush_RegistrationID && 0 != JPush_RegistrationID.length()) {
						params.put("registration_id", JPush_RegistrationID);
					} else {
						LogUtil.e(CONTEXT, "极光推送registration_id异常==========" + JPush_RegistrationID);
					}
					AndroidAsyncHttp.post(ServerAPIConfig.Do_Login_WeiXin, params, new AsyncHttpResponseHandler() {
						@Override
						public void onSuccess(int i, Header[] headers, byte[] bytes) {
							String str_json=new String(bytes);
							try {

								JSONObject obj = new JSONObject(str_json);
								LogUtil.e(CONTEXT, "getWXAccess_Token=----4------"+str_json);

								if (0 != obj.getInt("code")) {
									LogUtil.e(CONTEXT,"code="+obj.getInt("code"));
									ToastUtil.shortshow(CONTEXT, R.string.toast_login_failure);
									return;
								}
								obj = obj.getJSONObject("result");
								SharedPreferences.Editor et = sp.edit();
								et.putString("session_id", obj.getString("session_id"));
								et.putString("user_id", obj.getString("user_id"));
								et.putString("rongyun_token", obj.getString("rongyun_token"));
								et.putBoolean("isLogined", true);
								et.commit();
								LogUtil.e(CONTEXT, "---------code=0-----=----5------"+str_json);
								getUserInfo(obj.getString("session_id"), obj.getString("user_id"));
							} catch (JSONException e) {
								LogUtil.e(CONTEXT, "enter catch");
								LogUtil.e(CONTEXT, "str_json:==========="+str_json);
								e.printStackTrace();
							}


						}

						@Override
						public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
							LogUtil.e(CONTEXT,"网络连接失败");
							ToastUtil.shortshow(CONTEXT,R.string.toast_error_net);
						}
					});
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					LogUtil.e(WXEntryActivity.this,
							"enter catch=" + e.toString());
					LogUtil.e(CONTEXT, "response:==========="+response);
					ToastUtil.shortshow(WXEntryActivity.this, "网络连接异常");
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  String responseString, Throwable throwable) {
				// TODO 自动生成的方法存根
				super.onFailure(statusCode, headers, responseString, throwable);
				LogUtil.e(WXEntryActivity.this, "enter onFailure");
			}
		});
	}



	/**
	 * 获取用户信息
	 *
	 * @param session_id 用户的session_id
	 * @param user_id    用户的user_id
	 */
	public void getUserInfo2(String session_id, String user_id) {
		AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				String str_json = new String(bytes);
				LogUtil.e(WXEntryActivity.this, "------------------------------------------------------str_json=======6======:"+str_json);
				try {
					JSONObject obj = new JSONObject(str_json);
					if (obj.getInt("code") != 0) {
						ToastUtil.shortshow(CONTEXT, R.string.toast_login_failure);
						return;
					}
					Gson gson = new Gson();
					UserBean user = gson.fromJson(obj.getJSONObject("result").toString(), UserBean.class);
					if (user == null) {
						return;
					}
					UserInfo.updateUserInfoPref(getApplicationContext(), user);
					rongLogin(obj.getString("session_id"));
					UserInfo.rongLogin(getApplicationContext(), UserInfo.getSessionID(getApplicationContext()));
				} catch (JSONException e) {
					LogUtil.e(CONTEXT, "enter catch");
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
				LogUtil.e(CONTEXT, "网络连接异常");
			}
		});

	}








	/**
	 * 获取用户信息
	 *
	 * @param session_id 用户的session_id
	 * @param user_id    用户的user_id
	 */
	public void getUserInfo(String session_id, String user_id) {
		AndroidAsyncHttp.get(ServerAPIConfig.Get_UserInfo + "session_id=" + session_id + "&user_id=" + user_id, null, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				String str_json = new String(bytes);
				try {
					JSONObject obj = new JSONObject(str_json);
					if (obj.getInt("code") != 0) {
						ToastUtil.shortshow(CONTEXT, R.string.toast_login_failure);
						return;
					}
					obj = obj.getJSONObject("result");
					SharedPreferences.Editor et = sp.edit();
					et.putString("user_id", obj.getString("user_id"));
					et.putString("nickname", obj.getString("nickname"));
					et.putString("portrait", obj.getString("portrait"));
					et.putInt("gender", obj.getInt("gender"));
					et.putString("mobile", obj.getString("mobile"));
					et.putString("country_code", obj.getString("country_code"));
					et.putString("province", obj.getString("province"));
					et.putString("city", obj.getString("city"));
					et.putString("district", obj.getString("district"));
					et.putString("birthday", obj.getString("birthday"));
					et.putInt("credit", obj.getInt("credit"));
					et.putInt("balance", obj.getInt("balance"));
					et.putInt("credit_balance", obj.getInt("credit_balance"));
					et.putInt("is_auth", obj.getInt("is_auth"));
					et.putInt("is_show_location", obj.getInt("is_show_location"));

					et.putString("real_name", obj.getString("real_name"));
					et.putString("id_card", obj.getString("id_card"));
					et.putString("id_card_pic", obj.getString("id_card_pic"));
					et.putString("emergency_name", obj.getString("emergency_name"));
					et.putString("emergency_country_code", obj.getString("emergency_country_code"));
					et.putString("emergency_mobile", obj.getString("emergency_mobile"));
					et.putString("session_id", obj.getString("session_id"));
					et.putString("rongyun_token", obj.getString("rongyun_token"));
					et.commit();
					rongLogin(obj.getString("session_id"));
				} catch (JSONException e) {
					LogUtil.e(CONTEXT, "enter catch");
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				ToastUtil.shortshow(CONTEXT, R.string.toast_error_net);
				LogUtil.e(CONTEXT, "网络连接异常");
			}
		});

	}


















	/**
	 * 登陆融云,默认先刷新融云Token,然后在登陆
	 * @param session_id
	 */
	public void rongLogin(String session_id) {

		AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				String strJson = new String(bytes);
				try {
					JSONObject obj = new JSONObject(strJson);
					if (0 != obj.getInt("code")) {
						LogUtil.e("返回结果码=" + obj.getInt("code"));
						return;
					}
					obj=obj.getJSONObject("result");
					sp.edit().putString("rongyun_token",obj.getString("rongyun_token")).commit();
					connect(obj.getString("rongyun_token"));
				} catch (JSONException e) {
					LogUtil.e(CONTEXT, "enter catch");
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				LogUtil.e(CONTEXT, "enter onFailure");
			}
		};

		RequestParams params = new RequestParams();
		params.put("session_id", session_id);
		AndroidAsyncHttp.post(ServerAPIConfig.RefreshRongToken, params, res);
	}


	/**
	 * 更新融云token
	 *
	 * @param session_id
	 */
	public void refreshRongToken(String session_id) {
		AsyncHttpResponseHandler res = new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int i, Header[] headers, byte[] bytes) {
				String strJson = new String(bytes);
				try {
					JSONObject obj = new JSONObject(strJson);
					if (0 != obj.getInt("code")) {
						LogUtil.e("返回结果码=" + obj.getInt("code"));
						return;
					}
					obj=obj.getJSONObject("result");
					sp.edit().putString("rongyun_token",obj.getString("rongyun_token")).commit();
				} catch (JSONException e) {
					LogUtil.e(CONTEXT, "enter catch");
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
				LogUtil.e(CONTEXT, "enter onFailure");
			}
		};

		RequestParams params = new RequestParams();
		params.put("session_id", session_id);
		AndroidAsyncHttp.post(ServerAPIConfig.RefreshRongToken, params, res);
	}

	// 该方法获取用户在微信上的基本资料
	public void getWXUserInfo(String access_token, String openid) {
		String url_userinfo = "https://api.weixin.qq.com/sns/userinfo?access_token="
				+ access_token + "&openid=" + openid + "&lang=zh-CN";
		AndroidAsyncHttp.get(url_userinfo, new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,
								  JSONObject response) {
				// TODO 自动生成的方法存根
				super.onSuccess(statusCode, headers, response);

				// 获取到的用户信息
				try {
					int sex = response.getInt("sex");// 性别
					String nickname = response.getString("nickname");// 昵称
					String province = response.getString("province");// 省份(省份是拼音)
					String headimgurl = response.getString("headimgurl");// 头像下载url
					String country = response.getString("country");// 国家
					String city = response.getString("city");// 城市
					getWXHeadImg(headimgurl);

					LogUtil.e("response=" + response.toString());
					LogUtil.e("headimgurl=" + headimgurl);
				} catch (JSONException e) {
					// TODO 自动生成的 catch 块
					e.printStackTrace();
					LogUtil.e(WXEntryActivity.this,
							"enter catch=" + e.toString());
				}

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
								  String responseString, Throwable throwable) {
				// TODO 自动生成的方法存根
				super.onFailure(statusCode, headers, responseString, throwable);
				LogUtil.e(WXEntryActivity.this, "enter onFailure");
				ToastUtil.shortshow(WXEntryActivity.this, "网络连接异常");
			}
		});

	}

	// 获取微信用户头像,头像保存路径及名字待定
	public void getWXHeadImg(String url) {
		AndroidAsyncHttp.get(url, new BinaryHttpResponseHandler() {

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO 自动生成的方法存根
				String filePath = MemoryManager.getPhoneInSDCardPath();
				String fileName = "HeadImg.png";
				FileUtil.saveFile(WXEntryActivity.this, filePath, fileName,
						arg2);

			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
								  Throwable arg3) {
				// TODO 自动生成的方法存根
				LogUtil.e(WXEntryActivity.this, "enter onFailure");
				ToastUtil.shortshow(WXEntryActivity.this, "头像获取失败");
			}
		});

	}


	/**
	 * 建立与融云服务器的连接
	 *
	 * @param token
	 */
	private void connect(String token) {
		if (getApplicationInfo().packageName.equals(PinApplication.getCurProcessName(getApplicationContext()))) {

			/**
			 * IMKit SDK调用第二步,建立与服务器的连接
			 */
			RongIM.connect(token, new RongIMClient.ConnectCallback() {
				/**
				 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
				 */
				@Override
				public void onTokenIncorrect() {
					LogUtil.e("--融云登陆失败:Token 已经过期");
				}

				/**
				 * 连接融云成功
				 * @param userid 当前 token
				 */
				@Override
				public void onSuccess(String userid) {
					LogUtil.e("融云登陆成功" + userid);
					/*startActivity(MainActivity.class);
					WXEntryActivity.this.finish();*/
					Intent intent = new Intent(AppConstants.INTENT_ACTION_LOGIN);
					WXEntryActivity.this.sendBroadcast(intent);
				}

				/**
				 * 连接融云失败
				 * @param errorCode 错误码，可到官网 查看错误码对应的注释
				 */
				@Override
				public void onError(RongIMClient.ErrorCode errorCode) {
					LogUtil.e("融云登陆失败" + errorCode);
				}
			});
		}
	}
}
