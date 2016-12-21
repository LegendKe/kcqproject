package com.djx.pin.serverapiconfig;

import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2016/7/5 0005.
 */

public class ServerAPIConfig {

    public static String MainUrl = "http://dujoy.cn:8001/";//测试IP
    /**
     * 上传图片和视频
     */
    public static String UpdataMedia = MainUrl + "v1/inform/publish";

    /**
     * 获取七牛的Id和Token
     */
    public static String GetIDToken = MainUrl + "v1/qiniu/request/token";

    /**
     * 获取用户资料
     */
    public static String Get_UserInfo = MainUrl + "v1/user/info/view?";

    /**
     * 新浪微博登陆
     */
    public static String Do_Login_Weibo = MainUrl + "v1/user/login/weibo";
    /**
     * 退出登录
     */
    public static String Do_Logout = MainUrl + "v1/user/logout";

    /**
     * QQ登陆
     */
    public static String GetQQ_UserInfo = MainUrl + "v1/user/login/qq";
    /**
     * 文明墙发布
     */
    public static String SendCivilizationMassage = MainUrl + "v1/culture_wall/publish";

    /**
     * 车船抛物-发布
     */
    public static String Updata_Drop = MainUrl + "v1/drop/publish/";

    /**
     * 车窗抛物-查看列表
     */
    public static String Get_DropList = MainUrl + "v1/drop/list?";
    /**
     * 车窗抛物-回家列表
     */
    public static String Get_GoHomeList = MainUrl + "v1/gohome/list?";

    /**
     * 用户注册-手机
     */
    public static String Do_Register_Phone = MainUrl + "v1/user/register/mobile";

    /**
     * 请求短信验证码
     */

    public static String GET_Request_SMSCode = MainUrl + "v1/sms/request";

    /**
     * 文明墙发布
     */
    public static String LookCivilization = MainUrl + "v1/culture_wall/list?";
    /**
     * 七牛下载资源URl
     */
    public static String GetQiNiuUrl = MainUrl + "v1/qiniu/url";

    /**
     * 查看文明墙详情
     */

    public static String LookCivilizationDetail = MainUrl + "v1/culture_wall/details?";
    /**
     * 修改手机
     */

    public static String BundleOrChangePhone = MainUrl + "v1/user/mobile/edit";
    /**
     * 修改密码
     */

    public static String ChangePassword = MainUrl + "v1/user/password/edit";
    /**
     * 请求短信验证码
     */

    public static String RequestMassageCode = MainUrl + "v1/sms/request";
    /**
     * 验证短信验证码
     */

    public static String VerifyMassageCode = MainUrl + "v1/sms/verify";
    /**
     * 修改用户资料
     */
    public static String Updata_UserInfo = MainUrl + "v1/user/info/edit";

    /**
     * 分享
     */

    public static String Share = MainUrl + "v1/publish";

    /**
     * 手机登录
     */
    public static String Do_Login_Phone = MainUrl + "v1/user/login/mobile";

    /**
     * 回家-发布
     */
    public static String Updata_GoHome = MainUrl + "v1/gohome/publish/";
    /**
     * 查看第三方绑定状态
     */
    public static String LookDisanfangBundleState = MainUrl + "v1/user/bound/query?";

    /**
     * 实名认证
     */
    public static String RealNameAuth = MainUrl + "v1/user/auth/edit";
    /**
     * 修改个人资料
     */
    public static String ChangeNewData = MainUrl + "v1/user/info/edit";
    /**
     * 修改紧急联系人
     */
    public static String RequestEmergency = MainUrl + "v1/user/emergency/edit";

    /**
     * SOS求助-发布
     */
    public static String Updata_SOS = MainUrl + "v1/sos/publish";
    /**
     * SOS-发布SOS短信
     */
    public static String Updata_SOS_SMS = MainUrl + "v1/sos/publish_sms";

    /**
     * SOS-我安全了
     */
    public static String Updata_SOS_Safe=MainUrl+"v1/sos/cancel";


    /**
     * 求助-发单者发布
     */
    public static String Updata_Help_Net = MainUrl + "v1/reward/publish";


    /**
     * 求助-查看列表
     */
    public static String Get_HelpList = MainUrl + "v1/help/list?";

