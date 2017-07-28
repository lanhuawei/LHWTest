package baifu.www.lhwtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

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
