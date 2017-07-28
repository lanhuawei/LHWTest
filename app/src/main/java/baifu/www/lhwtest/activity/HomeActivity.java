package baifu.www.lhwtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.adapter.HomeGridAdapter;
import baifu.www.lhwtest.adapter.HomeGridTempAdapter;
import baifu.www.lhwtest.entity.HomeModel;
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
