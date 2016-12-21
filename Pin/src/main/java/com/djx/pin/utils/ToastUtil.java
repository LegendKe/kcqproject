package com.djx.pin.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static Toast toast = null;

	public static void longshow(Context context, String text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
		} else {
			toast.setText(text);
		}
		toast.show();
	}public static void longshow(Context context, int resId) {
		if (toast == null) {
			toast = Toast.makeText(context, resId, Toast.LENGTH_LONG);
		} else {
			toast.setText(resId);
		}
		toast.show();
	}

	public static void shortshow(Context context, String text) {
		if (toast == null) {
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			toast.setText(text);
		}
		toast.show();
	}public static void shortshow(Context context, int resId) {
		if (toast == null) {
			toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
		} else {
			toast.setText(resId);
		}
		toast.show();
	}

	public static void errorCode(Context context,int code) {
		switch (code) {
			case 1001:
				ToastUtil.shortshow(context, "请求参数列表不完整或错误");
				break;
			case 4001:
				ToastUtil.shortshow(context, "数据库错误");
				break;
			case 4003:
				ToastUtil.shortshow(context, "数据库连接错误");
				break;
			case 4004:
				ToastUtil.shortshow(context, "数据库参数错误");
				break;
			case 2001:
				ToastUtil.shortshow(context, "请登录");
				break;
			case 2002:
				ToastUtil.shortshow(context, "第三方账号已被使用");
				break;
			case 2003:
				ToastUtil.shortshow(context, "该手机号已注册可直接登陆");
				break;
			case 2004:
				ToastUtil.shortshow(context, "昵称重复");
				break;
			case 2005:
				ToastUtil.shortshow(context, "身份证号已被占用");
				break;
			case 2006:
				ToastUtil.shortshow(context, "密码错误");
				break;
			case 2007:
				ToastUtil.shortshow(context, "手机号错误");
				break;
			case 2008:
				ToastUtil.shortshow(context, "重复实名认证");
				break;
			case 2009:
				ToastUtil.shortshow(context, "用户未实名认证");
				break;
			case 2011:
				ToastUtil.shortshow(context, "第三方账号token无效");
				break;
			case 2012:
				ToastUtil.shortshow(context, "用户未设置紧急联系人");
				break;
			case 2013:
				ToastUtil.shortshow(context, "用户未绑定芝麻信用或信用金为0");
				break;
			case 2014:
				ToastUtil.shortshow(context, "用户余额不足");
				break;
			case 2021:
				ToastUtil.shortshow(context, "用户无效或用户无法进行相应操作");
				break;
			case 2022:
				ToastUtil.shortshow(context, "用户与内容id不匹配");
				break;
			case 3001:
				ToastUtil.shortshow(context, "内容id无效");
				break;
			case 3101:
				ToastUtil.shortshow(context, "重复参与同一个SOS订单");
				break;
			case 3102:
				ToastUtil.shortshow(context, "有未完成的SOS订单，重复发布SOS");
				break;
			case 3103:
				ToastUtil.shortshow(context, "接单者完成SOS订单失败（距离没达到要求）");
				break;
			case 3104:
				ToastUtil.shortshow(context, "SOS当前状态不能进行该操作");
				break;
			case 4101:
				ToastUtil.shortshow(context, "重复PIN求助");
				break;
			case 4102:
				ToastUtil.shortshow(context, "重复接求助订单");
				break;
			case 4103:
				ToastUtil.shortshow(context, "签单者超过上限");
				break;
			case 4104:
				ToastUtil.shortshow(context, "您已提交申诉，请耐心等待申诉结果");
				break;
			case 4105:
				ToastUtil.shortshow(context, "求助订单上一节点用户无效");
				break;
			case 4201:
				ToastUtil.shortshow(context, "网络悬赏重复分享");
				break;
			case 4202:
				ToastUtil.shortshow(context, "网络悬赏重复抢单");
				break;
			case 4204:
				ToastUtil.shortshow(context, "网络悬赏当前状态不能进行该操作");
				break;
			case 5001:
				ToastUtil.shortshow(context, "短信发送失败");
				break;
			case 5002:
				ToastUtil.shortshow(context, "短信验证码校验失败");
				break;
			case 5003:
				ToastUtil.shortshow(context, "重复请求短信验证码");
				break;
			case 5004:
				ToastUtil.shortshow(context, "短信服务商登录失败");
				break;
			case 5011:
				ToastUtil.shortshow(context, "融云token请求失败");
				break;
			case 5012:
				ToastUtil.shortshow(context, "融云信息刷新失败");
				break;
			case 5021:
				ToastUtil.shortshow(context, "芝麻信用授权查询失败");
				break;
			case 5022:
				ToastUtil.shortshow(context, "芝麻信用获取OpenId失败");
				break;
			case 5023:
				ToastUtil.shortshow(context, "芝麻信用签名校验不通过");
				break;
			case 5024:
				ToastUtil.shortshow(context, "芝麻信用签名校验不通过");
				break;
		}
	}
}
