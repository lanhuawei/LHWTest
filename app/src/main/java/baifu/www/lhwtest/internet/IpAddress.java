package baifu.www.lhwtest.internet;

import baifu.www.lhwtest.utils.Utility;

/**
 * Created by Ivan.L on 2017/7/26.
 * 各项IP地址
 */

public class IpAddress {
    //测试
    public static final String baufydata = "http://wx.baifudata.com:8080";
    public static final String baufydatas = "http://wx.baifudata.com:8080";
    //正式
//	public static final String baufydata = "https://www.baifudata.com";
//	public static final String baufydatas = "http://www.baifudata.com";
    /**ip*/
    public static final String ip = "123.59.197.139";//"125.208.12.148";//131";//148
    /**端口 */
    public static final int port = 5605;
    /**软件下载地址 */
    public static final String download = baufydata+"/download.html";
    /**银行卡绑定 */
    public static final String BankCardBinding = baufydata+"/index.php/Interface/CardBind/BindBankCard?";
    /**银行接口*/
    public static final String BankList = baufydata+"/index.php/Interface/Bank/bankList?&sign=ce24e2249ab84e2832b9459a9b58f199&deviceUnique=Q0NL01512786";
    /**支行接口*/
    public static final String BankBranch = baufydata+"/index.php/Interface/Bank/branchList?";
    /**注册接口*/
    public static final String Registered = baufydata+"/index.php/Interface/Register/index";
    /**银行卡绑定查询*/
    public static final String BankCardBindingQuery = baufydata+"/index.php/Interface/CardBind/BindCardList?";
    /**上传图片*/
    public static final String UploadImage = baufydata+"/index.php/Interface/UploadImage/upload_image.html";
    /**是否注册*/
    public static final String isRegister = baufydata+"/index.php/Interface/deviceInfo/index?";
    /**获取口令*/
    public static final String Command = baufydata+"/index.php/Interface/PrivateKey/commKey.html?";
    /**交易查询*/
    public static final String TradingQuery = baufydata+"/index.php/Interface/TransactionList/Transaction.html?";
    /**城市接口*/
    public static final String City = "https://posp.baifudata.com/index.php/Interface/City/provinceList?deviceUnique=1";
    /**交易签名*/
    public static final String TranSign = baufydata+"/index.php/Interface/TransactionSign/TranSign.html";
    /**获取版本号*/
    public static final String judgeUpdate = baufydata+"/Public/Uploads/APPPack/androidversion.txt";
    /**下载apk*/
    public static final String downApk = baufydata+"/Public/Uploads/APPPack/MobilePayment.apk";
    /**签名补传*/
    public static final String AddTranSign = baufydata+"/index.php/Interface/TransactionSign/TranResign.html";
    /**冻结信息*/
    public static final String FrozenInfo = baufydata+"/index.php/Interface/Bank/fkmemo";
    /**设备信息接口,成功返回信息已添加*/
    public static final String index = baufydata +"/index.php/Interface/deviceInfo/index";
    /**通知消息*/
    public static final String androidMessage = baufydata +"/Public/Uploads/APPPack/androidMessage.txt";
    /**获取二维码*/
    public static final String getQr_code = baufydata +"/index.php/CmbcShaoma/Trade/smzf";
    /**加密获取二维码*/
    public static final String encipheredGetQr_code = baufydata +"/index.php/CmbcShaoma/Trade/smzf";
    /**无设备登录*/
    public static final String nodeviceLogin = baufydata +"/index.php/Interface/NodeviceLogin/index";
    /**查询二维码状态*/
    public static final String queryQr_code = baufydata + "/index.php/CmbcShaoma/Query/mssm_zfcx";
    /**加密查询二维码状态*/
    public static final String encipheredQueryQr_code = baufydata + "/index.php/CmbcShaoma/Query/mssm_zfcx";
    /**短信发送*/
    public static final String getCaptcha = baufydata +"/index.php/Interface/Captcha/index";
    /**无设备注册   设备连接登录*/
    public static final String nodeviceReg = baufydata +"/index.php/Interface/NodeviceReg/index";
    /**无设备绑定设备号   手机号登录*/
    public static final String deviceBind = baufydata +"/index.php/Interface/deviceBind/index";
    /**手机号绑定设备  为完善信息*/
    public static final String nodeviceLoginBind = baufydata +"/index.php/Interface/NodeviceLogin/bind";
    /**手机号换帮*/
    public static final String ChangePhone = baufydata+"/index.php/Interface/ChangePhone/index";
    /**判断手机验证码*/
    public static final String CaptchaCheck = baufydata + "/index.php/Interface/Captcha/check";
    /**使用说明*/
    public static final String Use_document = baufydata + "/Public/zgp/app/Use_document.html";
    /**交通银行信用卡申请地址*/
    public static final String BankOfCommunications = "https://creditcardapp.bankcomm.com/applynew/front/apply/new/index.html?trackCode=A123010564295&commercial_id=&telecom_id=";
    /**兴业银行信用卡申请地址*/
    public static final String SocieteGenerale = "https://ccshop.cib.com.cn:8010/application/cardapp/Fast/TwoBar/viewNew?id=b7702d61d6554ac7b6c1e824f2981c2a";
    /**微信限额*/
    //public static final String weixin = "http://220.231.200.209/Public/zgp/limit.html";//测试
    public static final String weixin = "http://posp.baifudata.com/Public/zgp/limit.html";
    /**支付宝限额*/
    //public static final String zhifubao = "http://220.231.200.209/Public/zgp/limitzfb.html";//测试
    public static final String zhifubao = "http://posp.baifudata.com/Public/zgp/limitzfb.html";
    /**扫码支付
     * @return 二维码链接
     */
    public static String sweepCodeToPay(String terminalNum) {
        String sign = Utility.MD5(terminalNum+"aaa6dd1175459fefa2c2a17810102610");
        return "http://posp.baifudata.com/index.php/CmbcShaoma/GzTrade/gzzf/terminalId/"+terminalNum+".html?sign="+sign;
//		return baufydata + "/index.php/CmbcShaoma/GzTrade/gzzf/terminalId/"+terminalNum+".html?sign="+sign;
    }
    /**首页获取图片的接口*/
    public static final String CHANGE_IMAGE = baufydata + "/index.php/Interface/ImageCopy/changeImage";
    /**奕信宝*/
    public static final String YIXINBAO = "http://yxb.jie360.com.cn/yilin/jieH5/inverList.html";//?yilinkeji&channel_code=AAZ000000009
    /**快捷支付绑卡接口*/
    public static final String QuickPayCardBind = baufydata+"/index.php/Interface/QuickPayCardBind/BindBankCard";
    /**快捷支付接口*/
    public static final String QuickPayTrade = baufydata+"/index.php/Interface/QuickPayTrade/quickPayTrade";
    /**银行卡变更接口*/
    public static final String ChangeInfo = baufydata+"/index.php/Interface/ChangeInfo/changeInfoCard";
    /**最新批次号流水号查询*/
    public static final String TransactionList =baufydata+"/index.php/Interface/TransactionList/Transaction_no.html";
//	public static final String TransactionList = "http://posp.baifudata.com/index.php/Interface/TransactionList/Transaction_no.html";
    /**快捷交易查询*///
    public static final String TradeQuery =baufydata+"/index.php/Interface/QuickPayTrade/tradeQuery";
    /**收费返现记录*/
    public static final String TradeList =baufydata+"/index.php/Interface/Countback/tradeList";
    /**用户返现当天统计时的交易总金额*/
    public static final String CountInfo =baufydata+"/index.php/Interface/Countback/CountInfo";
    //新的版本
    /**快捷支付api绑卡接口*/
    public static final String ApiBindBankCard =baufydata+"/index.php/Interface/ApiQuickPayCardBind/ApiBindBankCard";
    /**快捷支付获取短信接口*/
    public static final String QuickPaySendSms =baufydata+"/index.php/Interface/QuickPayApiTrade/quickPaySendSms";
    /**快捷支付接口*/
    public static final String QuickPayApiTrade =baufydata+"/index.php/Interface/QuickPayApiTrade/quickPayTrade";
    /**交易查询接口*/
    public static final String radeQuery =baufydata+"/index.php/Interface/QuickPayApiTrade/tradeQuery";
    /**删除绑定链接接口*/
    public static final String DeleteBindLink =baufydata+"/index.php/Interface/ApiQuickPayCardBind/delHtml";
    /**多商户查询接口*/
    public static final String MerchantsQuery =baufydata+"/index.php/CmbcShaoma/MultiMcht/multiMchtQuery";
    /**多商户交易接口*/
    public static final String MerchantsTrade =baufydata+"/index.php/CmbcShaoma/Trade/smzf_multi";
}
