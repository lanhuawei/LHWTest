package baifu.www.lhwtest;

import android.app.Application;
import android.graphics.Bitmap;

import org.xutils.x;

import java.math.BigDecimal;
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
    private String SingleCost = null;//单笔费用
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
        /**
         * 初始化xUtils
         */
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);// 是否输出debug日志, 开启debug会影响性能.
    }

    public int getUpDate() {
        return upDate;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getSN() {
        return SN;
    }

    public String getNoDeviceSN() {
        return noDeviceSN;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getDate() {
        return date;
    }

    public String getICData() {
        return ICData;
    }

    public String getICCardKey() {
        return ICCardKey;
    }

    public byte[] getFirstTrack() {
        return firstTrack;
    }

    public byte[] getSecondTrack() {
        return secondTrack;
    }

    public byte[] getThirdTrack() {
        return thirdTrack;
    }

    public byte[] getEncryptResult() {
        return encryptResult;
    }

    public String getPinKey() {
        return pinKey;
    }

    public String getPassword() {
        return password;
    }

    public String getTMK() {
        return TMK;
    }

    public String getPIK() {
        return PIK;
    }

    public String getTDK() {
        return TDK;
    }

    public String getCommand() {
        return command;
    }

    public String getSingleCost() {
        return SingleCost;
    }

    public String getRate() {
        return rate;
    }

    public String getAccount() {
        return account;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public String getMcht_name() {
        return mcht_name;
    }

    public String getMcht_shortname() {
        return mcht_shortname;
    }

    public String getCom_phone() {
        return com_phone;
    }

    public String getIf_pband() {
        return if_pband;
    }

    public String getLogin_phone() {
        return login_phone;
    }

    public String getIsPre() {
        return isPre;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<BluetoothDeviceContext> getDiscoveredDevices() {
        return discoveredDevices;
    }

    public boolean isDeviceConnState() {
        return deviceConnState;
    }

    public boolean isPhoneDeviceConnState() {
        return phoneDeviceConnState;
    }

    public HashMap<String, Bitmap> getHmBitmap() {
        return hmBitmap;
    }

    public List<HomePage.ResultBean> getList_resule() {
        return list_resule;
    }

    public List<HomePage.ResultBean> getList_lunbo() {
        return list_lunbo;
    }

    public List<BankCardInfo> getBankCardInfo() {
        return bankCardInfo;
    }

    public List<CashBackBean.ResultBean> getList_cashback() {
        return list_cashback;
    }

    public void setUpDate(int upDate) {
        this.upDate = upDate;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public void setNoDeviceSN(String noDeviceSN) {
        this.noDeviceSN = noDeviceSN;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setICData(String ICData) {
        this.ICData = ICData;
    }

    public void setICCardKey(String ICCardKey) {
        this.ICCardKey = ICCardKey;
    }

    public void setFirstTrack(byte[] firstTrack) {
        this.firstTrack = firstTrack;
    }

    public void setSecondTrack(byte[] secondTrack) {
        this.secondTrack = secondTrack;
    }

    public void setThirdTrack(byte[] thirdTrack) {
        this.thirdTrack = thirdTrack;
    }

    public void setEncryptResult(byte[] encryptResult) {
        this.encryptResult = encryptResult;
    }

    public void setPinKey(String pinKey) {
        this.pinKey = pinKey;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTMK(String TMK) {
        System.out.println("输入tmk");
        this.TMK = TMK;
    }

    public void setPIK(String PIK) {
        System.out.println(PIK);
        this.PIK = PIK;
    }

    public void setTDK(String TDK) {
        this.TDK = TDK;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setSingleCost(String singleCost) {
        SingleCost = singleCost;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public void setMcht_name(String mcht_name) {
        this.mcht_name = mcht_name;
    }

    public void setMcht_shortname(String mcht_shortname) {
        this.mcht_shortname = mcht_shortname;
    }

    public void setCom_phone(String com_phone) {
        this.com_phone = com_phone;
    }

    public void setIf_pband(String if_pband) {
        this.if_pband = if_pband;
    }

    public void setLogin_phone(String login_phone) {
        this.login_phone = login_phone;
    }

    public void setIsPre(String isPre) {
        this.isPre = isPre;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setDiscoveredDevices(List<BluetoothDeviceContext> discoveredDevices) {
        this.discoveredDevices = discoveredDevices;
    }

    public void setDeviceConnState(boolean deviceConnState) {
        this.deviceConnState = deviceConnState;
    }

    public void setPhoneDeviceConnState(boolean phoneDeviceConnState) {
        this.phoneDeviceConnState = phoneDeviceConnState;
    }

    public void setHmBitmap(HashMap<String, Bitmap> hmBitmap) {
        this.hmBitmap = hmBitmap;
    }

    public void setList_resule(List<HomePage.ResultBean> list_resule) {
        this.list_resule = list_resule;
    }

    public void setList_lunbo(List<HomePage.ResultBean> list_lunbo) {
        this.list_lunbo = list_lunbo;
    }

    public void setBankCardInfo(List<BankCardInfo> bankCardInfo) {
        this.bankCardInfo = bankCardInfo;
    }

    public void setList_cashback(List<CashBackBean.ResultBean> list_cashback) {
        this.list_cashback = list_cashback;
    }

    public static void setInstance(BaseApplication instance) {
        BaseApplication.instance = instance;
    }
}
