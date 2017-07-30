package baifu.www.lhwtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.adapter.HomeGridAdapter;
import baifu.www.lhwtest.adapter.HomeGridTempAdapter;
import baifu.www.lhwtest.entity.HomeModel;
import baifu.www.lhwtest.entity.HomePage;
import baifu.www.lhwtest.utils.showDialogUtil;
import baifu.www.lhwtest.view.MyGridView;

/**
 * Created by Ivan.L on 2017/7/26.
 * 主页面
 */

public class HomeActivity extends BaseActivity implements View.OnClickListener{

    public static HomeActivity homeActivity = null;
    private TextView device_name_tv;
    private SharedPreferences sp;
    private MyBroadcast myService;
    private String deviceName;
    private Context context = HomeActivity.this;
    public String userPhone;
    // 动态布局新增
    private MyGridView gv_homemodle;
    private HomeGridTempAdapter adapterTemp;
    private HomeGridAdapter adapter;
    private List<HomeModel> listHomeModles = new ArrayList<HomeModel>();


    public void initDate() {
        listHomeModles.add(new HomeModel(R.mipmap.home_02, "还款"));
        // listHomeModles.add(new HomeModle(R.drawable.home_01, "实名认证"));
        // listHomeModles.add(new HomeModle(R.drawable.home_03, "账号绑定"));
        listHomeModles.add(new HomeModel(R.mipmap.home_04, "信用卡申请"));
        listHomeModles.add(new HomeModel(R.mipmap.home_05, "查询"));
        // listHomeModles.add(new HomeModle(R.drawable.home_06, "收款码"));
        listHomeModles.add(new HomeModel(R.mipmap.home_07, "使用说明"));
        listHomeModles.add(new HomeModel(R.mipmap.home_08, "安全退出"));
        listHomeModles.add(new HomeModel(R.mipmap.wode, "我的"));
    }

    /**
     * 初始化主页面数据
     */
    private List<HomePage.ResultBean> list_resule = new ArrayList<HomePage.ResultBean>();

    // 判断是否为数字
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        动态加载布局
        gv_homemodle = (MyGridView) findViewById(R.id.gv_home);
        if (null != BaseApplication.getInstance().getList_resule()
                && 0 != BaseApplication.getInstance().getList_resule().size()) {
            list_resule = BaseApplication.getInstance().getList_resule();
            adapter = new HomeGridAdapter(HomeActivity.this,list_resule);
            gv_homemodle.setAdapter(adapter);
            Log.i("tag", "动态adapt");
        } else {
            initDate();
            adapterTemp = new HomeGridTempAdapter(this, listHomeModles);
            gv_homemodle.setAdapter(adapterTemp);
            Log.i("tag", "静态adapt");
        }
        gv_homemodle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = null;
                // &&null!=MyApplication.getInstance().getHmBitmap()
                if (null != BaseApplication.getInstance().getList_resule()
                        && 0 != BaseApplication.getInstance().getList_resule().size()) {
                    name = list_resule.get(position).getType();
                } else {
                    name = listHomeModles.get(position).getName();
                }

