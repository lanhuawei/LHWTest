package baifu.www.lhwtest;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.newland.me.ME3xDriver;
import com.newland.mtype.ConnectionCloseEvent;
import com.newland.mtype.Device;
import com.newland.mtype.DeviceDriver;
import com.newland.mtype.DeviceOutofLineException;
import com.newland.mtype.DeviceRTException;
import com.newland.mtype.ModuleType;
import com.newland.mtype.event.DeviceEventListener;
import com.newland.mtype.module.common.cardreader.CardReader;
import com.newland.mtype.module.common.cardreader.OpenCardReaderEvent;
import com.newland.mtype.module.common.emv.EmvControllerListener;
import com.newland.mtype.module.common.emv.EmvModule;
import com.newland.mtype.module.common.emv.EmvTransController;
import com.newland.mtype.module.common.emv.EmvTransInfo;
import com.newland.mtype.module.common.pin.EncryptType;
import com.newland.mtype.module.common.pin.KekUsingType;
import com.newland.mtype.module.common.pin.PinInput;
import com.newland.mtype.module.common.pin.WorkingKey;
import com.newland.mtype.module.common.pin.WorkingKeyType;
import com.newland.mtype.module.common.swiper.SwipResult;
import com.newland.mtype.module.common.swiper.SwipResultType;
import com.newland.mtype.module.common.swiper.Swiper;
import com.newland.mtype.module.common.swiper.SwiperReadModel;
import com.newland.mtype.util.ISOUtils;
import com.newland.mtypex.bluetooth.BlueToothV100ConnParams;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import baifu.www.lhwtest.encryption.Const;
import baifu.www.lhwtest.entity.BluetoothDeviceContext;
import baifu.www.lhwtest.utils.ByteUtils;

/**
 * Created by Ivan.L on 2017/7/26.
 * 用户蓝牙连接设备后，刷卡操作，读取
 */

public class MyService extends Service {

    private static String TAG = "MyService";
    private myReceiver myreceiver;//广播    接收消息
    private boolean bluetoothSearchState = true;//搜索蓝牙状态
    private List<BluetoothDeviceContext> discoveredDevices =new ArrayList<BluetoothDeviceContext>();//存放广播名称地址
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//蓝牙工具
    private DeviceDriver driverDriver = (DeviceDriver) new ME3xDriver();
    private static Device device = null;//设备device
    private static PinInput pinInput;
    private String deviceToConnect;
    private int bluetoothDevicesID;//设备id
    private BlueToothV100ConnParams params;
    private CardReader cardReader;
    private String TMK = null;
    private String PIK = null;

