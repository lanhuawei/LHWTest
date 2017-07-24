package baifu.www.lhwtest;

import android.app.Application;
import android.icu.math.BigDecimal;

/**
 * Created by Administrator on 2017/7/24.
 * Ivan Lan
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




}