    /**
     * SOS-查看列表
     */
    public static String Get_SOSList = MainUrl + "v1/sos/list?";

    /**
     * 求助-查看详情
     */
    public static String Get_HelpDetail = MainUrl + "v1/help/details?";

    /**
     * 抢单接口
     */
    public static String Updata_AcceptOrder = MainUrl + "v1/help/book";

    /**
     * 求助-发单者发布
     */
    public static String Updata_Help = MainUrl + "v1/help/publish";
    /**
     * 获取我的求助
     */
    public static String MyHelper = MainUrl + "v1/user/help/list/publish?";
    /**
     * 获取助人等赏
     */
    public static String RewardWait = MainUrl + "v1/user/help/list/book?";

    /**
     * 获取助人等赏
     */
    public static String OnlineReward = MainUrl + "v1/reward/details?";
    /**
     * 获取芝麻信用参数
     */
    public static String ZhiMaXinYong = MainUrl + "v1/zhima/encrypt";
    /**
     * 获取芝麻信用分数
     */
    public static String ZhiMaXinYongScore = MainUrl + "v1/zhima/score/get";
    /**
     * 获取芝麻信用解密
     */
    public static String ZhiMaXinYongDecrypt = MainUrl + "v1/zhima/decrypt";
    /**
     * 开启位置共享
     */
    public static String LocationShare = MainUrl + "v1/user/location/edit";
    /**
     * 分享——创建
     */
    public static String SendSharePost = MainUrl + "v1/share/publish";

    /**
     * 用户资料-更新融云token
     */
    public static String RefreshRongToken = MainUrl + "v1/user/refresh/rongyun_token";
    /**
     * 评论-创建
     */
    public static String UPdata_Comment = MainUrl + "v1/comment/publish";

    /**
     * 吐槽-创建
     */
    public static String UPdata_Teasing = MainUrl + "v1/report/publish";
    /**
     * 余额支付
     */
    public static String PayVerify = MainUrl + "v1/pay/balance";

    /**
     * 支付宝签名
     */
    public static String ZhiFuBaoQianMing = MainUrl + "v1/alipay/sign";
    /**
     * 支付宝回调
     */
    public static String ZhiFuBaoHuiDiao = MainUrl + "v1/pay/alipay/callback";
    /**
     * 支付宝支付
     */
    public static String ZhiFuBaoPay = MainUrl + "v1/pay/alipay";
    /**
     * 钱包明细
     */
    public static String WalletLog = MainUrl + "v1/user/wallet/log?";

    /**
     * 用户登录-微信
     */
    public static String Do_Login_WeiXin = MainUrl + "v1/user/login/wechat";
    /**
     * 网络悬赏-查看列表
     */
    public static String Get_OnlieRewardList = MainUrl + "v1/reward/list?";
    /**
     * 钱包记录
     */
    public static String balanceLog = MainUrl + "v1/user/balance/log?";

    /**
     * 接单者回答
     */
    public static String RewardBook = MainUrl + "v1/reward/book";

    /**
     * 求助-查看订单状态
     */
    public static String Get_RewardStatus = MainUrl + "v1/help/status?";

    /**
     * 查看地图周围人
     * */
    public static String AroundMe = MainUrl + "v1/help/map";

    /**
     * 积分记录
     */
    public static String creditLog = MainUrl + "v1/user/point/log?";
    /**
     * 求助-发单者选择接单者
     */
    public static String Do_SignOrder = MainUrl + "v1/help/sign";

    /**
     * 求助-发单者确认完成
     */
    public static String Do_ConfirmOrder = MainUrl + "v1/help/confirm";
    /**
     * 网络悬赏-发单者打赏
     */
    public static String RewardOk = MainUrl + "v1/reward/confirm";
    /**
     * 网络悬赏-发单者拒绝打赏
     */
    public static String RewardNo = MainUrl + "v1/reward/refuse";
    /**
     * 网络悬赏-发单者停止招募
     */
    public static String OnLine_RewardStop = MainUrl + "v1/reward/cancel";
    /**
     * 线下悬赏-发单者停止招募
     */
    public static String OffLine_RewardStop = MainUrl + "v1/help/cancel";
    /**
     * pin一下
     */
    public static String HelperPin = MainUrl + "v1/help/share";
    /**
     * 求助-发单者评价
     */
    public static String Do_DistributeComment = MainUrl + "v1/help/evaluate/publisher";
    /**
     * 查看溯源
     */
    public static String Get_SorceList = MainUrl + "v1/help/source?";