    //	private String TDK = null;
    public static Handler handler;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        myreceiver = new myReceiver();
        super.onCreate();
        startDiscovery();//搜搜蓝牙
        myHandler();
    }

    /**
     * @since ver1.0l
     * 之前状态是连接，后断开连接
     */
    private void destroyDeviceController() {
        if (null != device) {
            device.destroy();//摧毁设备
            bluetoothSearchState = true;//允许蓝牙送所
            BaseApplication.getInstance().setDeviceConnState(false);//保存设备断开状态
            BaseApplication.getInstance().setPhoneDeviceConnState(false);//保存断开状态 //电话登录
            device = null;
        }
    }

    /**
     * 设备断开 触发该事件
     */
    private class DeviceCloseListener implements DeviceEventListener<ConnectionCloseEvent> {
        @Override
        public void onEvent(ConnectionCloseEvent connectionCloseEvent, Handler handler) {
            if (connectionCloseEvent.isSuccess()) {//自动断开设备
                informsDeviceDisconnect();//通知所有Activity断开连接
            }else if (connectionCloseEvent.isFailed()) {//设备异常断开
                informsDeviceDisconnect();//通知所有Activity断开连接
            }
        }

        @Override
        public Handler getUIHandler() {
            return null;
        }
    }

    /**
     * 通知所用activity设备断开链接
     */
     private void informsDeviceDisconnect() {
        bluetoothSearchState = true;
        if(BaseApplication.getInstance().isDeviceConnState()){//判断设备连接  还是手机号连接
            sendCmd("initiativeDisconnect", "org.great.activity.All");//通过广播通知所用activity
            BaseApplication.getInstance().setDeviceConnState(false);//设备连接状态
        }else{
            sendCmd("initiativeDisconnect", "org.great.activity.DeviceMobilePhoneBinding");
            BaseApplication.getInstance().setDeviceConnState(false);//设备连接状态
            BaseApplication.getInstance().setPhoneDeviceConnState(false);//设备连接状态
        }
        device = null;
    }


    /**
     * 选择要连接的设备
     */
    private void selectBtAddrToInit() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deviceToConnect = discoveredDevices.get(bluetoothDevicesID).address;//通过id获取设备地址
                try {
                    params = new BlueToothV100ConnParams(deviceToConnect);
                    device = driverDriver.connect(MyService.this, params,new DeviceCloseListener());//链接
                    pinInput = (PinInput) device.getStandardModule(ModuleType.COMMON_PININPUT);
                    BaseApplication.getInstance().setSN(device.getDeviceInfo().getSN());//获取sn
                    String battery = device.getBatteryInfo().getElectricBattery();//获取电量信息
                    if(Integer.valueOf(battery)<20){//判断设备电量是否少于20%
                        sendCmd("PowerLow","org.great.activity.LoginActivity");//通知电量过低
                        sendCmd("PowerLow","org.great.activity.DeviceMobilePhoneBinding");
                    }
                    if (device.isAlive()) {//连接成功
                        sendCmd("connectSucceed", "org.great.activity.LoginActivity");//通知连接成功
                        sendCmd("connectSucceed", "org.great.activity.DeviceMobilePhoneBinding");
                        RecordTime();//启动计时
                    }
                }catch(DeviceOutofLineException e){//连接异常
                    sendCmd("connectException", "org.great.activity.LoginActivity");
                    sendCmd("connectException","org.great.activity.DeviceMobilePhoneBinding");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 调用设备信息 防止连接超时 设备自动断开连接.若两分钟未操作 设备自动断开
     */
    private void RecordTime() {
        new Thread(new Runnable(){
            public void run() {
                while(device!=null){
                    if(device.isAlive()){//设备是否存活
                        try{
                            device.getDeviceInfo();//调用设备信息 防止断开连接
                        }catch(DeviceRTException d){
                            Log.i("MyService-158", d.getMessage());
                        }catch(NullPointerException d){
//							Log.i(TAG, "空");
                        }finally{
                            try {
                                Thread.sleep(1000*20);//每二十秒调用一次
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 刷卡操作
     */
    private void SwipingCard() {
        new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try {
                    //请刷卡...
                    cardReader = (CardReader) device//调取刷卡
                            .getStandardModule(ModuleType.COMMON_CARDREADER);
                    cardReader.openCardReader(
                            // 需要打开的读卡器类型
                            new ModuleType[] {ModuleType.COMMON_SWIPER,
                                    ModuleType.COMMON_ICCARD }, 30,
                            TimeUnit.SECONDS, new OpenCardReaderListener());
                }catch (Exception e) {
                    Log.i(TAG,"刷卡失败！" + e.getMessage());
                    return;
                }
            }
        }).start();
    }

    /**
     * 对刷卡进行监听操作
     */
    private class OpenCardReaderListener implements
            DeviceEventListener<OpenCardReaderEvent> {
        @Override
        public Handler getUIHandler() {
            return null;// ...根据需要决定是否要传递handler
        }
        @SuppressLint("ShowToast")
        @Override
        public void onEvent(OpenCardReaderEvent event, Handler handler) {
            //刷卡动作...
            try {
                if (event.isSuccess()) {
                    // ..处理刷卡成功事件
                    ModuleType[] openedModuleTypes = event.getOpenedCardReaders();
                    if (openedModuleTypes != null&& openedModuleTypes.length > 0) {
                        for (ModuleType type : openedModuleTypes) {
                            if (ModuleType.COMMON_SWIPER == type) {//磁条卡
                                Swiper swiper = (Swiper) device
                                        .getStandardModule(ModuleType.COMMON_SWIPER);// 获得磁卡读卡器模块接口
                                // 调用一个获得明文磁道的交易过程,这里同时读取一二三磁道
                                SwipResult swipResult = swiper
                                        .readPlainResult(new SwiperReadModel[] {
                                                SwiperReadModel.READ_FIRST_TRACK,
                                                SwiperReadModel.READ_SECOND_TRACK,
                                                SwiperReadModel.READ_THIRD_TRACK });
                                if (swipResult.getRsltType() != SwipResultType.SUCCESS) {
                                    //刷卡失败
                                    Toast.makeText(MyService.this,"磁道错误，请重新刷卡",Toast.LENGTH_SHORT).show();
                                    // ...刷卡结果非正确，很可能是解码失败，类似磁道错误
                                } else {
                                    byte[] firstTrack = swipResult.getFirstTrackData(); // 获取 1 磁道信息
                                    byte[] secondTrack = swipResult.getSecondTrackData(); // 获取 2 磁道信息
                                    byte[] thirdTrack = swipResult.getThirdTrackData(); // 获取 3 磁道信息
                                    if(null==secondTrack){// 2 磁道为空  重新调取刷卡事件
                                        SwipingCard();
                                    }else{
                                        String a = ISOUtils.dumpString(secondTrack);
                                        int i = a.indexOf("=");
                                        String b = a.substring(i+5,i+6);//取等号后5位 若是2或6 该卡为ic卡
                                        if(b.equals("2")||b.equals("6")){
                                            //通知该卡为ic卡 请插卡消费
                                            sendCmd("noDowngradeTrading","org.great.activity.PaymentActivity");
                                            SwipingCard();
                                        }else{
                                            String k = swipResult.getAccount().getAcctNo();
                                            BaseApplication.getInstance().setAccountNumber(k);
                                            BaseApplication.getInstance().setFirstTrack(firstTrack);
                                            BaseApplication.getInstance().setSecondTrack(secondTrack);
                                            BaseApplication.getInstance().setThirdTrack(thirdTrack);
                                            //通知刷卡成功
                                            sendCmd("CreditCardSuccessfully","org.great.activity.PaymentActivity");
                                        }
                                    }
                                    // .. 将刷卡结果swipResult通知主线程
                                }
                            }if (ModuleType.COMMON_ICCARD == type) {
                                //ic卡
                                EmvModule emvModule = (EmvModule) device
                                        .getStandardModule(ModuleType.COMMON_EMV);
                                EmvTransController controller = emvModule
                                        .getEmvTransController(new SimpleEmvControllerListener());
                                controller.startEmv(BaseApplication.getInstance().getTransactionAmount(),
                                        new BigDecimal("0"), true);
//								mTime = System.currentTimeMillis();
                                //刷卡成功
                            }
                        }
                    }
                } else if (event.isFailed()) {
                    sendCmd("DeviceRTException", "org.great.activity.PaymentActivity");
                } else if (event.isUserCanceled()) {
                    // ..处理刷卡用户撤消事件
                }
            } finally {
                if(device.isAlive()){
                    cardReader.closeCardReader(); // 关闭读卡器，该对象可以通过上下文传入，也可以通过device获取
                }
            }
        }
    }

    /**
     * IC卡刷卡异常
     */
    private void myHandler() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.arg1==0){
                    sendCmd("CreditCardSuccessfullyIC",
                            "org.great.activity.PaymentActivity");//通知ic卡刷卡成功
                }if(msg.arg1==1){
                    sendCmd("CreditCardSuccessfullyICException",
                            "org.great.activity.PaymentActivity");//刷卡异常
                }
            }
        };
    }
    /**
     * 开启ic卡pboc 交易
     */
    private static class SimpleEmvControllerListener implements
            EmvControllerListener {
        @Override
        public void onEmvFinished(boolean issuccess, EmvTransInfo context)
                throws Exception {
            // 当emv交易正常结束时发生
            // issuccess表示emv交易是否成功，emvTransInfo包含emv交易信息
            if(issuccess){
                //"交易成功");
            }else{
                //交易失败
            }
        }

        @Override
        public void onError(EmvTransController arg0, Exception arg1) {
            Log.i(TAG, "交易异常");
            // 当PBOC流程中出现异常时会回调该方法
            // 可在该方法中处理PBOC交易的异常
            Message m = new Message();
            m.arg1 = 1;
            handler.handleMessage(m);
        }

        @Override
        public void onFallback(EmvTransInfo arg0) throws Exception {
            // 当固件判定ic卡交易条件不具备时，emv流程返回一个fallback事件，此处可做降级交易处理，如磁条卡交易。
        }

        @Override
        public void onRequestOnline(EmvTransController controller,
                                    EmvTransInfo context) throws Exception {

            BaseApplication.getInstance().setAccountNumber(context.getCardNo());//卡号
            BaseApplication.getInstance().setSecondTrack(context.getTrack_2_eqv_data());// 获取 2 磁道信息
            BaseApplication.getInstance().setICCardKey(context.getCardSequenceNumber());//IC卡序列号
            BaseApplication.getInstance().setDate(context.getCardExpirationDate());//交易日期
            BaseApplication.getInstance().setICData("9f2608"+ISOUtils.hexString(context.getAppCryptogram())+"9f2701"//9f26
                    +ISOUtils.hexString(new byte[]{context.getCryptogramInformationData()})+"9f1013"//9f27
                    +ISOUtils.hexString(context.getIssuerApplicationData())+"9f3704"//9f10
                    +ISOUtils.hexString(context.getUnpredictableNumber())+"9f3602"//9f37
                    +ISOUtils.hexString(context.getAppTransactionCounter())+"9505"//9f36
                    +ISOUtils.hexString(context.getTerminalVerificationResults())+"9a03"//95
                    +context.getTransactionDate()+"9c010"//9a
                    +context.getTransactionType()+"9f0206"//9c
                    +rightFill(context.getAmountAuthorisedNumeric())+"5f2a020"//9f02
                    +context.getTransactionCurrencyCode()+"8202"//5f2a
                    +ISOUtils.hexString(context.getApplicationInterchangeProfile())+"9f1a020"//82
                    +context.getTerminalCountryCode()+"9f030600000000000"//9f1a
                    +context.getAmountOtherNumeric()+"9f3303"//9f03
                    +ISOUtils.hexString(context.getTerminal_capabilities())+"9f3403"//9f33
                    +ISOUtils.hexString(context.getCvmRslt())+"9f3501"//9f34
                    +context.getTerminalType()+"9f1e08"//9f35
                    +ISOUtils.hexString(context.getInterface_device_serial_number().getBytes())+"8408"//9f1e
                    +ISOUtils.hexString(context.getDedicatedFileName())+"9f0902"//84
                    +ISOUtils.hexString(context.getAppVersionNumberTerminal())+"9f4104"//9f09
                    +ISOUtils.hexString(context.getTransactionSequenceCounter()));//9f41
            Message m = new Message();
            m.arg1 = 0;
            handler.handleMessage(m);
        }

        @Override
        public void onRequestPinEntry(EmvTransController arg0, EmvTransInfo arg1)
                throws Exception {
            // 预留方法（目前暂未开放）
        }

        @Override
        public void onRequestSelectApplication(EmvTransController arg0,
                                               EmvTransInfo arg1) throws Exception {
            // 预留方法（目前暂未开放）
        }

        @Override
        public void onRequestTransferConfirm(EmvTransController arg0,
                                             EmvTransInfo arg1) throws Exception {
            // 预留方法（目前暂未开放）
        }
    }

    /**
     * 右补“0”
     * @param num
     * @return
     */
    private static String rightFill(String num) {
        while(num.length()<12)num="0"+num;
        return num;
    }

    /**
     * 撤消  当前操作，撤销刷卡
     */
    private void Cancel() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != device) {
                    device.reset();//撤消  当前操作
                }
            }
        }).start();
    }

    /**
     * 装秘钥
     */
    private void LoadWorkKey() {
        new Thread(new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                try {
                    TMK = BaseApplication.getInstance().getTMK();//提取主秘钥
                    PIK = BaseApplication.getInstance().getPIK();//提取pin秘钥
//					TDK = MyApplication.getInstance().getTDK();
                    // 步骤1：装载主密钥并返回校验值
                    pinInput.loadMainKey(KekUsingType.ENCRYPT_TMK, 1,ISOUtils.hex2byte(TMK));//主密钥罐装
                    pinInput.loadWorkingKey(WorkingKeyType.MAC,1,//pin秘钥罐装
                            Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX,
                            ISOUtils.hex2byte(PIK));
//					pinInput.loadWorkingKey(WorkingKeyType.MAC,1,
//							MacWKIndexConst.DEFAULT_MAC_WK_INDEX,
//							ISOUtils.hex2byte(TDK));
                    sendCmd("setWorkKeySuccessful","org.great.activity.LoginActivity");//通过广播提示装载成功
                    Log.i(TAG, "工作密钥装载成功!");
                } catch (Exception ex) {
                    sendCmd("setWorkKeyError","org.great.activity.LoginActivity");//提示秘钥罐装失败
                    Log.i(TAG,"工作密钥装载失败!");
                }
            }
        }).start();
    }

    /**
     * 密码加密
     * @param strCardNo
     * @param passWord
     */
    private static void doPinInputShower(final String strCardNo,final String passWord) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] pinBlock = ByteUtils.process(//计算PIN BLOCK
                            ByteUtils.getPinBlock(passWord),
                            ByteUtils.getPanInfo(strCardNo));
                    byte[] bKey = pinInput.encrypt(new WorkingKey(//通过pin秘钥把PIN BLOCK加密
                                    Const.PinWKIndexConst.DEFAULT_PIN_WK_INDEX),
                            EncryptType.ECB,pinBlock,
                            new byte[]{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00});
                    String strKey = "";
                    for (int i = 0; i < bKey.length; i++)
                        strKey = strKey + String.format("%02X",bKey[i]);//byte2String
                    BaseApplication.getInstance().setPinKey(strKey);//保存
                } catch (Exception e) {
                    Log.i(TAG, "pin加密失败！" + e.getMessage());
                    return;
                }
            }
        }).start();
    }

    /**
     * 搜索蓝牙
     */
    private void startDiscovery() {
        if (bluetoothAdapter.isEnabled()) {//判断蓝牙是否打开
            if (discoveredDevices != null) {
                discoveredDevices.clear();//清空discoveredDevices
                BaseApplication.getInstance().setDiscoveredDevices(discoveredDevices);//保存至全局变量
            }
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.cancelDiscovery();
            }
            bluetoothSearch();
        } else {
            //通知用户打开蓝牙
            sendCmd("showDialog", "org.great.activity.LoginActivity");
        }
    }

    /**
     * 蓝牙搜索
     */
    private void bluetoothSearch() {
        new Thread(new Runnable(){
            public void run() {
                while(bluetoothSearchState){
                    bluetoothAdapter.startDiscovery();//蓝牙搜索
                    try {
                        Thread.sleep(5000);//睡眠5秒 //每隔5秒搜索一次
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bluetoothAdapter.cancelDiscovery();//关闭搜索
                }
            }
        }).start();
    }


    /**
     * 接收广播返回的通知,刷卡操作
     */
    private class myReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");
            if ("connect".equals(info)) {// 连接设备
                bluetoothSearchState = false;
                bluetoothDevicesID = intent.getIntExtra("num", -1);
                destroyDeviceController();//若之前是连接状态断开连接
                selectBtAddrToInit();
            }else if ("swipingCard".equals(info)) {// 刷卡动作反馈
                SwipingCard();
            }else if ("break".equals(info)) {// 撤销刷卡
                Cancel();
            }else if ("setWorkKey".equals(info)){//装载秘钥
                LoadWorkKey();
            }else if ("DeviceDisconnect".equals(info)){// 断开设备
                destroyDeviceController();
            }else if("EnterPassword".equals(info)){//获取用户输入的密码
                //计算出pinKey
                doPinInputShower(BaseApplication.getInstance().getAccountNumber(),
                        BaseApplication.getInstance().getPassword());
            }else if("DeviceState".equals(info)){//判断设备是否连接
                if(null == device){
                    BaseApplication.getInstance().setDeviceConnState(false);//设备连接状态
                }else{
                    BaseApplication.getInstance().setDeviceConnState(true);//设备连接状态
                }
                sendCmd("DeviceState","org.great.activity.LoginActivity");
            }else if("SearchBluetooth".equals(info)){//搜索蓝牙
                startDiscovery();
            }else if("closeBluetooth".equals(info)){
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.cancelDiscovery();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyDeviceController();
        unregisterReceiver(discoveryReciever);
        unregisterReceiver(myreceiver);//移除广播
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.great.util.MyService");
        registerReceiver(myreceiver, filter);//注册广播
        IntentFilter filterBluetooth = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryReciever, filterBluetooth);
        return super.onStartCommand(intent, flags, startId);
    }

    //    广播接收器
    private BroadcastReceiver discoveryReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ifAddressExist(device.getAddress())) {
                    return;
                }
                BluetoothDeviceContext btContext = new BluetoothDeviceContext(
                        device.getName() == null ? device.getAddress()
                                : device.getName(), device.getAddress());
                discoveredDevices.add(btContext);//搜索到的设备加入道discoveredDevices
                BaseApplication.getInstance().setDiscoveredDevices(discoveredDevices);
                sendCmd("cancelDiscovery", "org.great.activity.LoginActivity");
                sendCmd("cancelDiscovery", "org.great.activity.DeviceMobilePhoneBinding");
            }
        }
    };

    //判断地址是否存在
    private boolean ifAddressExist(String addr) {
        for (BluetoothDeviceContext devcie : discoveredDevices) {
            if (addr.equals(devcie.address))
                return true;
        }
        return false;
    }

    /**
     * 通过广播发送通知给各个activity
     * @param value
     * @param action
     */
    public void sendCmd(String value, String action) {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction(action);
        serviceIntent.putExtra("value", value);
        sendBroadcast(serviceIntent);
    }
}
