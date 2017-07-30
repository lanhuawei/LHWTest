package baifu.www.lhwtest.activity;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.entity.BankCard;
import baifu.www.lhwtest.entity.BluetoothDeviceContext;
import baifu.www.lhwtest.internet.ConnServerPost;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.utils.ByteUtils;
import baifu.www.lhwtest.utils.CountdownAsyncTask;
import baifu.www.lhwtest.utils.Utility;
import baifu.www.lhwtest.utils.showDialogUtil;

/**
 * Created by Ivan.L on 2017/7/27.
 * 设备和手机绑定
 */

public class DeviceMobilePhoneBinding extends BaseActivity implements View.OnClickListener {

    private Context context = DeviceMobilePhoneBinding.this;
    private SharedPreferences sp;

    private TextView deviceNumber;// 输入设备号TextView
    private EditText phoneNumber;// 输入手机号edittext
    private EditText code_et;// 输入验证码edittext
    private TextView getCodeKey;// 获取验证码 按钮
    private Button submitBinding;// 提交绑定信息button
    private ImageView back_iv, phoneNumberIV, code_etIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        myHandler();
        onBroadcast();
    }

    /** 初始化 */
    private void initView() {
        sp = getSharedPreferences("localData", Activity.MODE_PRIVATE);

        deviceNumber = (TextView) findViewById(R.id.device_number_tv);
        phoneNumber = (EditText) findViewById(R.id.phone_number_et);
        code_et = (EditText) findViewById(R.id.code_et);

        view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        bluetooth_name_lv = (ListView) view.findViewById(R.id.bluetooth_name_lv);
        cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
        dialog = new Dialog(this, R.style.selectorDialog);

        phoneNumberIV = (ImageView) findViewById(R.id.phone_number_iv);//清空手机号
        code_etIV = (ImageView) findViewById(R.id.code_iv);//清空验证码

        code_et.addTextChangedListener(new TextChanged(code_et));//验证码监听事件

        getCodeKey = (TextView) findViewById(R.id.get_codeKey_tv);//获取验证码
        submitBinding = (Button) findViewById(R.id.submit_binding_btn);//提交绑定
        Button replacePhoneNumber = (Button) findViewById(R.id.replace_phone_number_btn);//更换手机号
        back_iv = (ImageView) findViewById(R.id.back_iv);
        LinearLayout codeKeyLL = (LinearLayout) findViewById(R.id.codeKey_ll);
        phoneNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
        code_et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
        deviceNumber.setOnClickListener(this);
        getCodeKey.setOnClickListener(this);
        submitBinding.setOnClickListener(this);
        replacePhoneNumber.setOnClickListener(this);
        back_iv.setOnClickListener(this);
        phoneNumberIV.setOnClickListener(this);
        code_etIV.setOnClickListener(this);
        String isSN = null;
        if(null!= BaseApplication.getInstance().getNoDeviceSN()){
            isSN = BaseApplication.getInstance().getNoDeviceSN().subSequence(0, 3).toString();
        }
        boolean deviceConnState = BaseApplication.getInstance().isDeviceConnState();
        String ifPband = BaseApplication.getInstance().getIf_pband();//是否注册//1 需要绑定  0不需要绑定
        String com_phone = BaseApplication.getInstance().getCom_phone();//法人手机号
//		String com_phone = MyApplication.getInstance().getLogin_phone();//登入手机号
        String login_phone = BaseApplication.getInstance().getLogin_phone();//
        String sn = BaseApplication.getInstance().getSN();//SN
        if("loginbind".equals(getIntent().getStringExtra("bind"))){
            replacePhoneNumber.setVisibility(View.GONE);
        }
        if("WSB".equalsIgnoreCase(isSN)&&!deviceConnState){//手机登录 未绑定
            codeKeyLL.setVisibility(View.GONE);//隐藏验证码
            phoneNumber.setText(com_phone);//输入手机号
            phoneNumber.setFocusable(false);//不可编辑
            phoneNumberIV.setVisibility(View.GONE);
            replacePhoneNumber.setText("更换手机号");
//			replacePhoneNumber.setVisibility(View.GONE);
        }else if(!"WSB".equalsIgnoreCase(isSN)&&!deviceConnState){//手机登录 绑定
            codeKeyLL.setVisibility(View.GONE);//隐藏验证码
            submitBinding.setVisibility(View.GONE);//隐藏确定按钮
            phoneNumberIV.setVisibility(View.GONE);
            phoneNumber.setText(sp.getString("userPhone",""));//输入手机号
            deviceNumber.setText(BaseApplication.getInstance().getNoDeviceSN());
            phoneNumber.setFocusable(false);//不可编辑
            deviceNumber.setFocusable(false);//失焦
            deviceNumber.setClickable(false);
        }else if(deviceConnState){//设备连接
            deviceNumber.setText(sn);
            deviceNumber.setFocusable(false);
            deviceNumber.setClickable(false);
            if("1".equals(ifPband)){//未绑定手机号  验证
                phoneNumber.addTextChangedListener(new TextChanged(phoneNumber));
                phoneNumber.setText(com_phone);//输入手机号
                replacePhoneNumber.setVisibility(View.GONE);
            }else if("0".equals(ifPband)){//绑定手机号
                codeKeyLL.setVisibility(View.GONE);//隐藏验证码
                submitBinding.setVisibility(View.GONE);//隐藏确定按钮
                phoneNumber.setText(login_phone);//输入手机号
                phoneNumber.setFocusable(false);//不可编辑
                phoneNumberIV.setVisibility(View.GONE);
            }else{
                Log.i("device_155", "ifPband="+ifPband);
            }
        }
    }

    private ArrayAdapter<String> nameAdapter;
    private ListView bluetooth_name_lv;
    private Button cancel_btn;

    private List<BluetoothDeviceContext> discoveredDevices = new ArrayList<BluetoothDeviceContext>();
    private BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();// 蓝牙

    /** 添加设备名到listview */
    private void selectBtAddrToInit() {
        int i = 0;
        String[] bluetoothNames = new String[discoveredDevices.size()];
        for (BluetoothDeviceContext device : discoveredDevices) {
            bluetoothNames[i++] = device.name;
        }
        bluetoothListView();
        nameAdapter = new ArrayAdapter<String>(this, R.layout.list_view_item,
                bluetoothNames);
        bluetooth_name_lv.setAdapter(nameAdapter);
        bluetooth_name_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                dialog.dismiss();
                dialog.cancel();
                showDialogUtil.startProgressDialog(" 初始化设备 ", context);
                connectingDevice(arg2);
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private String deviceName = null;// 设备名称
    /**
     * 连接设备
     * @param num
     */
    private void connectingDevice(final int num) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                deviceName = discoveredDevices.get(num).name;
                BaseApplication.getInstance().setDeviceName(deviceName);
                discoveredDevices = null;
                sendCmd("connect", num);
            }
        }).start();
    }


    private Dialog dialog;
    private View view;
    private Window window;
    /** 显示蓝牙设备的listview */
    @SuppressWarnings("deprecation")
    private void bluetoothListView() {
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        window = dialog.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay().getHeight();
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialog.onWindowAttributesChanged(wl);
        dialog.setCanceledOnTouchOutside(true);
    }

    /**
     * text状态监听
     */
    public class TextChanged implements TextWatcher {
        EditText e;

        public TextChanged(EditText e) {
            this.e = e;
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (e == phoneNumber && TextUtils.isEmpty(phoneNumber.getText())) {
                phoneNumberIV.setVisibility(View.GONE);
            }
            if (e == code_et && TextUtils.isEmpty(code_et.getText())) {
                code_etIV.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
            if (e == phoneNumber) {
                phoneNumberIV.setVisibility(View.VISIBLE);
            }else if (e == code_et) {
                code_etIV.setVisibility(View.VISIBLE);
            }
        }
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
                serviceIntent.setAction("org.great.util.MyService");
                serviceIntent.putExtra("value", value);
                serviceIntent.putExtra("num", i);
                context.sendBroadcast(serviceIntent);
            }
        }).start();
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_device_mobile_phone_binding;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.phone_number_iv:
                phoneNumber.setText("");
                phoneNumberIV.setVisibility(View.GONE);
                break;
            case R.id.code_iv:
                code_et.setText("");
                code_etIV.setVisibility(View.GONE);
                break;
            case R.id.back_iv:
                if (!BaseApplication.getInstance().isDeviceConnState()) {//是否是设备连接
                    sendCmd("DeviceDisconnect", 0);//断开链接
                }
                finish();
                break;
            case R.id.submit_binding_btn:// 提交绑定信息
                String deviceNumberStr = deviceNumber.getText().toString();
                String phoneNumStr = phoneNumber.getText().toString();
                String codeStr = code_et.getText().toString();
                if (isEmpty()) {
                    showDialogUtil.startProgressDialog("", context);
                    submitBinding(deviceNumberStr, phoneNumStr, codeStr);
                }
                break;
            case R.id.replace_phone_number_btn://换绑定手机号
                Intent intent = new Intent(context,VerificationOrdPhone.class);
                intent.putExtra("deviceNumber",deviceNumber.getText().toString());
                intent.putExtra("phoneNumber",phoneNumber.getText().toString());
                startActivityForResult(intent, 0);
                break;
            case R.id.get_codeKey_tv:// 获取验证码
                String phoneNumberStr = phoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumberStr)) {
                    showDialogUtil.showDialog("请输入手机号", context);
                } else {
                    if (!phoneNumberStr.matches("^1[3|5|7|8|][0-9]{9}$")) {// 电话是否为空
                        Toast.makeText(context, "手机号格式错误",Toast.LENGTH_SHORT).show();
                    } else {
                        showDialogUtil.startProgressDialog("", context);
                        MessageAuthenticationCode(phoneNumberStr, "wsb_reg");
                    }
                }
                break;
            case R.id.device_number_tv:
                if (bluetoothAdapter.isEnabled()) {
                    discoveredDevices = BaseApplication.getInstance()
                            .getDiscoveredDevices();
                    if (null == discoveredDevices) {
                        discoveredDevices = new ArrayList<BluetoothDeviceContext>();
                    }
                    selectBtAddrToInit();
                } else {
                    showDialogUtil.selectDialog("请为钱宝打开蓝牙", context, myHandler,"openBlueTooth", "设置", "好");
                }
                break;
            default:
                break;
        }

    }

    /**
     * 短信验证码 注册wsb_reg 网页注册wsb_regweb 登录wsb_login
     */
    private void MessageAuthenticationCode(String phone, String operation_type) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", phone));
        params.add(new BasicNameValuePair("operation_type", operation_type));
        String sign = ByteUtils.JoiningTogether(params);
        params.add(new BasicNameValuePair("sign", ByteUtils.MD5(ByteUtils.MD5(sign))));
        String url = IpAddress.getCaptcha;// 获取短信验证码
        ConnServerPost csp = new ConnServerPost(url, myHandler, "getCaptcha",params);
        new Thread(csp).start();
    }

    /**
     * 判断提交的绑定信息是否为空
     * @return
     */
    private boolean isEmpty() {
        if (TextUtils.isEmpty(deviceNumber.getText())) {
            showDialogUtil.showDialog("请输入设备号", context);
        } else {
            if (TextUtils.isEmpty(phoneNumber.getText())) {
                showDialogUtil.showDialog("请输入手机号", context);
            } else {
                if (!phoneNumber.getText().toString()
                        .matches("^1[3|5|7|8|][0-9]{9}$")) {// 电话是否为空
                    Toast.makeText(context, "手机号格式错误",Toast.LENGTH_SHORT).show();
                } else {
                    if (TextUtils.isEmpty(code_et.getText())&&!BaseApplication.getInstance().isPhoneDeviceConnState()) {
                        showDialogUtil.showDialog("请输入验证码", context);
                    } else {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** 提交绑定 */
    private void submitBinding(String deviceUnique, String phone, String captcha) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String url;
        if(BaseApplication.getInstance().isDeviceConnState()){//设备登录
            params.add(new BasicNameValuePair("deviceUnique", deviceUnique));
            params.add(new BasicNameValuePair("captcha", captcha));
            params.add(new BasicNameValuePair("phone", phone));
            url = IpAddress.nodeviceReg;
        }else{//手机号登录
            if("loginbind".equals(getIntent().getExtras().getString("bind"))){
                params.add(new BasicNameValuePair("deviceBind",deviceUnique));
                params.add(new BasicNameValuePair("phone", phone));
                BaseApplication.getInstance().setCommand("");
                url = IpAddress.nodeviceLoginBind;
            }else{
                params.add(new BasicNameValuePair("deviceUnique",BaseApplication.getInstance().getNoDeviceSN()));
                params.add(new BasicNameValuePair("deviceBind", deviceUnique));
                url = IpAddress.deviceBind;
            }
        }
        String sign = ByteUtils.JoiningTogether(params);
        params.add(new BasicNameValuePair("sign",
                ByteUtils.MD5(ByteUtils.MD5(sign)+BaseApplication.getInstance().getCommand())));
        ConnServerPost csp = new ConnServerPost(url, myHandler,"submitBinding", params);
        new Thread(csp).start();
    }

    private Handler myHandler;
    private void myHandler() {
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.getData().getString("data");
                if (msg.obj.equals("getCaptcha")) {
                    showDialogUtil.stopProgressDialog();
                    if (data.equals("Timeout")) {
                        showDialogUtil.showDialog("获取验证码超时，请检查网络后重试", context);
                    } else {
                        JSONObject j = Utility.jsonObjectData(context,data);
                        String code = j.getString("code");
                        if ("1000".equals(code)) {
                            CountdownAsyncTask cdat = new CountdownAsyncTask(getCodeKey, context);
                            cdat.execute(1000);
                        } else {
                            showDialogUtil.showDialog(j.getString("info"), context);
                        }
                    }
                } else if (msg.obj.equals("submitBinding")) {
                    if (data.equals("Timeout")) {
                        showDialogUtil.stopProgressDialog();
                        showDialogUtil.showDialog("上传超时，请检查网络后重试", context);
                    } else {
                        showDialogUtil.stopProgressDialog();
                        JSONObject j = Utility.jsonObjectData(context,data);
                        String code = j.getString("code");
                        if ("1000".equals(code)) {
                            BaseApplication.getInstance().setIf_pband("0");//是否需要绑定手机号
                            if(BaseApplication.getInstance().isDeviceConnState()){
                                BaseApplication.getInstance().setLogin_phone(phoneNumber.getText().toString());
                            }else{
                                BaseApplication.getInstance().setNoDeviceSN(deviceNumber.getText().toString());
                            }
                            showDialogUtil.showDialog("绑定成功，请重新登陆", context,new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
//									Intent i = new Intent(context,HomeNewActivity.class);
//									i.putExtra("finish", "bangding");
//									setResult(RESULT_OK, i);
                                    //返回登入页
                                    Intent stopSer = new Intent(DeviceMobilePhoneBinding.this, MyService.class);
                                    Intent intent = new Intent(DeviceMobilePhoneBinding.this, LoginActivity.class);
                                    sendCmd("DeviceDisconnect", 0);
                                    stopService(stopSer);// 停止服务
                                    startActivity(intent);// 返回登录界面
                                    finish();
                                }
                            });
                            Log.i("DeviceMobilePhoneBinding-371",j.getString("info"));
                        } else {
                            showDialogUtil.showDialog(j.getString("info"), context);
                            Log.i("DeviceMobilePhoneBinding-375",j.getString("info"));
                        }
                    }
                } else if (msg.obj.equals("openBlueTooth")) {
                    SearchBluetooth();
                    startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                }else if (msg.obj.equals("register")) {// 设备是否注册 返回
                    if (data.equals("Timeout")) {
                        showDialogUtil.stopProgressDialog();
                        showDialogUtil.showDialog("检测注册信息超时，请重新连接！",context);
                    } else {
                        registerReturn(data);
                    }
                }
            }
        };
    }

    /**
     * 判断蓝牙是否开启
     */
    private void SearchBluetooth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!bluetoothAdapter.isEnabled()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                sendCmd("SearchBluetooth", -1);
            }
        }).start();
    }

    /**
     * 返回注册信息
     */
    private void registerReturn(String message) {
        String s = null;
        String memo = null;
        JSONObject j = null;
        JSONObject jo = null;
        try {
            jo = Utility.jsonObjectData(context,message);
            s = jo.getString("code");
        } catch (JsonParseException e) {
            try{
                BankCard bc = new Gson().fromJson(message, BankCard.class);
                s = bc.getCode();
            }catch(JsonParseException a){
                s="数据异常";
                Log.i("LoginActivity—"+new Throwable().getStackTrace()[0].getLineNumber(),"数据异常");
            }
        }
        if(BaseApplication.getInstance().isDeviceConnState()){
            if (s.equals("1000")) {
                showDialogUtil.stopProgressDialog();
                j = Utility.jsonObjectData(context,jo.get("result"));
                BaseApplication.getInstance().setTerminalId(j.getString("termNo"));
                sp.edit().putString("ACT_OWNER",j.getString("ACT_OWNER")).commit();
                BaseApplication.getInstance().setMcht_shortname(j.getString("mcht_shortname"));
                BaseApplication.getInstance().setIf_pband(j.getString("if_pband"));//是否需要绑定手机号
                BaseApplication.getInstance().setCom_phone(j.getString("com_phone"));//法人手机号
                if(j.getInt("if_pband")==0){
                    BaseApplication.getInstance().setLogin_phone(j.getString("login_phone"));//法人手机号
                }
                showDialogUtil.showDialog("绑定成功", context,true);
            } else {
                showDialogUtil.stopProgressDialog();
                if (s.equals("1001")) {
                    showDialogUtil.selectDialog("设备未注册，请先注册", context, myHandler, "IntentRegister","立即注册", "稍后注册");
                } else if (s.equals("1002")) {
                    showDialogUtil.showDialog("该设备申请单正在审批中，请等待！",context);
                } else if (s.equals("1003")) {
                    j = Utility.jsonObjectData(context,jo.get("result"));
                    memo = j.getString("memo");
                    showDialogUtil.selectDialog("该设备申请单已被拒绝，请重新注册\n审批意见：" + memo, context, myHandler, "IntentRegister","立即注册", "稍后注册");
                } else if (s.equals("1005")) {
                    showDialogUtil.showDialog("设备绑定中，请耐心等待！",context);
                } else if (s.equals("1112")) {
                    showDialogUtil.showDialog("设备码错误或设备未绑定",context);
                } else {
                    showDialogUtil.showDialog("未知错误，请重新登陆！\n错误："+s,context);
                }
            }
        }else{
            if (s.equals("1000")) {
                showDialogUtil.stopProgressDialog();
                j = Utility.jsonObjectData(context,jo.get("result"));
                BaseApplication.getInstance().setTerminalId(j.getString("termNo"));
                sp.edit().putString("termNo"+phoneNumber.getText().toString(),j.getString("termNo")).commit();
                sp.edit().putString("deviceRegister"+phoneNumber.getText().toString(),"1000").commit();
                sp.edit().putString("ACT_OWNER"+phoneNumber.getText().toString(),j.getString("ACT_OWNER")).commit();
                BaseApplication.getInstance().setMcht_shortname(j.getString("mcht_shortname"));
                BaseApplication.getInstance().setIf_pband(j.getString("if_pband"));//是否需要绑定手机号
                BaseApplication.getInstance().setCom_phone(j.getString("com_phone"));//法人手机号
                if(j.getInt("if_pband")==0){
                    BaseApplication.getInstance().setLogin_phone(j.getString("login_phone"));//法人手机号
                }
                showDialogUtil.showDialog("绑定成功", context,true);
            } else {
                showDialogUtil.stopProgressDialog();
                if (s.equals("1001")) {
                    sp.edit().putString("deviceRegister"+phoneNumber.getText().toString(),"1001").commit();
//					showUtil.selectDialog("是否完善信息？", context,infoHandler,true);
                } else if (s.equals("1002")) {
                    sp.edit().putString("deviceRegister"+phoneNumber.getText().toString(),"1002").commit();
                    Intent intent = new Intent(context,HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else if (s.equals("1003")) {
                    j = Utility.jsonObjectData(context,jo.get("result"));
                    memo = j.getString("memo");
                    sp.edit().putString("deviceRegister"+phoneNumber.getText().toString(),"1003").commit();
                    if(memo!=null){
//						showUtil.selectDialog("该账号申请单已被拒绝，是否重新注册！\n审批意见："+memo, context, infoHandler,true);
                    }else{
//						showUtil.selectDialog("该账号申请单已被拒绝，是否重新注册！", context, infoHandler,true);
                    }
                } else if (s.equals("1005")) {
//					showUtil.showDialog("设备绑定中，请耐心等待！",context,infoHandler);
                    sp.edit().putString("deviceRegister"+phoneNumber.getText().toString(),"1005").commit();
                } else {
                    sp.edit().putString("deviceRegister"+phoneNumber.getText().toString(),"1001").commit();
                }
            }
        }
    }

    private MyBroadcast myService;

    protected void onBroadcast() {
        myService = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.great.activity.DeviceMobilePhoneBinding");
        filter.addAction("org.great.activity.All");
        registerReceiver(myService, filter);// 注册广播
        Intent intent = new Intent(context, MyService.class);
        startService(intent);// 启动服务
    }

    /**
     * 广播
     */
    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context1, Intent intent) {
            String info = intent.getStringExtra("value");// 获取返回的数据
            if ("cancelDiscovery".equals(info)) {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    discoveredDevices = BaseApplication.getInstance()
                            .getDiscoveredDevices();
                    selectBtAddrToInit();
                }
            }else if ("connectSucceed".equals(info)) {// 连接成功
                showDialogUtil.stopProgressDialog();
                deviceNumber.setText(BaseApplication.getInstance().getSN());// deviceName);
                BaseApplication.getInstance().setPhoneDeviceConnState(true);
            }else if ("exceptionalDisconnect".equals(info)
                    || "initiativeDisconnect".equals(info)) {
                BaseApplication.getInstance().setPhoneDeviceConnState(false);
                deviceNumber.setText("");
            }else if ("PowerLow".equals(info)) {
                Toast.makeText(context, "电量过低,请及时充电",Toast.LENGTH_SHORT).show();
            }else if ("connectException".equals(info)) {
                showDialogUtil.stopProgressDialog();
                Toast.makeText(context, "连接异常请重新连接", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if(data.getStringExtra("finish") != null){
                Intent i = new Intent(context,HomeActivity.class);
                i.putExtra("finish", "huanbang");
                setResult(RESULT_OK, i);
                finish();
            }
        }
    }

    /**
     * 摧毁广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myService != null) {
            unregisterReceiver(myService);
        }
    }
}
