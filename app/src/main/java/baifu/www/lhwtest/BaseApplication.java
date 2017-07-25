package baifu.www.lhwtest;

import android.app.Application;
import android.graphics.Bitmap;
import android.icu.math.BigDecimal;

import java.util.HashMap;
import java.util.List;

import baifu.www.lhwtest.entity.BankCardInfo;
import baifu.www.lhwtest.entity.BluetoothDeviceContext;
import baifu.www.lhwtest.entity.CashBackBean;
import baifu.www.lhwtest.entity.HomePage;

/**
 * Created by Administrator on 2017/7/24.
 *
 */

public class BaseApplication extends Application {
    private int upDate = 0;//检测更新的信息
    private BigDecimal transactionAmount = null;//交易金额
    private String deviceName = null;//设备名称
    private String SN = null;//连接设备时所用的sn码
    private String noDeviceSN = null;//无设备连接时设备SN码  用来辨别
    private String accountNumber = null;//交易卡号
    private String date = null;//交易日期
    private String ICData = null;//IC卡信息
    private String ICCardKey = null;//IC卡序列号
    private byte[] firstTrack = null;//一磁道
    private byte[] secondTrack = null;//二磁道
    private byte[] thirdTrack = null;//三磁道
    private byte[] encryptResult = null;//加密结果
    private String pinKey = null;//pin八进制
    private String password = null;//卡号密码
    private String TMK = null;//终端主秘钥
    private String PIK = null;
    private String TDK = null;
    private String command = null;//口令
    private String SingleCoast = null;//单笔费用
    private String rate = null;// 扣率
    private String account = null;// 账号

    private String terminalId = null;// 终端号
    private String mcht_name = null;// 商户名称
    private String mcht_shortname = null;// 商户简称
    private String com_phone = null;// 注册时手机号
    private String if_pband;// 1 需要绑定 0不需要绑定
    private String login_phone = null;// 绑定成功的电话

    private String isPre = null;// 1提示限额
    private String prompt = null;// 预审批 提示信息

    private List<BluetoothDeviceContext> discoveredDevices = null;
    private boolean deviceConnState = false;// 设备连接状态
    private boolean phoneDeviceConnState = false;// 判断手机号登录时 设备连接状态

    private HashMap<String, Bitmap> hmBitmap = null;// 主页面图片
    private List<HomePage.ResultBean> list_resule = null;// 主界面list
    private List<HomePage.ResultBean> list_lunbo = null;// 主界面轮播图list
    private List<BankCardInfo> bankCardInfo = null;// 银行卡列表
    private List<CashBackBean.ResultBean> list_cashback;// 返现列表

    public static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
