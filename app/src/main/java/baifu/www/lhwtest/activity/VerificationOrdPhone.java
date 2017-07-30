package baifu.www.lhwtest.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

import baifu.www.lhwtest.R;
import baifu.www.lhwtest.internet.ConnServerPost;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.utils.ByteUtils;
import baifu.www.lhwtest.utils.CountdownAsyncTask;
import baifu.www.lhwtest.utils.Utility;
import baifu.www.lhwtest.utils.showDialogUtil;

/**
 * Created by Ivan on 2017/7/30.
 * 更换手机号绑定页面，之后进入绑定手机号页面
 */

public class VerificationOrdPhone extends BaseActivity implements View.OnClickListener{

    private Context context = VerificationOrdPhone.this;
    private String tga = "VerificationOrdPhone";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initInfo();
        myHandler();
    }

    private TextView device_number_tv,get_codeKey_tv;
    private EditText phone_number_et,code_et;
    private Button submit_binding_btn;
    private ImageView back_iv;
    private void initView() {
        TextView replace_title = (TextView) findViewById(R.id.replace_title_tv);
        replace_title.setText("验证原手机号");
        device_number_tv = (TextView) findViewById(R.id.device_number_tv);
        phone_number_et = (EditText) findViewById(R.id.phone_number_et);
        code_et = (EditText) findViewById(R.id.code_et);
        code_et.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});//长度4
        get_codeKey_tv = (TextView) findViewById(R.id.get_codeKey_tv);
        submit_binding_btn = (Button) findViewById(R.id.submit_binding_btn);
        submit_binding_btn.setText("下一步");
        back_iv = (ImageView) findViewById(R.id.back_iv);
        submit_binding_btn.setOnClickListener(this);
        get_codeKey_tv.setOnClickListener(this);
        back_iv.setOnClickListener(this);
    }

    private Bundle bundle;
    private void initInfo(){
        bundle = getIntent().getExtras();
        String deviceNumber = bundle.getString("deviceNumber");
        String phoneNumber = bundle.getString("phoneNumber");
        if(!TextUtils.isEmpty(deviceNumber)){
            device_number_tv.setText(deviceNumber);
        }else{
            device_number_tv.setHint("无需设备号");
        }
        phone_number_et.setText(phoneNumber);
        phone_number_et.setFocusable(false);//不可编辑
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_replace_phone_number;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_binding_btn:
                if(TextUtils.isEmpty(code_et.getText().toString())){
                    Toast.makeText(context, "请输入验证码", Toast.LENGTH_SHORT).show();
                }else{//判断格式
                    showDialogUtil.startProgressDialog("校验验证码...", context);
                    isCode(phone_number_et.getText().toString(), code_et.getText().toString());
                }
                break;
            case R.id.back_iv:
                finish();
                break;
            case R.id.get_codeKey_tv:// 获取验证码
                String phoneNumberStr = phone_number_et.getText().toString();
                if (TextUtils.isEmpty(phoneNumberStr)) {
                    showDialogUtil.showDialog("请输入手机号", context);
                } else {
                    if (!phoneNumberStr.matches("^1[3|5|7|8|][0-9]{9}$")) {// 电话是否为空
                        Toast.makeText(context, "手机号格式错误", Toast.LENGTH_SHORT).show();
                    } else {
                        showDialogUtil.startProgressDialog("", context);
                        MessageAuthenticationCode(phoneNumberStr, "wsb_oldphone");//旧手机验证码wsb_oldphone
                    }
                }
                break;
            default:
                break;
        }
    }

    /**判断验证码*/
    private void isCode(String phone, String captcha) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("phone", phone));
        params.add(new BasicNameValuePair("operation_type", "wsb_oldphone"));
        params.add(new BasicNameValuePair("captcha", captcha));
        String sign = ByteUtils.JoiningTogether(params);
        params.add(new BasicNameValuePair("sign", ByteUtils.MD5(ByteUtils.MD5(sign))));
        String url = IpAddress.CaptchaCheck;
        ConnServerPost csp = new ConnServerPost(url, myHandler, "CaptchaCheck",params);
        new Thread(csp).start();
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

    private Handler myHandler;
    private void myHandler() {
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                String data = msg.getData().getString("data");
                if (msg.obj.equals("getCaptcha")) {// 获取短信验证码
                    showDialogUtil.stopProgressDialog();
                    if (data.equals("Timeout")) {//超时
                        showDialogUtil.showDialog("获取验证码超时，请检查网络后重试", context);
                    } else {
                        JSONObject j = Utility.jsonObjectData(context,data);
                        String code = j.getString("code");
                        if ("1000".equals(code)) {
                            //读秒
                            CountdownAsyncTask cdat = new CountdownAsyncTask(get_codeKey_tv,context);
                            cdat.execute(1000);
                        } else {
                            //提示错误信息
                            showDialogUtil.showDialog(j.getString("info"), context);
                            Log.i(tga+new Throwable().getStackTrace()[0].getLineNumber(),j.getString("info"));
                        }
                    }
                }else if("CaptchaCheck".equals(msg.obj)){//判断验证码
                    showDialogUtil.stopProgressDialog();
                    if (data.equals("Timeout")) {
                        showDialogUtil.showDialog("判断验证码超时，请检查网络后重试", context);
                    } else {
                        Log.i(tga+new Throwable().getStackTrace()[0].getLineNumber(),data);
                        JSONObject j = Utility.jsonObjectData(context,data);
                        String code = j.getString("code");
                        if ("1000".equals(code)) {
                            Intent intent = new Intent(context,ReplecePhoneNumber.class);
                            bundle.putString("ordCode",code_et.getText().toString());
                            intent.putExtras(bundle);
                            startActivityForResult(intent,0);
                        } else {
                            showDialogUtil.showDialog(j.getString("info"), context);
                            Log.i(tga+new Throwable().getStackTrace()[0].getLineNumber(),j.getString("info"));
                        }
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if(data.getStringExtra("finish") != null){
//				if(!MyApplication.getInstance().isDeviceConnState()){
                Intent i = new Intent(context,DeviceMobilePhoneBinding.class);
                i.putExtra("finish", "true");
                setResult(RESULT_OK, i);
                finish();
//				}else{
//					finish();
//				}
            }
        }
    }
}