    /**
     * 用户钱包充值
     */
    public static String PurseRecharge = MainUrl + "v1/user/recharge?";
    /**
     * 用户钱包提现
     */
    public static String PurseWithdraw = MainUrl + "v1/user/withdraw";

    /**
     * 用户钱包提现记录
     */
    public static String PurseWithdrawLog = MainUrl + "v1/user/withdraw/log";

    /**
     * 走失儿童列表
     */
    public static String LostChildList = MainUrl + "v1/lost/list?";
    /**
     * 走失儿童详情
     */
    public static String LostChildDetail = MainUrl + "v1/lost/details?";

    /**
     * 求助-接单者放弃订单
     */
    public static String Do_Giveup = MainUrl + "v1/help/giveup";

    /**
     * SOS-接单者完成任务
     */
    public static String Do_SOSCompelete = MainUrl + "v1/sos/complete";

    /**
     * SOS-发单者确认完成
     */
    public static String Do_SOSConfirm=MainUrl+"v1/sos/confirm";

    /**
     * SOS-发单者拒绝支付&申诉
     */
    public static String Do_SOSRefuse = MainUrl + "v1/sos/refuse";


    /**
     * SOS-查看详情
     */

    public static String Do_SOSDetail = MainUrl + "v1/sos/details?";


    /**
     * 求助-接单者标记完成
     */
    public static String Do_CompeleteTask = MainUrl + "v1/help/complete";

    /**
     * 求助-发单者拒绝完成&申诉
     */
    public static String Do_HelpRefuse = MainUrl + "v1/help/refuse";
    /**
     * 用户登录-找回密码
     */
    public static String PassWordReset = MainUrl + "v1/user/password/reset";

    /**
     * 求助-接单者申诉
     */
    public static String Do_RewardOfflineAppeal=MainUrl+"v1/help/appeal";

    /**
     * SOS-接单者接单
     */
    public static String Do_SOSReceive=MainUrl+"v1/sos/book";

    /**
     * SOS-更新地理位置
     */
    public static String Do_UpdataLocation=MainUrl+"v1/sos/update/location";


    /**
     * SOS-接单者完成任务
     */
    public static String Do_ReceiverCompelete=MainUrl+"v1/sos/complete";


    /**
     * 文明墙分享链接
     * */
    public static String CivilizationShare="http://www.dujoy.cn/#/access/police2?id=";
    /**
     * 线下悬赏分享链接：
     * */
    public static String OffLineShare="http://www.dujoy.cn/#/access/police3?id=";
    /**
     * 网络悬赏分享链接:
     * */
    public static String OnLineShare="http://www.dujoy.cn/#/access/police4?id=";
    /**
     * 走失儿童分享链接:
     * */
    public static String LostChildShare="http://www.dujoy.cn/#/access/police?id=";
    /**
     * SOS分享链接短信通知:
     * */
    public static String SOSShare="http://www.dujoy.cn/#/access/last?id=";

    /**
     * 公司Logo图标地址
     * */
    public static String Logo="http://www.dujoy.cn/img/djx.png";


    /**
     * 求助，查看申诉详情/v1/help/appeal/details?
     */
    public static String APPEAL_DETAIL=MainUrl+"v1/help/appeal/details?session_id=";

    /**
     * 服务列表(技能列表)，查看申诉详情/v1/skill/list
     */
    public static String SKILL_LIST=MainUrl+"/v1/skill/list";///v1/skill/publish

    /**
     * 添加技能
     */
    public static String ADD_SKILL=MainUrl+"/v1/skill/publish";

    /**
     * 修改技能
     */
    public static String UPDATE_SKILL=MainUrl+"/v1/skill/update";

    /**
     * 删除技能
     */
    public static String DELETE_SKILL_LIST=MainUrl+"/v1/skill/delete";

    /**
     * 搜索技能
     */
    public static String SEARCH_SKILL=MainUrl+"/v1/skill/search";


}


