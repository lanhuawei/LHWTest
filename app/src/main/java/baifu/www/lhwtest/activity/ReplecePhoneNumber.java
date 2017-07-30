package baifu.www.lhwtest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.internet.ConnServerPost;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.utils.ByteUtils;
import baifu.www.lhwtest.utils.CountdownAsyncTask;
import baifu.www.lhwtest.utils.Utility;
import baifu.www.lhwtest.utils.showDialogUtil;

/**
 * Created by Ivan on 2017/7/30.
 * 绑定新的手机号
 */

public class ReplecePhoneNumber extends BaseActivity implements View.OnClickListener{

    private Context context = ReplecePhoneNumber.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        myHandler();
    }

    private Bundle bundle;
    private ImageView phoneNumberIV, code_etIV;
    private EditText phoneNumber;// 输入手机号edittext
    private EditText code_et;// 输入验证码edittext
    private TextView getCodeKey;//获取验证码
    private void initView() {
        bundle = getIntent().getExtras();
        TextView replace_title = (TextView) findViewById(R.id.replace_title_tv);
        replace_title.setText("绑定新手机号");
        LinearLayout device_number_ll = (LinearLayout) findViewById(R.id.device_number_ll);
        device_number_ll.setVisibility(View.GONE);
        Button submit_binding = (Button) findViewById(R.id.submit_binding_btn);
        submit_binding.setText("提交信息");
        submit_binding.setOnClickListener(this);

        phoneNumber = (EditText) findViewById(R.id.phone_number_et);
        code_et = (EditText) findViewById(R.id.code_et);
        phoneNumberIV = (ImageView) findViewById(R.id.phone_number_iv);//清空手机号
        code_etIV = (ImageView) findViewById(R.id.code_iv);//清空验证码
        ImageView back_iv = (ImageView) findViewById(R.id.back_iv);
        getCodeKey = (TextView) findViewById(R.id.get_codeKey_tv);

        phoneNumber.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
        code_et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) });
        code_et.addTextChangedListener(new TextChanged(code_et));//验证码监听事件
        phoneNumber.addTextChangedListener(new TextChanged(phoneNumber));
        back_iv.setOnClickListener(this);
        phoneNumberIV.setOnClickListener(this);
        code_etIV.setOnClickListener(this);
        getCodeKey.setOnClickListener(this);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_replace_phone_number;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_number_iv://清空电话号
                phoneNumber.setText("");
                phoneNumberIV.setVisibility(View.GONE);
                break;
            case R.id.code_iv://清空验证码
                code_et.setText("");
                code_etIV.setVisibility(View.GONE);
                break;
            case R.id.back_iv://返回
                finish();//关闭
                break;
            case R.id.submit_binding_btn://提交换帮信息
                String phoneNumberString =phoneNumber.getText().toString();
                String codeStr = code_et.getText().toString();//获取验证码
                if (TextUtils.isEmpty(phoneNumberString)) {//手机号是否为空
                    showDialogUtil.showDialog("请输入手机号", context);
                } else {
                    if (!phoneNumberString.matches("^1[3|5|7|8|][0-9]{9}$")) {// 电话是否为空
                        Toast.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        if (TextUtils.isEmpty(codeStr)) {//验证码是否为空
                            showDialogUtil.showDialog("请输入验证码", context);
                        } else {
                            showDialogUtil.startProgressDialog("", context);
                            submitBinding(phoneNumberString,codeStr);
                        }
                    }
                }
                break;
            case R.id.get_codeKey_tv:// 获取验证码
                String phoneNumberStr = phoneNumber.getText().toString();
                if (TextUtils.isEmpty(phoneNumberStr)) {
                    showDialogUtil.showDialog("请输入手机号", context);
                } else {
                    if (!phoneNumberStr.matches("^1[3|5|7|8|][0-9]{9}$")) {// 电话是否为空
                        Toast.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        showDialogUtil.startProgressDialog("", context);
                        MessageAuthenticationCode(phoneNumberStr, "wsb_newphone");//新手机验证码wsb_newphone
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提交绑定的信息
     * @param phone
     * @param captcha
     */
    private void submitBinding(String phone, String captcha) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("old_captcha",bundle.getString("ordCode")));//原手机号验证码
        if(!TextUtils.isEmpty(bundle.getString("deviceNumber"))){
            params.add(new BasicNameValuePair("deviceUnique",bundle.getString("deviceNumber")));//设备号
        }else if(!TextUtils.isEmpty(BaseApplication.getInstance().getNoDeviceSN())){
            params.add(new BasicNameValuePair("deviceUnique",BaseApplication.getInstance().getNoDeviceSN()));//手机登入 设备号
        }
//		params.add(new BasicNameValuePair("deviceUnique",bundle.getString("deviceNumber")));//设备号
        params.add(new BasicNameValuePair("old_phone",bundle.getString("phoneNumber")));//原手机号
        params.add(new BasicNameValuePair("phone",phone));//与更换手机号
        params.add(new BasicNameValuePair("captcha",captcha));//新手机号验证码
        String sign = ByteUtils.JoiningTogether(params);//拼接
        params.add(new BasicNameValuePair("sign", ByteUtils.MD5(ByteUtils.MD5(sign)+BaseApplication.getInstance().getCommand())));
        String url = IpAddress.ChangePhone;//提交更换手机号地址
        ConnServerPost csp = new ConnServerPost(url, myHandler, "ChangePhone",params);
        new Thread(csp).start();
    }

    /**
     * 短信验证码 注册wsb_reg 网页注册wsb_regweb 登录wsb_login
     */
    private void MessageAuthenticationCode(String phone, String operation_type) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", phone));//添加手机号
        params.add(new BasicNameValuePair("operation_type", operation_type));
        String sign = ByteUtils.JoiningTogether(params);//拼接
        params.add(new BasicNameValuePair("sign", ByteUtils.MD5(ByteUtils.MD5(sign))));//验签
        String url = IpAddress.getCaptcha;// 获取短信验证码
        ConnServerPost csp = new ConnServerPost(url, myHandler, "getCaptcha",params);
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
                    if (data.equals("Timeout")) {//超时
                        showDialogUtil.showDialog("获取验证码超时，请检查网络后重试", context);
                    } else {
                        JSONObject j = Utility.jsonObjectData(context,data);
                        String code = j.getString("code");
                        if ("1000".equals(code)) {
                            CountdownAsyncTask cdat = new CountdownAsyncTask(getCodeKey,context);
                            cdat.execute(1000);
                        } else {
                            showDialogUtil.showDialog(j.getString("info"), context);
                            Log.i("DeviceMobilePhoneBinding-179",j.getString("info"));
                        }
                    }
                } else if (msg.obj.equals("ChangePhone")) {
                    if (data.equals("Timeout")) {//超时
                        showDialogUtil.stopProgressDialog();
                        showDialogUtil.showDialog("上传超时，请检查网络后重试", context);
                    } else {
                        showDialogUtil.stopProgressDialog();
                        JSONObject j = Utility.jsonObjectData(context,data);
                        String code = j.getString("code");
                        if ("1000".equals(code)) {
                            showDialogUtil.showDialog("更换手机号成功，请重新登录", context,new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
//									Intent intent = new Intent(context,VerificationOrdPhone.class);
//									intent.putExtra("finish", "true");
//									setResult(RESULT_OK,intent);
                                    //返回登入页
                                    Intent stopSer = new Intent(ReplecePhoneNumber.this, MyService.class);
                                    Intent intent = new Intent(ReplecePhoneNumber.this, LoginActivity.class);
                                    sendCmd("DeviceDisconnect", 0);
                                    stopService(stopSer);// 停止服务
                                    startActivity(intent);// 返回登录界面
                                    finish();
                                }
                            });
                        } else {
                            showDialogUtil.showDialog(j.getString("info"), context);
                        }
                    }
                }
            }
        };
    }

    public void sendCmd(String value, int i) {
        Intent serviceIntent = new Intent();
        serviceIntent.setAction("org.great.util.MyService");
        serviceIntent.setAction("org.great.activity.All");
        serviceIntent.putExtra("value", value);
        serviceIntent.putExtra("num", i);
        sendBroadcast(serviceIntent);
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
            if (e == phoneNumber && TextUtils.isEmpty(phoneNumber.getText()))
                phoneNumberIV.setVisibility(View.GONE);
            else if (e == code_et && TextUtils.isEmpty(code_et.getText()))
                code_etIV.setVisibility(View.GONE);
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {}

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {
            if (e == phoneNumber)
                phoneNumberIV.setVisibility(View.VISIBLE);
            else if (e == code_et)
                code_etIV.setVisibility(View.VISIBLE);
        }
    }


}