                switch (name) {
                    case "smrz":// 实名认证
                    case "实名认证":
                        if (!BaseApplication.getInstance().isDeviceConnState()) {
                            if (judgePerfectInformation()) {
                                // System.out.println(MyApplication.getInstance().getNodeviceSN());
                                // String isSN =
                                // MyApplication.getInstance().getNodeviceSN().subSequence(0,
                                // 3).toString();
                                // if(isSN.equalsIgnoreCase("WSB")){
                                // showUtil.showDialog("该功能需绑定设备", context);
                                // }else{
                                Intent certificationIntent = new Intent(HomeActivity.this, CertificationActivity.class);
                                startActivity(certificationIntent);
                                // }
                            } else {
                                String a = sp.getString("deviceRegister" + userPhone, null);
                                deviceRegister(a);
                            }
                        } else {
                            Intent certificationIntent = new Intent(HomeActivity.this, CertificationActivity.class);
                            startActivity(certificationIntent);
                        }
                        break;
                    case "hk":// 还款
                    case "还款":
                        if (!BaseApplication.getInstance().isDeviceConnState()) {
                            if (judgePerfectInformation()) {
                                // showUtil.showDialog("该功能需连接设备", context);
                                Intent proceedsIntent = new Intent(HomeActivity.this, SelectBankActivity.class);
                                startActivity(proceedsIntent);
                            } else {
                                String a = sp.getString("deviceRegister" + userPhone, null);
                                deviceRegister(a);
                            }
                        } else {
                            Intent proceedsIntent = new Intent(HomeActivity.this, SelectBankActivity.class);
                            startActivity(proceedsIntent);
                        }
                        break;
                    case "zhbd":// 账号绑定
                    case "账号绑定":
                        if (BaseApplication.getInstance().isDeviceConnState()) {
                            Intent intentBusinessInfo = new Intent(HomeActivity.this, DeviceMobilePhoneBinding.class);
                            startActivityForResult(intentBusinessInfo, 0);
                        } else {
                            if (judgePerfectInformation()) {
                                Intent intentBusinessInfo = new Intent(HomeActivity.this,
                                        DeviceMobilePhoneBinding.class);
                                intentBusinessInfo.putExtra("bind", "false");
                                startActivityForResult(intentBusinessInfo, 0);
                            } else {
                                String a = sp.getString("deviceRegister" + userPhone, null);
                                deviceRegister(a);
                            }
                        }
                        break;
                    case "xyksq":// 信用卡申请
                    case "信用卡申请":
                        startActivity(new Intent(context, ApplyCreditCard.class));
                        break;
                    case "cx":// 查询
                    case "查询":
                        if (!BaseApplication.getInstance().isDeviceConnState()) {
                            if (judgePerfectInformation()) {
                                Intent queryIntent = new Intent(HomeActivity.this, QueryTransactionActivity.class);
                                // Intent queryIntent = new
                                // Intent(HomeNewActivity.this,
                                // PayQueryActivity.class);
                                queryIntent.putExtra("showBtn", "false");
                                startActivity(queryIntent);
                            } else {
                                String a = sp.getString("deviceRegister" + userPhone, null);
                                deviceRegister(a);
                            }
                        } else {
                            Intent queryIntent = new Intent(HomeActivity.this, QueryTransactionActivity.class);
                            // Intent queryIntent = new Intent(HomeNewActivity.this,
                            // PayQueryActivity.class);
                            startActivity(queryIntent);
                        }
                        break;
                    case "skm":// 收款码
                    case "收款码":
                        if (MyApplication.getInstance().isDeviceConnState()) {
                            String terminalId = MyApplication.getInstance().getTerminalId();
                            String url = IpAddress.sweepCodeToPay(terminalId);
                            intentQRCode(url);
                        } else {
                            if (judgePerfectInformation()) {
                                String terminalId = MyApplication.getInstance().getTerminalId();
                                String url = IpAddress.sweepCodeToPay(terminalId);
                                intentQRCode(url);
                            } else {
                                String a = sp.getString("deviceRegister" + userPhone, null);
                                deviceRegister(a);
                            }
                        }
                        break;
                    case "sysm":// 使用说明
                    case "使用说明":
                        Intent wechatLimitIntent = new Intent(context, WebActivity.class);
                        wechatLimitIntent.putExtra("status", "使用说明");
                        wechatLimitIntent.putExtra("url", IpAddress.Use_document);
                        startActivity(wechatLimitIntent);
                        break;
                    case "aqtc":// 安全退出
                    case "安全退出":
                        if (MyApplication.getInstance().isDeviceConnState()) {
                            // showUtil.startProgressDialog("签退中...", context);
                            // SignBack();//签退 暂时关闭
                            Intent stopSer = new Intent(HomeNewActivity.this, MyService.class);
                            Intent intent = new Intent(HomeNewActivity.this, LoginActivity.class);
                            sendCmd("DeviceDisconnect", 0);
                            stopService(stopSer);// 停止服务
                            startActivity(intent);// 返回登录界面
                            finish();
                        } else {
                            Intent stopSer = new Intent(HomeNewActivity.this, MyService.class);
                            Intent intent = new Intent(HomeNewActivity.this, LoginActivity.class);
                            sendCmd("DeviceDisconnect", 0);
                            stopService(stopSer);// 停止服务
                            startActivity(intent);// 返回登录界面
                            finish();
                        }
                        break;
                    case "myself":// 我的
                        if (!MyApplication.getInstance().isDeviceConnState()) {
                            if (judgePerfectInformation()) {
                                Intent i = new Intent(context, MyselfActivity.class);
                                i.putExtra("userPhone", userPhone);
                                startActivity(i);
                            } else {
                                String a = sp.getString("deviceRegister" + userPhone, null);
                                deviceRegister(a);
                            }
                        } else {
                            Intent i = new Intent(context, MyselfActivity.class);
                            i.putExtra("userPhone", userPhone);
                            startActivity(i);
                        }
                        break;
                    case "xexd":
                        startActivity(new Intent(context, MicrofinanceActivity.class));
                        break;
                    case "dsh":
                        if (MyApplication.getInstance().isDeviceConnState()) {
                            Intent inputMoneyIntent = new Intent(HomeNewActivity.this, InputMoneyMoreActivity.class);
                            startActivity(inputMoneyIntent);
                        } else {
                            if (judgePerfectInformation()) {
                                Intent inputMoneyIntent = new Intent(HomeNewActivity.this, InputMoneyMoreActivity.class);
                                startActivity(inputMoneyIntent);
                            } else {
                                String a = sp.getString("deviceRegister" + userPhone, null);
                                deviceRegister(a);
                            }
                        }
                        break;
                }
            }
        });

    }

    /**
     * 判断信息是否完善
     * @return
     */
    public boolean judgePerfectInformation() {
        String deviceUnique = sp.getString("deviceUnique" + userPhone, null);
        String termNo = sp.getString("termNo" + userPhone, null);
        if (userPhone != null && deviceUnique != null && termNo != null) {
            if ("1000".equals(sp.getString("deviceRegister" + userPhone, null)))
                return true;
            else
                return false;
        }
        return false;
    }

    /**
     * 设备注册
     * @param s
     */
    private void deviceRegister(String s) {
        String isSN = BaseApplication.getInstance().getNoDeviceSN().subSequence(0, 3).toString();
        if ("1001".equals(s)) {
            if (isSN.equalsIgnoreCase("WSB")) {
                showDialogUtil.selectThreeDialog("是否完善信息？", context, "IntentRegister", infoHandler);
            } else {
                showDialogUtil.selectDialog("请先完善信息", context, infoSelectHandler, true);
            }
        } else if ("1002".equals(s)) {
            showDialogUtil.showDialog("信息审批中，请稍等", context);
        } else if ("1003".equals(s)) {
            String memo = sp.getString("memo", null);
            if (isSN.equalsIgnoreCase("WSB")) {
                if (null == memo) {
                    showDialogUtil.selectThreeDialog("该设备申请单已被拒绝，需要重新注册！", context, "IntentRegister", infoHandler);
                } else {
                    showDialogUtil.selectThreeDialog("该设备申请单已被拒绝，请重新注册\n审批意见：" + memo, context, "IntentRegister",
                            infoHandler);
                }
            } else {
                if (null == memo) {
                    showDialogUtil.selectDialog("该账号申请单已被拒绝，需要重新注册！", context, infoSelectHandler, true);
                } else {
                    showDialogUtil.selectDialog("该账号请单已被拒绝，请重新注册\n审批意见：" + memo, context, infoSelectHandler, true);
                }
            }
        } else if ("1005".equals(s)) {
            showDialogUtil.showDialog("设备绑定中，请耐心等待！", context);
        } else {
            if (isSN.equalsIgnoreCase("WSB")) {
                showDialogUtil.selectThreeDialog("是否完善信息？", context, "IntentRegister", infoHandler);
            } else {
                showDialogUtil.selectDialog("请先完善信息", context, infoSelectHandler, true);
            }
        }
    }

    private Handler infoHandler;

    private void infoHandler() {
        infoHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Intent intentBusinessInfo = new Intent(context, BusinessInformationActivity.class);
                        startActivityForResult(intentBusinessInfo, 0);
                        break;
                    case 2:
                        Intent intentDeviceMobilePhone = new Intent(context, DeviceMobilePhoneBinding.class);
                        intentDeviceMobilePhone.putExtra("bind", "loginbind");
                        startActivityForResult(intentDeviceMobilePhone, 0);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private Handler infoSelectHandler;

    private void infoSelectHandler() {
        infoSelectHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        Intent intentBusinessInfo = new Intent(context, BusinessInformationActivity.class);
                        startActivityForResult(intentBusinessInfo, 0);
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    public void onClick(View v) {

    }

    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");
            if ("exceptionalDisconnect".equals(info) || "initiativeDisconnect".equals(info)) {
                device_name_tv.setText("设备已断开");
                device_name_tv.setTextColor(Color.rgb(255, 0, 0));
                Intent stopSer = new Intent(HomeActivity.this, MyService.class);
                Intent clossIntent = new Intent(HomeActivity.this, LoginActivity.class);
                stopService(stopSer);// 停止服务
                startActivity(clossIntent);// 返回登录界面
                finish();
            }
            // 这个监听wifi的连接状态即是否连上了一个有效无线路由，当上边广播的状态是
            // WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
            // 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
            // 当然刚打开wifi肯定还没有连接到有效的无线
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (null != parcelableExtra) {
                    NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                    NetworkInfo.State state = networkInfo.getState();
                    boolean isConnected = state == NetworkInfo.State.CONNECTED;// 当然，这边可以更精确的确定状态
                    if (isConnected) {
                    } else {
                        System.out.println("网路断开");
                    }
                }
            }
        }
    }




}
