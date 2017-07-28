package baifu.www.lhwtest.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.encryption.CRC1021;
import baifu.www.lhwtest.entity.BankCard;
import baifu.www.lhwtest.entity.BluetoothDeviceContext;
import baifu.www.lhwtest.entity.History;
import baifu.www.lhwtest.entity.HomePage;
import baifu.www.lhwtest.internet.ConnServer;
import baifu.www.lhwtest.internet.ConnServerPost;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.internet.SocketServer;
import baifu.www.lhwtest.service.SaveHomePageService;
import baifu.www.lhwtest.utils.ByteUtils;
import baifu.www.lhwtest.utils.CountdownAsyncTask;
import baifu.www.lhwtest.utils.Utility;
import baifu.www.lhwtest.utils.showDialogUtil;

/**
 * 登录界面
 */


public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = "LoginActivity";
    private Context context = LoginActivity.this;
    private Boolean activityIsBeing = true;// 检测activity是否在栈顶,
    private Handler myHandler;
    private String SN = null;
    private EditText accounts_et;
    private CheckBox auto_check_in_cb;
    private List<BluetoothDeviceContext> discoveredDevices = new ArrayList<BluetoothDeviceContext>();
    private ArrayAdapter<String> nameAdapter;
    private String deviceName = null;// 设备名称
    private boolean isVerify;// 手机是否通过验证
    private Window window;
    private View view;
    private Dialog dialog;
    private Button cancel_btn;
    private ListView bluetooth_name_lv;
    private TextView connect_tv;
    private MyBroadcast myService;
    private SharedPreferences sp;
    protected Map<String, Object> mResult;
    private boolean deviceState = false;// 连接状态
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private TextView codeKey;// 获取验证码按钮
    private EditText password_et;
    private ImageView accounts_clear, codeKey_clear;// 清除

    private int update = 0;// 获取版本号状态0，登录 1，新版本，2检测中 3，重新运行

    /**
     * 判断设备的连接状态
     */
    private void DeviceConnState() {
        if (BaseApplication.getInstance().isDeviceConnState()) {
            deviceName = BaseApplication.getInstance().getDeviceName();
            if (deviceName == null) {
                sendCmd("DeviceDisconnect", 0);
            } else {
                connect_tv.setText(deviceName);
            }
        }
    }
    /**
     * 获取版本信息
     */
    private void getVersionCode() {
        String latestVersion = sp.getString("judgeUpdate", "");
        boolean judgeFirst = sp.getBoolean("judgeUpdateFirst", false);
        if (judgeFirst) {
            sp.edit().putBoolean("judgeUpdateFirst", false).commit();
            if (!latestVersion.equals(judgeUpdate())) {
                showDialogUtil.selectDialog("检测到新版本，是否立即更新", context, myHandler,
                        "update", "立即更新", "稍后更新");
                update = 1;
            } else {
                boolean judgeAndroidMessage = sp.getBoolean("judgeAndroidMessage", false);
                if (judgeAndroidMessage) {
                    sp.edit().putBoolean("judgeAndroidMessage", false).commit();
                    boolean showMessage = sp.getBoolean("showMessage", false);
                    String downShowMessageNum = sp.getString("downShowMessageNum", "");// 网络下载次数
                    String showMessageNum = sp.getString("showMessageNum", "");// 获取本地次数
                    if (!downShowMessageNum.equals(showMessageNum)) {
                        if (showMessage) {
                            sp.edit().putString("showMessageNum", downShowMessageNum).commit();// 如果显示旧存在本地
                            String message = sp.getString("androidMessage", "");
                            showDialogUtil.showDialog(message, context);
                        }
                    }
                }
            }
        }
    }
    /**
     * 判断更新,获取版本信息中用到
     * @return
     */
    private String judgeUpdate() {
        int versionCode = 0;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return String.valueOf(versionCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        initView();//初始化各项控件
        myListener();//判断连接并签到checkBox是否选择上
//        判断各注册返回数据
        Handler();
//        注册广播
        onBroadcast();
//        设备连接状态
        DeviceConnState();


    }
    @Override
    protected int getContentViewId() {
        return R.layout.activity_login_activity;
    }
    /**
     * 初始化布局控件
     */
    private void initView() {
        sp = getSharedPreferences("localData", Activity.MODE_PRIVATE);
        accounts_et = (EditText) findViewById(R.id.accounts_et);// 账号edittext
        accounts_et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });// 长度11
        accounts_et.addTextChangedListener(new TextChanged(accounts_et));// 输入监听
        password_et = (EditText) findViewById(R.id.password_et);// 验证码
        password_et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });// 长度4
        password_et.addTextChangedListener(new TextChanged(password_et));// 输入监听
        codeKey = (TextView) findViewById(R.id.get_codeKey_login_tv);// 获取验证码按钮

        accounts_clear = (ImageView) findViewById(R.id.accounts_clear_iv);// 清除账号
        codeKey_clear = (ImageView) findViewById(R.id.codeKey_clear_iv);// 清除验证码

        auto_check_in_cb = (CheckBox) findViewById(R.id.auto_check_in_cb);
        auto_check_in_cb.setChecked(sp.getBoolean("checkBox", true));
        connect_tv = (TextView) findViewById(R.id.connect_tv);
        view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        bluetooth_name_lv = (ListView) view.findViewById(R.id.bluetooth_name_lv);
        dialog = new Dialog(this, R.style.selectorDialog);
        cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        connect_tv.setOnClickListener(this);
        codeKey.setOnClickListener(this);

        accounts_clear.setOnClickListener(this);
        codeKey_clear.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        监听网络变化
        myNetBroadReceiver = new MyNetBroadReceiver();
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myNetBroadReceiver, mFilter);

        activityIsBeing = true;
        String userPhone = sp.getString("userPhone", null);
        isVerify = sp.getBoolean("isVerify", false);
        if (userPhone != null) {
            accounts_et.setText(userPhone);
//            获取版本信息
            getVersionCode();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != myNetBroadReceiver) {
            unregisterReceiver(myNetBroadReceiver);
            Intent i = new Intent(LoginActivity.this, SaveHomePageService.class);
            stopService(i);
        }
        activityIsBeing = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accounts_clear_iv:
                accounts_et.setText("");
                accounts_clear.setVisibility(View.GONE);
                break;
            case R.id.codeKey_clear_iv:
                password_et.setText("");
                codeKey_clear.setVisibility(View.GONE);
                break;
            case R.id.connect_tv:// 设备
                connect();
                break;
            case R.id.get_codeKey_login_tv:// 获取验证码
                getCodeKeyLogin();
                break;
            default:
                break;
        }
    }

    /**
     * 选择设备
     */
    private void connect() {
        try {
//            Camera c = Camera.open(0);// 检测是否打开相机权限 //6.0后不可用
//            c.release();
            if (update == 0) {
                discoveredDevices = BaseApplication.getInstance().getDiscoveredDevices();
                if (bluetoothAdapter.isEnabled()) {// 判断蓝牙是否打开
                    if (null == discoveredDevices)
                        discoveredDevices = new ArrayList<BluetoothDeviceContext>();
                    selectBtnAddToInit();
                } else {
                    showDialogUtil.selectDialog("请为钱宝打开蓝牙", context, myHandler, "openBlueTooth", "设置", "好");
                }
            } else if (update == 1) {
                showDialogUtil.selectDialog("请先更新版本", context, myHandler, "update", "立即更新", "稍后更新");
            } else if (update == 2) {
                showDialogUtil.showDialog("正在检测版本信息", context);
            } else if (update == 3) {
                showDialogUtil.showDialog("请重新运行程序", context);
            }
        } catch (NullPointerException n) {
            showDialogUtil.showDialog("相机未能开启，注册和解冻需要使用相机功能，请检查相机权限是否开启！", context);
        } catch (RuntimeException r) {
            if ("Fail to connect to camera service".equals(r.getMessage())) {
                showDialogUtil.showDialog("相机权限未开启，注册和解冻需要使用相机，请手动开启！", context);
            }
            r.printStackTrace();
        }
    }

    //    点击登录按钮
    public void loginBtnOnClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                if (BaseApplication.getInstance().isDeviceConnState()) {
                    sendCmd("DeviceState", -1);// 发送判断设备是否连接
                } else {
                    if (update == 0) {
                        if (isEmpty()) {
                            showDialogUtil.startProgressDialog("", context);
                            judgeAccountLogin(0);// 登陆
                        } else {
                            if (TextUtils.isEmpty(password_et.getText())) {
                                showDialogUtil.showDialog("请输入验证码", context);
                            } else {
                                judgeAccountLogin(1);// 登陆
                            }
                        }
                    } else if (update == 1) {
                        showDialogUtil.selectDialog("请先更新版本", context, myHandler, "update", "立即更新", "稍后更新");
                    } else if (update == 2) {
                        showDialogUtil.showDialog("正在检测版本信息", context);
                    } else if (update == 3) {
                        showDialogUtil.showDialog("请重新运行程序", context);
                    }
                }
                break;
        }
    }
    /**
     * 广播接收器
     *
     * @return
     */
    public class MyBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");// 获取返回的数据
            if ("verificationCode".equals(info)) {// 检测是否最新版本
                showDialogUtil.stopProgressDialog();
                sp.edit().putString("username", accounts_et.getText().toString()).commit();
                Intent intent1 = new Intent(context, HomeActivity.class);
                startActivity(intent1);
                finish();
            } else if ("verificationSend".equals(info)) {// 通知倒计时
                CountdownAsyncTask countdownAsyncTask = new CountdownAsyncTask(codeKey, context);
                countdownAsyncTask.execute(1000);
            } else if ("showDialog".equals(info)) {// 弹窗提示打开蓝牙
                // showUtil.selectDialog("请为钱宝打开蓝牙",
                // context,myHandler,"openBlueTooth","设置", "好");
            } else if ("cancelDiscovery".equals(info) && activityIsBeing) {// 返回搜索到得蓝牙设备信息
                if (dialog.isShowing()) {// 若蓝牙设备列表打开状态
                    dialog.dismiss();
                    discoveredDevices = BaseApplication.getInstance().getDiscoveredDevices();
                    selectBtnAddToInit();// 添加设备名到listview
                }
            } else if ("connectSucceed".equals(info) && activityIsBeing) {//连接成功
                connect_tv.setText(deviceName);
                accounts_et.setEnabled(false);
                accounts_clear.setVisibility(View.GONE);
                BaseApplication.getInstance().setDeviceConnState(true);// 设备连接成功，设置设备连接状态
                if (auto_check_in_cb.isChecked()) {
                    sendCmd("DeviceState", -1);
                } else {
                    showDialogUtil.stopProgressDialog();
                }
            } else if ("exceptionDisconnect".equals(info) || "initiativeDisconnect".equals(info)) {
//                设备断开
                connect_tv.setText("点击连接");
                accounts_et.setEnabled(true);
            } else if ("DeviceState".equals(info) && activityIsBeing) {// 设备是否连接
                deviceState = BaseApplication.getInstance().isDeviceConnState();
                if (deviceState) {
                    showDialogUtil.changeProgressDialogMessage("检查注册信息");
                    showDialogUtil.startProgressDialog("检查注册信息", context);
//                    判断是否注册
                    isRegister(BaseApplication.getInstance().getSN());
                } else {
                    showDialogUtil.startProgressDialog("", context);
                    if (isEmpty()) {// 判断手机号是否为空 是否需要验证码
                        Intent intent1 = new Intent(context, HomeActivity.class);
                        startActivity(intent1);
                    } else {
                        if (TextUtils.isEmpty(password_et.getText())) {
                            showDialogUtil.showDialog("请输入验证码", context);
                        } else {
                            // 1需要进行验证码判断
                            judgeAccountLogin(1);
                        }
                    }
                    // 调取帐号密码接口
                }
            } else if ("PowerLow".equals(info) && activityIsBeing) {
                Toast.makeText(context,"电量过低，请及时充电",Toast.LENGTH_SHORT).show();
            } else if ("connectException".equals(info) && activityIsBeing) {// 设备连接异常
                connectException();
            } else if ("setWorkKeyError".equals(info) && activityIsBeing) {// 密钥灌装失败
                showDialogUtil.showDialog("密钥灌装失败！", context);
            } else if ("setWorkKeySuccessful".equals(info) && activityIsBeing) {// 密钥灌装成功
//                签到跳转界面
                intentActivity();
            }
        }
    }

    private ConnectivityManager mConnectivityManager;
    private NetworkInfo netInfo;
    private MyNetBroadReceiver myNetBroadReceiver = null;

    //    监听网络状态变化的广播接收器
    public class MyNetBroadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

                mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    Log.i("tag", "连接网络");
                    Intent i = new Intent(LoginActivity.this, SaveHomePageService.class);
                    startService(i);
                } else {
                    // 网络断开
                    Log.i("tag", "断开网络");
                }
            }
        }
    }

    /**
     * 添加设备名到listView
     * 添加设备名到蓝牙显示设备的listView
     */
    private void selectBtnAddToInit() {
        int i = 0;
        String[] bluetoothNames = new String[discoveredDevices.size()];
        for (BluetoothDeviceContext deviceContext : discoveredDevices) {
            bluetoothNames[i++] = deviceContext.name;
        }
        bluetoothListView();
        nameAdapter = new ArrayAdapter<String>(this, R.layout.list_view_item, bluetoothNames);
        bluetooth_name_lv.setAdapter(nameAdapter);
        bluetooth_name_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
                dialog.cancel();
                showDialogUtil.startProgressDialog("  初始化设备 ", context);
//                连接设备
                connectingDevice(position);
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    /**
     * 显示蓝牙设备的listView
     */
    private void bluetoothListView() {
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        window = dialog.getWindow();
        WindowManager.LayoutParams w1 = window.getAttributes();
        w1.x = 0;
        //        依赖于手机系统，获取到的是系统的屏幕信息
        w1.y = getResources().getDisplayMetrics().heightPixels;
        w1.width = ViewGroup.LayoutParams.MATCH_PARENT;
        w1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(w1);
        dialog.setCanceledOnTouchOutside(true);
    }

    //    连接设备
    private void connectingDevice(final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deviceName = discoveredDevices.get(num).name;// 获取设备名称
                BaseApplication.getInstance().setDeviceName(deviceName);// 保存设备名称
                discoveredDevices = null;// 清空列表
                // 发送通知与设备连接，发送消息给service
                sendCmd("connect", num);
            }
        }).start();
    }

    //    连接后自动签到
    private void myListener() {
        auto_check_in_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sp.edit().putBoolean("checkBox", true).commit();
                } else {
                    sp.edit().putBoolean("checkBox", false).commit();
                }
            }
        });
    }

    /**
     * 发送消息给service
     * @param value
     * @param i
     */
    public void sendCmd(final String value, final int i) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent serviceIntent = new Intent();
                serviceIntent.setAction("baifu.www.lhwtest.MyService");
                serviceIntent.putExtra("value", value);
                serviceIntent.putExtra("num", i);
                context.sendBroadcast(serviceIntent);
            }
        }).start();
    }

    /**
     * 发送判断是否注册
     * @param sn
     */
    private void isRegister(String sn) {
        String url = IpAddress.isRegister + "deviceUnique" + sn;
//        连接到服务器，使用服务器连接工具
        ConnServer register = new ConnServer(url, myHandler, "register");
        new Thread(register).start();
    }

    /**
     * 判断是否为空
     * @return
     */
    private boolean isEmpty() {
        if (TextUtils.isEmpty(accounts_et.getText())) {
            showDialogUtil.showDialog("请连接设备，或输入手机号", context);
            return false;
        } else {
            String userPhone = sp.getString("userPhone", null);
            if (userPhone != null && accounts_et.getText().toString().equals(userPhone) && isVerify) {
                return true;
            } else {
                isVerify = false;
                sp.edit().putBoolean("isVerify", false);
                return false;
            }
        }
    }

    /**
     * 判断账号密码登录
     * @param i
     *          1-需要验证码判断
     */
    private void judgeAccountLogin(int i) {
        showDialogUtil.startProgressDialog("", context);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", accounts_et.getText().toString()));
        if ("18950261460".equals(accounts_et.getText().toString())) {
            params.add(new BasicNameValuePair("ifcheck", "1"));
        } else if (i == 1)// 1需要验证码
            params.add(new BasicNameValuePair("captcha", password_et.getText().toString()));
        else
            params.add(new BasicNameValuePair("ifcheck", "1"));
        String sign = ByteUtils.JoiningTogether(params);
        params.add(new BasicNameValuePair("sign", ByteUtils.MD5(ByteUtils.MD5(sign))));
        String url = IpAddress.nodeviceLogin;
        ConnServerPost csp = new ConnServerPost(url, myHandler, "nodeviceLogin", params);
        new Thread(csp).start();
    }

    /**
     * 设备连接异常
     */
    private void connectException() {
        showDialogUtil.stopProgressDialog();
        // MyApplication.getInstance().setDiscoveredDevices(null);

        Toast.makeText(context, "连接异常,请重新连接", Toast.LENGTH_SHORT).show();
    }

    /**
     * 签到 跳转界面
     */
    private void intentActivity() {
        Intent intent = new Intent(context, HomeActivity.class);
        showDialogUtil.stopProgressDialog();
        startActivity(intent);
        sendCmd("closeBluetooth", -1);// 通知关闭蓝牙搜索
        finish();
    }

    /**
     * 返回设备信息
     */
    private void getIndex() {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String sn = BaseApplication.getInstance().getSN();
        params.add(new BasicNameValuePair("deviceUnique", sn));
        String url = IpAddress.index;
        ConnServerPost csp = new ConnServerPost(url, myHandler, "index", params);
        new Thread(csp).start();
    }

    /**
     * text状态监听
     * 当手机号和验证码输入后，增加删除按钮
     */
    private class TextChanged implements TextWatcher {
        EditText editText = null;

        public TextChanged(EditText editText) {
            this.editText = editText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (editText == accounts_et) {
                accounts_clear.setVisibility(View.VISIBLE);
            } else if (editText == password_et) {
                codeKey_clear.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (editText == accounts_et && TextUtils.isEmpty(accounts_et.getText())) {
                accounts_clear.setVisibility(View.GONE);
            } else if (editText == password_et && TextUtils.isEmpty(password_et.getText())) {
                codeKey_clear.setVisibility(View.GONE);
            }
        }
    }

//    判断各项注册返回数据
    private void Handler() {
        myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.getData() != null) {
                    String message = "";
                    if (msg.getData().get("data") != null) {
                        message = msg.getData().get("data").toString();
                    }
                    if ("ConnectException".equals(msg.obj)) {
                        showDialogUtil.stopProgressDialog();
                    } else if ("index".equals(msg.obj)) {
                        try {
                            JSONObject j = Utility.jsonObjectData(context, message);
                            String code = j.getString("code");
                            if (code.equals("1000")) {
                                JSONObject jResult = Utility.jsonObjectData(context, j.getString("result"));
                                BaseApplication.getInstance().setRate(jResult.getString("rate"));
                                BaseApplication.getInstance().setSingleCost(jResult.getString("SingleCost"));
                                BaseApplication.getInstance().setMcht_shortname(jResult.getString("mcht_shortname"));
                                BaseApplication.getInstance().setCom_phone(jResult.getString("com_phone"));// 法人手机号
                                BaseApplication.getInstance().setIf_pband(jResult.getString("if_pband"));// 是否需要绑定手机号
                                if (jResult.getString("if_pband").equals("0")) {
                                    BaseApplication.getInstance().setLogin_phone(jResult.getString("login_phone"));// 法人手机号
                                }
                                if (jResult.getString("isPre").equals("1")) {
                                    BaseApplication.getInstance().setPrompt(jResult.getString("prompt"));//
                                }
                                BaseApplication.getInstance().setIsPre(jResult.getString("isPre"));//
                                command(BaseApplication.getInstance().getSN());
                            }
                        } catch (JSONException j) {
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog("获取数据异常，请重新签到", context);
                        }
                    } else if ("getCaptcha".equals(msg.obj)) {// 获取验证码
                        showDialogUtil.stopProgressDialog();
                        if (message.equals("Timeout")) {
                            Toast.makeText(context, "获取超时，请重检查网络后重试！", Toast.LENGTH_SHORT).show();
                        } else {
                            JSONObject j = Utility.jsonObjectData(context, message);
                            String code = j.getString("code");
                            if ("1000".equals(code)) {
//                                获取验证码倒计时
                                CountdownAsyncTask cdat = new CountdownAsyncTask(codeKey, context);
                                cdat.execute(1000);
                            } else {
                                showDialogUtil.showDialog(j.getString("info"), context);
                            }
                        }
                    } else if ("nodeviceLogin".equals(msg.obj)) {
                        if (message.equals("Timeout")) {
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog("登录超时，请重检查网络后重试！", context);
                        } else {
                            JSONObject j = Utility.jsonObjectData(context, message);
                            String code = j.getString("code");
                            if (code.equals("1000")) {
                                sp.edit().putString("userPhone", accounts_et.getText().toString()).commit();
                                isVerify = true;
                                JSONObject jo = Utility.jsonObjectData(context, j.get("result"));
//                                无设备连接时的SN
                                BaseApplication.getInstance().setNoDeviceSN(jo.getString("deviceUnique"));
                                sp.edit().putBoolean("isVerify", true).commit();
                                sp.edit().putString("deviceUnique" + accounts_et.getText().toString(),
                                        jo.getString("deviceUnique")).commit();
                                isRegister(jo.getString("deviceUnique"));
                            } else if (code.equals("1005")) {
                                showDialogUtil.stopProgressDialog();
                                showDialogUtil.showDialog(j.getString("info"), context);
                            } else if (code.equals("5004")) {
                                showDialogUtil.stopProgressDialog();
                                showDialogUtil.showDialog("验证码错误", context);
                                sp.edit().putBoolean("isVerify", false).commit();
                                isVerify = false;
                            } else if (code.equals("1006")) {
                                showDialogUtil.stopProgressDialog();
                                showDialogUtil.showDialog("该号码需重新验证，请输入验证码", context);
                                sp.edit().putBoolean("isVerify", false).commit();
                                isVerify = false;
                            } else {
                                showDialogUtil.stopProgressDialog();
                                showDialogUtil.showDialog(j.getString("info"), context);
                            }
                        }
                    } else if (msg.obj.equals("signIn")) {// 签到返回
                        if (message.equals("Timeout")) {
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog("签到超时，请重新连接！", context);
                        } else {
                            obtainKey(message);
                        }
                    } else if (msg.obj.equals("IntentRegister")) {
                        if (message.equals("Timeout")) {
                            showDialogUtil.stopProgressDialog();
                            Toast.makeText(context, "连接超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(context, BusinessInformationActivity.class));
                        }
                    } else if (msg.obj.equals("command")) {// 获取口令
                        if (message.equals("Timeout")) {
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog(" 获取口令超时，请重新连接！", context);
                        } else {
                            if (BaseApplication.getInstance().isDeviceConnState()) {
                                showDialogUtil.changeProgressDialogMessage("签到...");
                                try {
                                    Map<String, String> bc = new Gson().fromJson(message,
                                            new TypeToken<Map<String, String>>() {
                                            }.getType());
                                    BaseApplication.getInstance().setCommand(bc.get("result"));
                                    // queryLog(bc.get("result"));
//                                    批次号流水号查询
                                    getTransactionList(bc.get("result"));
                                } catch (JsonParseException e) {
                                    showDialogUtil.stopProgressDialog();
                                }
                            } else {
                                showDialogUtil.stopProgressDialog();
                                try {
                                    Map<String, String> bc = new Gson().fromJson(message,
                                            new TypeToken<Map<String, String>>() {
                                            }.getType());
                                    BaseApplication.getInstance().setCommand(bc.get("result"));
                                    Intent intent = new Intent(context, HomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } catch (JsonParseException e) {
                                    showDialogUtil.stopProgressDialog();
                                }
                            }
                        }
                    } else if (msg.obj.equals("register")) {// 设备是否注册 返回
                        if (message.equals("Timeout")) {
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog("检测注册信息超时，请重新连接！", context);
                        } else {
                            registerReturn(message);
                        }
                    } else if (msg.obj.equals("UnknownHostException")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    } else if (msg.obj.equals("history")) {
                        String data = msg.getData().get("data").toString();
                        Log.i("ceshi", "history:" + data);
                        if (data.equals("Timeout")) {
                            Toast.makeText(context, "初始化超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                        } else {
                            String code = null;
                            List<Map<String, String>> hs = null;
                            History h = null;
                            try {
                                h = new Gson().fromJson(data, History.class);
                                Log.i(TAG + new Throwable().getStackTrace()[0].getLineNumber(), h.toString());
                                code = h.getCode();
                            } catch (JsonParseException j) {
                                try {
                                    showDialogUtil.stopProgressDialog();
                                    BankCard bc = new Gson().fromJson(data, BankCard.class);
                                    code = bc.getCode();
                                } catch (JsonParseException e) {
                                    Log.i(TAG + new Throwable().getStackTrace()[0].getLineNumber(), "数据异常");
                                }
                            }
                            if (code.equals("1000")) {
                                // if (!h.getHs().getCount().equals("0")) {
                                // hs = h.getHs().getList();
                                // String batchNum = hs.get(0).get("BatchNum");
                                // String traceNum = hs.get(0).get("TraceNum");
                                // Log.i("ceshi",
                                // "batchNum:"+batchNum+",traceNum:"+traceNum);
                                // String date = hs.get(0).get("date");
                                // try {
                                // Date d = new
                                // SimpleDateFormat("yyyy-MM-dd").parse(date);
                                // date = new
                                // SimpleDateFormat("yyyyMMdd").format(d);
                                // } catch (ParseException e) {
                                // e.printStackTrace();
                                // }
                                // judgeDate(batchNum, traceNum, date);//
                                // 判断是否当天操作
                                // } else {
                                // sp.edit().putInt("batchNumber", 0).commit();
                                // sp.edit().putInt("serialNumber", 0).commit();
                                // }
                                // signIn();// 连接成功后获取签到报文
                            } else if (code.equals("1111")) {
                                showDialogUtil.stopProgressDialog();
                            } else if (code.equals("2222")) {
                                showDialogUtil.stopProgressDialog();
                            } else {
                                showDialogUtil.stopProgressDialog();
                            }
                        }
                    } else if (msg.obj.equals("TransactionList")) {
                        String data = msg.getData().get("data").toString();
                        Log.i("ceshi", "TransactionList:" + data);
                        if (data.equals("Timeout")) {
                            showDialogUtil.stopProgressDialog();
                            Toast.makeText(context, "初始化超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                        } else {
                            showDialogUtil.stopProgressDialog();
                            JSONObject j = Utility.jsonObjectData(context, message);
                            String code = j.getString("code");
                            String info = j.getString("info");
                            if ("1000".equals(code)) {
                                JSONObject jo = j.getJSONObject("result");
                                Log.i("ceshi", "result:" + jo.toString());
                                String batchNumber = jo.getString("TraceNum");
                                String traceNum = jo.getString("BatchNum");
                                if (TextUtils.isEmpty(batchNumber)) {
                                    batchNumber = "0";
                                }
                                if (TextUtils.isEmpty(traceNum)) {
                                    traceNum = "0";
                                }
                                sp.edit().putInt("batchNumber", Integer.valueOf(batchNumber)).commit();
                                sp.edit().putInt("serialNumber", Integer.valueOf(traceNum)).commit();
                                Log.i("ceshi", "batchNumber:" + batchNumber + ",traceNum:" + traceNum);
                                signIn();// 连接成功后获取签到报文
                            } else if ("1001".equals(code)) {
                                sp.edit().putInt("batchNumber", 0).commit();
                                sp.edit().putInt("serialNumber", 0).commit();
                                signIn();// 连接成功后获取签到报文
                            } else {
                                showDialogUtil.stopProgressDialog();
                                // showUtil.errorMessage(info, context);
                            }
                        }
                    } else if (msg.obj.equals("openBlueTooth")) {
                        SearchBluetooth();
                        startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    } else if (msg.obj.equals("update")) {
                        Uri uri = Uri.parse(IpAddress.download);
                        Intent it = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(it);
                    }
                }
            }
        };
    }

    /**
     * 发送 获取口令
     * @param sn
     */
    private void command(String sn) {
        String url = IpAddress.Command + "&deviceUnique=" + sn;
        ConnServer command = new ConnServer(url, myHandler, "command");
        new Thread(command).start();
    }

    /**
     * 提取秘钥
     * @param message
     */
    private void obtainKey(String message) {
        try {
            Map<String, String> map = Utility.disassembleMessage(message);// 获取map形式报文
            String responseCode = map.get("98");// 98域
            if (responseCode != null && responseCode.equals("3030")) {// 返回00则签到成功
                String secretKey = map.get("99");// 获取99域
                BaseApplication.getInstance().setTMK(secretKey.substring(0, 32));
                BaseApplication.getInstance().setPIK(secretKey.substring(32, 64));
                BaseApplication.getInstance().setTDK(secretKey.substring(64, 96));
                sendCmd("setWorkKey", -1);// 通知服务执行灌装秘钥
            } else {
                showDialogUtil.stopProgressDialog();
                showDialogUtil.showDialog("签到失败，请重试！", context);
            }
        } catch (Exception e) {
            showDialogUtil.stopProgressDialog();
            showDialogUtil.showDialog("签到异常", context);
            return;
        }
    }

    /**
     * 批次号、流水号查询
     * @param Command
     */
    private void getTransactionList(String Command) {

        String sn = BaseApplication.getInstance().getSN();
        String url = null;
        String s = ByteUtils.MD5(ByteUtils.MD5("deviceUnique=" + sn) + Command);
        url = IpAddress.TransactionList + "?sign=" + s + "&deviceUnique=" + sn;
        Log.i("ceshi", "url:" + url);
        ConnServer conn = new ConnServer(url, myHandler, "TransactionList");
        new Thread(conn).start();
    }

    /**
     * 查询时间 1-当天；2-当月；3-历史
     * @param Command
     */
    private void queryLog(String Command) {
        String sn = BaseApplication.getInstance().getSN();
        String url = null;
        String s = ByteUtils
                .MD5(ByteUtils.MD5("deviceUnique=" + sn + "&page=" + 1 + "&pageN=" + 1 + "&timetype=" + 1) + Command);
        url = IpAddress.TradingQuery + "sign=" + s + "&deviceUnique=" + sn + "&page=" + 1 + "&timetype=" + 1 + "&pageN="
                + 1;
        ConnServer conn = new ConnServer(url, myHandler, "history");
        new Thread(conn).start();
    }

    /**
     * 返回注册信息
     * @param message
     */
    private void registerReturn(String message) {
        String s = null;
        String memo = null;
        JSONObject j = null;
        JSONObject jo = null;
        try {
            jo = Utility.jsonObjectData(context, message);
            s = jo.getString("code");
        } catch (JsonParseException e) {
            try {
                BankCard bc = new Gson().fromJson(message, BankCard.class);
                s = bc.getCode();
            } catch (JsonParseException a) {
                s = "数据异常";
                Log.i(TAG + new Throwable().getStackTrace()[0].getLineNumber(), "数据异常");
            }
        }

        if (BaseApplication.getInstance().isDeviceConnState()) {// 设备连接
            if (s.equals("1000")) {// 成功
                j = Utility.jsonObjectData(context, jo.get("result"));
                BaseApplication.getInstance().setTerminalId(j.getString("termNo"));// 保存终端号
                sp.edit().putString("ACT_OWNER", j.getString("ACT_OWNER")).commit();
                getIndex();// 获取设备信息
            } else {
                showDialogUtil.stopProgressDialog();
                if (s.equals("1001")) {// 设备未注册
                    showDialogUtil.selectDialog("设备未注册，请先注册", context, myHandler, "IntentRegister", "立即注册", "稍后注册");
                } else if (s.equals("1002")) {// 审批中
                    showDialogUtil.showDialog("该设备申请单正在审批中，请等待！", context);
                } else if (s.equals("1003")) {// 被拒绝
                    j = Utility.jsonObjectData(context, jo.get("result"));
                    memo = j.getString("memo");// 获取拒绝信息
                    sp.edit().putString("memo", memo).commit();// 保存拒绝信息
                    showDialogUtil.selectDialog("该设备申请单已被拒绝，请重新注册\n审批意见：" + memo, context, myHandler, "IntentRegister",
                            "立即注册", "稍后注册");
                } else if (s.equals("1005")) {// 绑定中
                    showDialogUtil.showDialog("设备绑定中，请耐心等待！", context);
                } else if (s.equals("1112")) {// 未绑定
                    showDialogUtil.showDialog("设备码错误或设备未绑定", context);
                } else {
                    showDialogUtil.showDialog("未知错误，请重新登陆！\n错误：" + s, context);
                }
            }
        } else {// 手机号登陆
            if (s.equals("1000")) {
                j = Utility.jsonObjectData(context, jo.get("result"));
                BaseApplication.getInstance().setTerminalId(j.getString("termNo"));// 保存终端
                sp.edit().putString("termNo" + accounts_et.getText().toString(), j.getString("termNo")).commit();// 保存手机号
                sp.edit().putString("deviceRegister" + accounts_et.getText().toString(), "1000").commit();// 保存注册状态
                sp.edit().putString("ACT_OWNER" + accounts_et.getText().toString(), j.getString("ACT_OWNER")).commit();// 保存姓名
                BaseApplication.getInstance().setMcht_shortname(j.getString("mcht_shortname"));// 保存商户简称
                BaseApplication.getInstance().setIf_pband(j.getString("if_pband"));// 是否需要绑定手机号
                BaseApplication.getInstance().setCom_phone(j.getString("com_phone"));// 法人手机号
                if (j.getString("if_pband").equals("0"))
                    BaseApplication.getInstance().setLogin_phone(j.getString("login_phone"));// 法人手机号
                if (j.getString("isPre").equals("1"))// 1 需要显示
                    BaseApplication.getInstance().setPrompt(j.getString("prompt"));// 预审批
                // 消息
                BaseApplication.getInstance().setIsPre(j.getString("isPre"));// 显示
                // 预审批
                // 消息
                command(BaseApplication.getInstance().getNoDeviceSN());// 获取口令
            } else {
                showDialogUtil.stopProgressDialog();
                BaseApplication.getInstance().setCom_phone(accounts_et.getText().toString());// 法人手机号
                // 截取前三位 用来判断是否WSB
                String isSN = BaseApplication.getInstance().getNoDeviceSN().subSequence(0, 3).toString();
                if (s.equals("1001")) {// 未注册
                    sp.edit().putString("deviceRegister" + accounts_et.getText().toString(), "1001").commit();
                    if (isSN.equalsIgnoreCase("WSB")) {
                        showDialogUtil.selectThreeDialog("是否完善信息？", context, "IntentRegister", infoHandler);
                    } else {
                        showDialogUtil.selectDialog("请先完善信息", context, infoSelectHandler, true);
                    }
                } else if (s.equals("1002")) {// 审批中
                    sp.edit().putString("deviceRegister" + accounts_et.getText().toString(), "1002").commit();
                    Intent intent = new Intent(context, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (s.equals("1003")) {// 被拒绝
                    j = Utility.jsonObjectData(context, jo.get("result"));
                    memo = j.getString("memo");// 获取拒绝信息
                    sp.edit().putString("deviceRegister" + accounts_et.getText().toString(), "1003").commit();
                    sp.edit().putString("memo", memo).commit();// 保存拒绝信息
                    if (isSN.equalsIgnoreCase("WSB")) {
                        if (memo != null) {
                            showDialogUtil.selectThreeDialog("该账号申请单已被拒绝，是否重新注册！\n审批意见：" + memo, context, "IntentRegister",
                                    infoHandler);
                        } else {
                            showDialogUtil.selectThreeDialog("该账号申请单已被拒绝，是否重新注册！", context, "IntentRegister", infoHandler);
                        }
                    } else {
                        if (memo != null) {
                            showDialogUtil.selectDialog("该账号申请单已被拒绝，是否重新注册！\n审批意见：" + memo, context, infoSelectHandler, true);
                        } else {
                            showDialogUtil.selectDialog("该账号申请单已被拒绝，是否重新注册！", context, infoSelectHandler, true);
                        }
                    }
                } else if (s.equals("1005")) {
                    showDialogUtil.showDialog(jo.getString("info"), context, infoHandler);
                    sp.edit().putString("deviceRegister" + accounts_et.getText().toString(), "1005").commit();
                } else {
                    sp.edit().putString("deviceRegister" + accounts_et.getText().toString(), "1001").commit();
                }
            }
        }
    }

    private Handler infoSelectHandler=new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:// 跳转注册
                    startActivity(new Intent(context, BusinessInformationActivity.class));
                    break;
                case 2:// 跳转主页
                    startActivity(new Intent(context, HomeActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    private Handler infoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:// 跳转注册
                    startActivity(new Intent(context, BusinessInformationActivity.class));
                    break;
                case 2:// 跳转绑定
                    startActivity(new Intent(context, DeviceMobilePhoneBinding.class).putExtra("bind", "loginbind"));
                    break;
                case 3:
                    startActivity(new Intent(context, HomeActivity.class));
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 签到
     */
    private void signIn() {
        SN = convertStringToHex(BaseApplication.getInstance().getSN());
        String s = "01900c" + SN + "9103" + countSerialNumber() + "9203" + countBatchNumber() + "930230319402";
        s = s + CRC1021.getCRC1021(ByteUtils.HexString2Bytes(s), ByteUtils.HexString2Bytes(s).length);
        s = Integer.toHexString(s.length() / 2) + "00" + s;
        byte[] message = ByteUtils.HexString2Bytes(s);
        SocketServer conn = new SocketServer(myHandler, message, "signIn");
        new Thread(conn).start();
    }

    /**
     * 转换成十六进制,签到中用到
     * @param str
     * @return
     */
    @NonNull
    private String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++)
            hex.append(Integer.toHexString((int) chars[i]));
        return hex.toString();
    }

    /**
     * 计算流水号
     * @return
     */
    private String countSerialNumber() {
        int a = sp.getInt("serialNumber", 0) + 1;
        sp.edit().putInt("serialNumber", a).commit();
        return RightAdd0(a + "", 6);
    }

    /**
     * 计算批次号 92
     * @return
     */
    private String countBatchNumber() {
        int a = sp.getInt("batchNumber", 0) + 1;
        sp.edit().putInt("batchNumber", a).commit();
        return RightAdd0(a + "", 6);
    }

    /**
     * 数据右"0"补位，计算流水号中用到
     * @param s
     *          数据
     * @param i
     *          共多少位
     * @return
     */
    private static String RightAdd0(String s, int i) {
        while (s.length() < i)
            s = "0" + s;
        return s;
    }

    /**
     * 判断蓝牙是否开启
     */
    private void SearchBluetooth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!bluetoothAdapter.isEnabled()) {
                    try {// 若蓝牙关闭状态 我们每个两秒判断蓝牙是否一打开
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendCmd("SearchBluetooth", -1);// 通知启动蓝牙搜索
            }
        }).start();
    }

    /**
     * 获取验证码
     */
    private void getCodeKeyLogin() {
        if (BaseApplication.getInstance().isDeviceConnState()) {
            Toast.makeText(context, "请先断开设备", Toast.LENGTH_SHORT).show();
        } else {
            if (update == 0) {
                String phone = accounts_et.getText().toString();
                if (TextUtils.isEmpty(phone)) {
                    showDialogUtil.showDialog("请输入手机号", context);
                } else {
                    if (!phone.matches("^1[3|5|7|8|][0-9]{9}$")) {// 电话是否为空
                        Toast.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        String userPhone = sp.getString("userPhone", null);
                        if (userPhone != null && !userPhone.equals("") && phone.equals(userPhone) && isVerify) {
                            Toast.makeText(context, "该手机号码已通过验证，可直接登录", Toast.LENGTH_SHORT).show();
                        } else {
                            showDialogUtil.startProgressDialog("", context);
                            MessageAuthenticationCode(phone, "wsb_login");
                        }
                    }
                }
            } else if (update == 1) {
                showDialogUtil.selectDialog("请先更新版本", context, myHandler, "update", "立即更新", "稍后更新");
            } else if (update == 2) {
                showDialogUtil.showDialog("正在检测版本信息", context);
            } else if (update == 3) {
                showDialogUtil.showDialog("请重新运行程序", context);
            }
        }
    }

    /**
     * 短信验证码 注册wsb_reg 网页注册wsb_regweb 登录wsb_login
     * @param phone
     * @param operation_type
     */
    private void MessageAuthenticationCode(String phone, String operation_type) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", phone));
        params.add(new BasicNameValuePair("operation_type", operation_type));
        String sign = ByteUtils.JoiningTogether(params);
        params.add(new BasicNameValuePair("sign", ByteUtils.MD5(ByteUtils.MD5(sign))));
        String url = IpAddress.getCaptcha;// 获取短信验证码
        ConnServerPost csp = new ConnServerPost(url, myHandler, "getCaptcha", params);
        new Thread(csp).start();
    }

    // 注册广播
    protected void onBroadcast() {
        myService = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.great.activity.LoginActivity");
        filter.addAction("org.great.activity.All");
        registerReceiver(myService, filter);// 注册广播
        Intent intent = new Intent(context, MyService.class);
        startService(intent);// 启动服务
    }

    /**
     * 摧毁广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myService != null) {
            unregisterReceiver(myService);//注销广播

        }
    }

    @Override
    public void onBackPressed() {
        runBack();
    }

    // 后台运行
    public void runBack() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
}
