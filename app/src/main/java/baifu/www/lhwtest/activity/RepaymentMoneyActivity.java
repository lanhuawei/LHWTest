package baifu.www.lhwtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;

/**
 * Created by Ivan on 2017/7/29.
 * 输入的还款金额页面
 */

public class RepaymentMoneyActivity extends BaseActivity {

    private TextView bank_name_tv, bank_type_tv, bank_num_tv;
    private String bank_name, bank_type, bank_num;
    private EditText repayment_money_et,net_amount_et;
    private ImageView bank_head_iv;
    private MyBroadcast myService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBroadcast();
        initView();

    }

    private void initView() {
        bank_name = getIntent().getExtras().get("bank_name_tv").toString();
        bank_num = getIntent().getExtras().get("bank_num_tv").toString();
        bank_type = getIntent().getExtras().get("bank_type_tv").toString();
        bank_num = bank_num.substring(bank_num.length()-4, bank_num.length());
        bank_name_tv = (TextView) this.findViewById(R.id.bank_name_tv);
        bank_num_tv = (TextView) this.findViewById(R.id.bank_num_tv);
        bank_type_tv =	(TextView) this.findViewById(R.id.bank_type_tv);
        bank_head_iv = (ImageView) this.findViewById(R.id.head_iv);
        repayment_money_et = (EditText) this.findViewById(R.id.repayment_money_et);
        net_amount_et = (EditText) this.findViewById(R.id.net_amount_et);
        net_amount_et.setEnabled(false);
        bank_name_tv.setText(bank_name);
        bank_num_tv.setText("尾数:"+bank_num);
        if(bank_type.equals("0")){
            bank_type_tv.setText("借记卡");
        }else{
            bank_type_tv.setText("信用卡");
        }
        setHead(bank_name);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_repayment_activity;
    }

    /**
     * text改变
     */
    private void sdf() {
        repayment_money_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if(arg0.length()!=0&&Double.parseDouble(arg0.toString())>2){
                    BigDecimal n = new BigDecimal(arg0.toString());
                    BigDecimal interest = new BigDecimal(0.0049);
                    BigDecimal minimum = new BigDecimal(2);
                    BigDecimal money = n.subtract((n.multiply(interest)).add(minimum));
                    BigDecimal s4 = money.setScale(2,BigDecimal.ROUND_DOWN);
                    if(money.compareTo(s4)==1){
                        s4.add(new BigDecimal("0.01"));
                    }
                    net_amount_et.setText(s4.toString());
                }else{
                    net_amount_et.setText("0");
                }
            }
        });

    }


    /** 设置银行图标*/
    private void setHead(String bankName) {
        if(bankName.equals("招商银行")){
            bank_head_iv.setImageResource(R.mipmap.zhaoshang);
        }else if(bankName.equals("工商银行")){
            bank_head_iv.setImageResource(R.mipmap.gongshang);
        }else if(bankName.equals("建设银行")){
            bank_head_iv.setImageResource(R.mipmap.jianshe);
        }else if(bankName.equals("广发银行")){
            bank_head_iv.setImageResource(R.mipmap.guangfa);
        }else if(bankName.equals("华夏银行")){
            bank_head_iv.setImageResource(R.mipmap.huaxia);
        }else if(bankName.equals("农业银行")){
            bank_head_iv.setImageResource(R.mipmap.nongye);
        }else if(bankName.equals("兴业银行")){
            bank_head_iv.setImageResource(R.mipmap.xingye);
        }else if(bankName.equals("光大银行")){
            bank_head_iv.setImageResource(R.mipmap.guangda);
        }else if(bankName.equals("交通银行")){
            bank_head_iv.setImageResource(R.mipmap.jiaotong);
        }else if(bankName.equals("民生银行")){
            bank_head_iv.setImageResource(R.mipmap.minsheng);
        }else if(bankName.equals("邮政储蓄银行")){
            bank_head_iv.setImageResource(R.mipmap.youzheng);
        }else if(bankName.equals("中信银行")){
            bank_head_iv.setImageResource(R.mipmap.zhongxin);
        }else if(bankName.equals("人民银行")){
            bank_head_iv.setImageResource(R.mipmap.renmin);
        }else{//银联
            bank_head_iv.setImageResource(R.mipmap.yinlian);
        }
    }

    /**
     * 按钮点击事件
     * @param v
     */
    public void RepaymentMoneyOnClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.ok_btn:
                Toast.makeText(RepaymentMoneyActivity.this,
                        "尽请期待", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }


    /**
     * 开启接受广播
     */
    private void onBroadcast() {
        myService = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.great.activity.RepaymentMoneyActivity");
        filter.addAction("org.great.activity.All");
        registerReceiver(myService, filter);//注册
        Intent intent = new Intent(RepaymentMoneyActivity.this,MyService.class);
        startService(intent);
    }


    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");
            if("exceptionalDisconnect".equals(info)||"initiativeDisconnect".equals(info)){
                finish();//设备断开关闭界面
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
