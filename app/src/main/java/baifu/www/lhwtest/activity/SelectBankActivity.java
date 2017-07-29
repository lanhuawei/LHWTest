package baifu.www.lhwtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.adapter.BankImageAdapter;
import baifu.www.lhwtest.entity.BankCardInfo;

/**
 * Created by Ivan.L on 2017/7/28.
 * 选择欲还款的银行卡
 * 连接设备后可选择
 */

public class SelectBankActivity extends BaseActivity {

    private Context context = SelectBankActivity.this;
    private Handler myHandler;
    private ListView bank_lv;
    private ImageView add_iv;
    private TextView add_tv;
    private String deviceUnique;
    private List<BankCardInfo> bankCardInfo;
    private MyBroadcast myService;
    private BankImageAdapter bia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void initView() {
        bank_lv = (ListView) findViewById(R.id.bank_card_lv);
        add_tv = (TextView) findViewById(R.id.add_tv);
        if (BaseApplication.getInstance().isDeviceConnState()) {// 设备链接
            deviceUnique = BaseApplication.getInstance().getSN();// sn
        } else {
            deviceUnique = BaseApplication.getInstance().getNoDeviceSN();// sn
        }
        add_iv = (ImageView) findViewById(R.id.add_iv);
        add_iv.setVisibility(View.GONE);
        add_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent certificationIntent = new Intent(context, CertificationActivity.class);
                startActivity(certificationIntent);
            }
        });

    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");
            if ("exceptionalDisconnect".equals(info) || "initiativeDisconnect".equals(info)) {
                finish();// 设备断开关闭界面
            }
        }
    }
}
