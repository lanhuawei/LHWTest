package baifu.www.lhwtest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;

/**
 * Created by Ivan.L on 2017/7/28.
 * 手动填写的分行及分行行号
 */

public class CustomBankActivity extends BaseActivity {

    private EditText branch_name_et, branch_number_et;
    private ImageView branch_name_iv, branch_number_iv;
    private MyBroadcast myService;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_custom_bank;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        onBroadcast();
    }


    /**
     * 初始化
     */
    private void initView() {
        branch_name_et = (EditText) findViewById(R.id.branch_name_et);
        branch_name_et.addTextChangedListener(new TextChanged(branch_name_et));
        // branch_name_et.setFilters(new InputFilter[]{new
        // InputFilter.LengthFilter(20)});
        branch_name_iv = (ImageView) findViewById(R.id.branch_name_iv);
        branch_number_et = (EditText) findViewById(R.id.branch_number_et);
        branch_number_et.addTextChangedListener(new TextChanged(
                branch_number_et));
        branch_number_et
                .setFilters(new InputFilter[] { new InputFilter.LengthFilter(12) });
        branch_number_iv = (ImageView) findViewById(R.id.branch_number_iv);
        branch_name_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                branch_name_et.setText("");
                branch_name_iv.setVisibility(View.GONE);
            }
        });
        branch_number_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                branch_number_et.setText("");
                branch_number_iv.setVisibility(View.GONE);
            }
        });
    }

    /**
     * text状态监听
     */
    private class TextChanged implements TextWatcher {
        EditText e;

        public TextChanged(EditText e) {
            this.e = e;
        }

        @Override
        public void afterTextChanged(Editable arg0) {
            if (e == branch_name_et
                    && TextUtils.isEmpty(branch_name_et.getText())) {
                branch_name_iv.setVisibility(View.GONE);
                // branch_name_et.setText("");
            }
            if (e == branch_number_et
                    && TextUtils.isEmpty(branch_number_et.getText())) {
                branch_number_iv.setVisibility(View.GONE);
                // branch_number_et.setText("");
            }

            // 设置输入非数字
            if (e == branch_name_et && !TextUtils.isEmpty(arg0.toString())) {
                if (arg0.toString().contains("0")||arg0.toString().contains("1")||arg0.toString().contains("2")||arg0.toString().contains("3")||arg0.toString().contains("4")||arg0.toString().contains("5")||arg0.toString().contains("6")||arg0.toString().contains("7")||arg0.toString().contains("8")||arg0.toString().contains("9")) {
                    if(arg0.length()>1){
                        StringBuffer sb = new StringBuffer();
                        for(int i=0;i<arg0.length();i++){
                            char cha=arg0.charAt(i);
                            if('0'==cha||'1'==cha||'2'==cha||'3'==cha||'4'==cha||'5'==cha||'6'==cha||'7'==cha||'8'==cha||'9'==cha){
                                continue;
                            }else{
                                sb.append(cha);
                            }
                        }
                        int index=branch_name_et.getSelectionStart();
                        branch_name_et.setText(sb);
                        branch_name_et.setSelection(index-1);
                    }else{
                        branch_name_et.setText("");
                    }

                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
            if (e == branch_name_et) {
                branch_name_iv.setVisibility(View.VISIBLE);
            } else if (e == branch_number_et) {
                branch_number_iv.setVisibility(View.VISIBLE);
            }

        }
    }

    /**
     * 确定，取消按钮，按钮点击事件
     */
    public void CustomBankOnClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_btn:
                Intent i = new Intent(CustomBankActivity.this,
                        BankCardLiatViewActivity.class);
                i.putExtra("BranchName", "");
                i.putExtra("BranchNum", "");
                setResult(RESULT_OK, i);
                finish();
                break;
            case R.id.submit_btn:
                if (!TextUtils.isEmpty(branch_name_et.getText())) {
                    if (!TextUtils.isEmpty(branch_number_et.getText())) {
                        Intent intent = new Intent(CustomBankActivity.this,
                                BankCardLiatViewActivity.class);
                        intent.putExtra("BranchName", branch_name_et.getText()
                                .toString());
                        intent.putExtra("BranchNum", branch_number_et.getText()
                                .toString());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(CustomBankActivity.this, "分行行号不能为空", Toast.LENGTH_SHORT)
                                .show();
                    }
                } else {
                    Toast.makeText(CustomBankActivity.this, "支行名不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
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
        filter.addAction("org.great.activity.CustomBankActivity");
        filter.addAction("org.great.activity.All");
        registerReceiver(myService, filter);
        Intent intent = new Intent(CustomBankActivity.this, MyService.class);
        startService(intent);
    }

    /**
     * 接收广播
     */
    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");
            if ("exceptionalDisconnect".equals(info)
                    || "initiativeDisconnect".equals(info)) {
                finish();// 设备断开关闭界面
            }
        }
    }
}
