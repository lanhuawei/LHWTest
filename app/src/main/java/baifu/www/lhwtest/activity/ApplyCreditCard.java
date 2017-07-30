package baifu.www.lhwtest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import baifu.www.lhwtest.R;
import baifu.www.lhwtest.internet.IpAddress;

/**
 * Created by Ivan on 2017/7/29.
 * 申请信用卡
 */

public class ApplyCreditCard extends BaseActivity {

    private Context context = ApplyCreditCard.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_apply_credit_card;
    }
    private void initView() {
        //兴业银行
        findViewById(R.id.societe_generale_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,WebActivity.class);
                intent.putExtra("status", "兴业银行");//标题
                intent.putExtra("url", IpAddress.SocieteGenerale);
                startActivity(intent);
            }
        });
        //交通银行
        findViewById(R.id.bank_of_communications_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,WebActivity.class);
                intent.putExtra("status", "交通银行");
                intent.putExtra("url", IpAddress.BankOfCommunications);
                startActivity(intent);
            }
        });
        findViewById(R.id.back_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
