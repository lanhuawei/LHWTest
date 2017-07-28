package baifu.www.lhwtest.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import baifu.www.lhwtest.BankName;
import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.CardNumder;
import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.entity.BankCard;
import baifu.www.lhwtest.entity.BankCardInfo;
import baifu.www.lhwtest.entity.BankCardView;
import baifu.www.lhwtest.entity.City;
import baifu.www.lhwtest.entity.CityName2;
import baifu.www.lhwtest.internet.ConnServer;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.utils.ByteUtils;
import baifu.www.lhwtest.utils.Utility;
import baifu.www.lhwtest.utils.showDialogUtil;
import baifu.www.lhwtest.wheel.ArrayWheelAdapter;
import baifu.www.lhwtest.wheel.OnWheelChangedListener;
import baifu.www.lhwtest.wheel.WheelView;

/**
 * Created by Ivan.L on 2017/7/28.
 * 添加 银行卡实名认证
 */

public class CertificationActivity extends BaseActivity {

    private Context context = CertificationActivity.this;
    private EditText branch_name_et, card_type_et, select_bank_card_et, card_number_et, select_region_et,
            branch_phone_et;
    private List<String> category_str1;
    private List<List<String>> category_str2;
    private Handler myHandler;
    private String temp = "Certification";
    private String deviceUnique;// 设备唯一吗
    private String cardId;
    private int cardTp;// 银行卡类型0-借记卡2-信用卡
    private String bankName = null;// 银行名称
    private String province = null;// 开户行省份
    private String city = null;// 开户市
    private String branchCode = null;// 支行号
    private String branchName = null;// 支行名称
    private String cardPhone;
    private MyBroadcast myService;
    private ImageView card_number_iv, branch_phone_iv;
    private String cardSimple;
    private BankCardInfo bi = new BankCardInfo();
    private List<BankCardInfo> bankCardInfo;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_certification_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
//        银行卡号每隔四位数一个空格
        myListener();
//        选择地区
        selectRegion();
//        获取城市信息
        getRegionData();
//        开启接收广播
        onBroadcast();
        MyHandler();
    }

    private LinearLayout branch_ll, select_region_ll;
    /**
     * 初始化操作
     */
    private void initView() {
        bankCardInfo = BaseApplication.getInstance().getBankCardInfo();
        if (BaseApplication.getInstance().isDeviceConnState()) {
            deviceUnique = BaseApplication.getInstance().getSN();
        } else {
            deviceUnique = BaseApplication.getInstance().getNoDeviceSN();
        }
        card_type_et = (EditText) findViewById(R.id.card_type_et);
        select_bank_card_et = (EditText) findViewById(R.id.select_bank_card_et);
        select_region_et = (EditText) findViewById(R.id.select_region_et);

        card_number_et = (EditText) findViewById(R.id.card_number_et);
        card_number_et.addTextChangedListener(new TextChanged(card_number_et));
        card_number_et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(23) });

        card_number_iv = (ImageView) findViewById(R.id.card_number_iv);
        branch_name_et = (EditText) findViewById(R.id.branch_name_et);
        branch_phone_et = (EditText) findViewById(R.id.branch_phone_et);
        branch_phone_et.addTextChangedListener(new TextChanged(branch_phone_et));
        branch_phone_et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(11) });
        branch_phone_iv = (ImageView) findViewById(R.id.branch_phone_iv);
        branch_ll = (LinearLayout) findViewById(R.id.branch_ll);// 隐藏支行
        select_region_ll = (LinearLayout) findViewById(R.id.select_region_ll);// 隐藏选择地区
        card_number_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                card_number_et.setText("");
                card_number_iv.setVisibility(View.GONE);
            }
        });
        branch_phone_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                branch_phone_et.setText("");
                branch_phone_iv.setVisibility(View.GONE);
            }
        });
    }


    /**
     * 认证连接，用户点击判断中用到
     */
    private void CertificationConn() {
        cardPhone = branch_phone_et.getText().toString();
        cardId = card_number_et.getText().toString().replace(" ", "");
        if (cardId.equals("")) {
            Toast.makeText(context, "请输入银行卡号", Toast.LENGTH_SHORT).show();
        } else if (!cardId.matches("^\\d{1,20}$")) {
            Toast.makeText(context, "银行卡号格式非法", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(select_bank_card_et.getText())) {
            Toast.makeText(context, "请选择银行", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(card_type_et.getText())) {
            Toast.makeText(context, "请选择银行卡类型", Toast.LENGTH_SHORT).show();
        } else {
            if (cardTp == 0) {// 借记卡
                if (TextUtils.isEmpty(select_region_et.getText())) {// 地区不得为空
                    Toast.makeText(context, "请选择地区", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(branch_name_et.getText())) {
                    Toast.makeText(context, "请选择支行", Toast.LENGTH_SHORT).show();
                } else if (cardPhone.equals("") || cardPhone == null) {
                    Toast.makeText(context, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else if (!cardPhone.matches("^1[3|5|7|8|][0-9]{9}$")) {
                    Toast.makeText(context, "手机号格式非法", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogUtil.startProgressDialog("提交中...", context);
                    List<String> l = getData();
                    String url = JoiningTogetherGet(IpAddress.BankCardBinding, l);
                    ConnServer conn = new ConnServer(url, myHandler, temp);
                    new Thread(conn).start();
                }
            } else {// 信用卡
                if (cardPhone.equals("") || cardPhone == null) {
                    Toast.makeText(context, "手机号不能为空", Toast.LENGTH_SHORT).show();
                } else if (!cardPhone.matches("^1[3|5|7|8|][0-9]{9}$")) {
                    Toast.makeText(context, "手机号格式非法", Toast.LENGTH_SHORT).show();
                } else {
                    showDialogUtil.startProgressDialog("提交中...", context);
                    List<String> l = getData();
                    String url = JoiningTogetherGet(IpAddress.BankCardBinding, l);
                    ConnServer conn = new ConnServer(url, myHandler, temp);
                    new Thread(conn).start();
                }
            }
        }
    }

    /**
     * 获取数据
     * @return
     */
    private List<String> getData() {
        List<String> a = new ArrayList<String>();// 用来存储预发送数据
        a.add("deviceUnique=" + deviceUnique);
        if (cardTp == 0) {
            a.add("province=" + province);
            a.add("city=" + city);
        }
        if (cardSimple != null)
            a.add("cardSimple=" + cardSimple);
        if (bankName != null)
            a.add("bankName=" + bankName);
        if (cardPhone != null)
            a.add("cardPhone=" + cardPhone);
        if (cardId != null)
            a.add("cardId=" + cardId);
        if (branchName != null)
            a.add("branchName=" + branchName);
        if (branchCode != null)
            a.add("branchCode=" + branchCode);
        a.add("cardTp=" + cardTp);
        a.add("version=" + 2);

        return a;
    }

    /**
     * get请求拼接,用与拼接url
     * @param url
     * @param s
     * @return
     */
    private String JoiningTogetherGet(String url, List<String> s) {
        for (int i = 0; i < s.size(); i++) {
            url += "&" + s.get(i);
        }
        return url;
    }


    /**
     * 点击事件判断
     * @param v
     */
    public void CertificationOnClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.card_type_et:
                cardType();
                break;
            case R.id.select_bank_card_et:
                Intent bankCardIntent = new Intent(context, BankCardLiatViewActivity.class);
                bankCardIntent.putExtra("type", 0);
                startActivityForResult(bankCardIntent, 0);
                break;
            case R.id.branch_name_et:
                if (bankName != null) {
                    if (province != null) {
                        Intent branchIntent = new Intent(context, BankCardLiatViewActivity.class);
                        branchIntent.putExtra("type", 1);
                        branchIntent.putExtra("bankName", bankName);// 银行名
                        branchIntent.putExtra("city", city);// 银行所在市
                        startActivityForResult(branchIntent, 0);
                    } else {
                        Toast.makeText(this, "请先选择开户地址", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请先选择开户行", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.submit_btn:
                CertificationConn();
                break;
        }
    }

    /**
     * 银行卡类型
     */
    private void cardType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("类型");
        builder.setMessage("请选择银行卡类型");
        builder.setPositiveButton("借记卡", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                card_type_et.setText("借记卡");
                cardTp = 0;
                branch_name_et.setFocusable(true);
                branch_name_et.setEnabled(true);
                branch_name_et.setHint("请选择支行");
                branch_ll.setVisibility(View.VISIBLE);
                select_region_ll.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("信用卡", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                card_type_et.setText("信用卡");
                branch_name_et.setFocusable(false);
                branch_name_et.setEnabled(false);
                branch_name_et.setText("");
                branch_name_et.setHint("信用卡无支行");
                branchName = null;
                branchCode = null;
                cardTp = 2;
                branch_ll.setVisibility(View.GONE);
                select_region_ll.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
        builder.show();
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
            if (e == card_number_et) {
                if (TextUtils.isEmpty(card_number_et.getText())) {
                    card_number_iv.setVisibility(View.GONE);
                    select_bank_card_et.setText("");
                    branch_name_et.setText("");
                    branchName = null;
                    bankName = null;
                } else {
                    String a = arg0.toString().replace(" ", "");
                    if (a.length() == 6 || a.length() == 8) {
                        String c = CardBin(a);
                        if (null != c && !"null".equals(c) && !"".equals(c)) {
                            select_bank_card_et.setText(c);
                            bankName = c;
                        }
                    }
                }
            } else if (e == branch_phone_et && TextUtils.isEmpty(branch_phone_et.getText())) {
                branch_phone_iv.setVisibility(View.GONE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            if (e == card_number_et) {
                card_number_iv.setVisibility(View.VISIBLE);
            } else if (e == branch_phone_et) {
                branch_phone_iv.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 卡bin
     * @param cardNum
     * @return
     */
    private String CardBin(String cardNum) {
        String bankHereinafterReferredToAs = null;// 银行简称
        String cankName = null;// 银行名称
        if (CardNumder.cardBin.contains(cardNum)) {
            System.out.println(CardNumder.cardBin.indexOf(cardNum));
            System.out.println(BankName.bankName.get(CardNumder.cardBin.indexOf(cardNum)));
            cankName = BankName.bankName.get(CardNumder.cardBin.indexOf(cardNum));
            if (cankName != null && !"".equals(cankName)) {
                bankHereinafterReferredToAs = CardNumder.BankHereinafterReferredToAs.get(cankName);
            }
            if (bankHereinafterReferredToAs != null && !"".equals(bankHereinafterReferredToAs)) {
                cardSimple = bankHereinafterReferredToAs;
                return cankName;
            }
        }
        return null;
    }


    /**
     * 开启接受广播
     */
    private void onBroadcast() {
        myService = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.great.activity.CertificationActivity");
        filter.addAction("org.great.activity.All");
        registerReceiver(myService, filter);
        Intent intent = new Intent(context, MyService.class);
        startService(intent);
    }

    /**
     * 广播
     */
    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");
            if ("exceptionalDisconnect".equals(info) || "initiativeDisconnect".equals(info)) {
                finish();// 设备断开关闭界面
            }
        }
    }

    /**
     * 事件监听
     */
    private void myListener() {
        // 每输入四个数字一个空格
        card_number_et.addTextChangedListener(new TextWatcher() {
            private StringBuffer buffer = new StringBuffer();
            int onTextLength = 0;
            int beforeTextLength = 0;
            boolean isChanged = false;
            int konggeNumberB = 0;
            int location = 0;// 记录光标的位置
            private char[] tempChar;

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // 输入状态
                onTextLength = arg0.length();
                buffer.append(arg0.toString());
                if (onTextLength == beforeTextLength || onTextLength <= 3 || isChanged) {
                    isChanged = false;
                    return;
                }
                isChanged = true;
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // 输入文本之前的状态
                beforeTextLength = arg0.length();
                if (buffer.length() > 0) {
                    buffer.delete(0, buffer.length());
                }
                konggeNumberB = 0;
                for (int i = 0; i < arg0.length(); i++) {
                    if (arg0.charAt(i) == ' ') {
                        konggeNumberB++;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                if (isChanged) {
                    location = card_number_et.getSelectionEnd();
                    int index = 0;
                    while (index < buffer.length()) {
                        if (buffer.charAt(index) == ' ') {
                            buffer.deleteCharAt(index);
                        } else {
                            index++;
                        }
                    }

                    index = 0;
                    int konggeNumberC = 0;
                    while (index < buffer.length()) {
                        if (index == 4 || index == 9 || index == 14 || index == 19) {
                            buffer.insert(index, ' ');
                            konggeNumberC++;
                        }
                        index++;
                    }

                    if (konggeNumberC > konggeNumberB) {
                        location += (konggeNumberC - konggeNumberB);
                    }

                    tempChar = new char[buffer.length()];
                    buffer.getChars(0, buffer.length(), tempChar, 0);
                    String str = buffer.toString();
                    if (location > str.length()) {
                        location = str.length();
                    } else if (location < 0) {
                        location = 0;
                    }
                    card_number_et.setText(str);
                    Editable etable = card_number_et.getText();
                    Selection.setSelection(etable, location);
                    isChanged = false;
                }
            }
        });
    }

    /**
     * 选择地区
     */
    private void selectRegion() {
        select_region_et.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View arg0) {
                final AlertDialog dialog = new AlertDialog.Builder(context).create();
                dialog.setTitle("选择城市：");
                // 创建布局
                final LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL); // 设置布局方式：水平
                final WheelView category1 = new WheelView(context);
                category1.setVisibleItems(5); // category1.setCyclic(true);
                category1.setAdapter(new ArrayWheelAdapter(category_str1));// 大项
                final WheelView category2 = new WheelView(context);
                category2.setVisibleItems(5);
                // category2.setCyclic(true);
                category2.setAdapter(new ArrayWheelAdapter(category_str2.get(0)));// 子项初始化
                // 创建参数
                LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp1.gravity = Gravity.LEFT;
                lp1.weight = (float) 0.5;
                LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                lp2.weight = (float) 0.5;
                lp2.gravity = Gravity.RIGHT;
                // lp2.leftMargin = 10;
                ll.addView(category1, lp1);
                ll.addView(category2, lp2);
                // 为category1添加监听
                category1.addChangingListener(new OnWheelChangedListener() {
                    public void onChanged(WheelView wheel, int oldValue, int newValue) {
                        category2.setAdapter(new ArrayWheelAdapter(category_str2.get(newValue)));
                        category2.setCurrentItem(0);
                    }
                });
                // 为会话创建确定按钮
                dialog.setButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (category1.getCurrentItem() == 0) {
                            select_region_et.setText("");
                        } else {
                            String cat1 = category_str1.get(category1.getCurrentItem());
                            String cat2 = category_str2.get(category1.getCurrentItem()).get(category2.getCurrentItem());
                            if (cat2.equals(city)) {
                                if (!cat1.equals(province)) {
                                    province = cat1;
                                    city = cat2;
                                    branch_name_et.setText("");
                                }
                            } else {
                                province = cat1;
                                city = cat2;
                                branch_name_et.setText("");
                            }
                            select_region_et.setText(cat1 + "-" + cat2);
                        }
                        dialog.dismiss();
                    }
                });
                dialog.setButton2("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setView(ll);
                dialog.show();
            }
        });
    }

    /**
     * 获取城市信息
     */
    private void getRegionData() {
        try {
            BufferedInputStream bis = new BufferedInputStream(getAssets().open("city.txt"));
            byte[] buffer = new byte[1024];
            int d = -1;
            String chunk = "";
            while ((d = bis.read(buffer)) != -1) {
                chunk += new String(buffer, 0, d);
            }
            System.out.println(chunk);
            City city = new Gson().fromJson(chunk, City.class);
            category_str1 = new ArrayList<String>();
            category_str2 = new ArrayList<List<String>>();
            List<CityName2> city1 = new ArrayList<CityName2>();
            for (int i = 0; i < city.getResult().size(); i++) {
                if (i == 0) {
                    List<String> l = new ArrayList<String>();
                    l.add("");
                    category_str1.add("请选择");
                    category_str2.add(l);
                }
                category_str1.add(city.getResult().get(i).getOrgName());
                if ((city1 = city.getResult().get(i).getCity()) == null) {
                    List<String> l = new ArrayList<String>();
                    l.add(city.getResult().get(i).getOrgName());
                    category_str2.add(l);
                } else {
                    List<String> l = new ArrayList<String>();
                    for (int j = 0; j < city1.size(); j++) {
                        l.add(city1.get(j).getOrgName());
                    }
                    category_str2.add(l);
                }
            }
            selectRegion();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    private void MyHandler() {
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj.equals(temp)) {
                    showDialogUtil.stopProgressDialog();
                    String message = msg.getData().get("data").toString();
                    BankCard bc = new Gson().fromJson(message, BankCard.class);
                    String code = bc.getCode();
                    if (code.equals("1000")) {
//						bi.setBankName(bankName);
//						bi.setCardId(cardId);
//						bi.setCardTp(String.valueOf(cardTp));
//						bi.setIf_record("0");
//						bi.setPhone(cardPhone);
                        // bi.setIs_quickpay(is_quickpay);

                        // bindQuickpay(cardId,String.valueOf(cardTp),bankName,cardPhone,deviceUnique);
                        showDialogUtil.showDialog(bc.getInfo(), context, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                finish();
                            }
                        });
                        // bindQuickpay(bi.getCardId(), deviceUnique);
                    } else {
                        showDialogUtil.showDialog(bc.getInfo(), context);
                    }
                } else if (msg.obj.equals("QuickPayCardBind")) {
                    String strResult = msg.getData().get("data").toString();
                    Log.i("qianbao", "快捷绑定：" + strResult);
                    if (strResult.equals("Timeout")) {
                        bi.setIs_quickpay("0");
                        bankCardInfo.add(bi);
                        showDialogUtil.stopProgressDialog();
                        Toast.makeText(context, "连接超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jo = Utility.jsonObjectData(context, strResult);
                        String code = jo.getString("code");
                        String info = jo.getString("info");
                        if (code.equals("1000")) {
                            bi.setIs_quickpay("1");
                            bankCardInfo.add(bi);
                            showDialogUtil.stopProgressDialog();
                            // showUtil.showDialog(info, context);
                            showDialogUtil.showDialog(info, context, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    finish();
                                }
                            });
                        } else {
                            bi.setIs_quickpay("0");
                            bankCardInfo.add(bi);
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog(info, context);
                        }
                    }
                } else if (msg.obj.equals("getBank")) {
                    showDialogUtil.stopProgressDialog();
                    String message = msg.getData().get("data").toString();
                    Log.i("qianbao", "银行卡：" + message);
                    if (message.equals("Timeout")) {// 超时
                        Toast.makeText(context, "初始化数据超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    } else {
                        BankCardView bc = new Gson().fromJson(message, BankCardView.class);
                        BaseApplication.getInstance().setBankCardInfo(bc.getResult());
                    }
                } else if (msg.obj.equals("ApiBindBankCard")) {
                    String strResult = msg.getData().get("data").toString();
                    Log.i("qianbao", "快捷绑定：" + strResult);
                    if (strResult.equals("Timeout")) {
                        showDialogUtil.stopProgressDialog();
                        Toast.makeText(context, "连接超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jo = Utility.jsonObjectData(context, strResult);
                        String code = jo.getString("code");
                        if (code.equals("1000")) {
                            showDialogUtil.stopProgressDialog();
                            try {
                                JSONObject jo1 = jo.getJSONObject("info");
                                JSONObject jo2 = jo.getJSONObject("result");
                                String temporderId = jo2.getString("orderId");
                                String tempdeviceUnique = jo2.getString("deviceUnique");
                                String info = jo1.getString("url");
                                info = java.net.URLDecoder.decode(info, "utf-8");
                                Uri uri = Uri.parse(info);
                                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                try {
                                    Thread.sleep(1 * 1000);
                                    deleteLink(temporderId, tempdeviceUnique);
                                    showDialogUtil.showDialog("绑卡成功", context, new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            super.handleMessage(msg);
                                            finish();
                                        }
                                    });
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } catch (UnsupportedEncodingException e) {
                                showDialogUtil.showDialog("绑卡失败", context);
                            }
                        } else {
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog(jo.getString("info"), context);
                        }
                    }
                }
            }
        };
    }

    /**
     * 删除缓存链接接口
     * @param orderId
     * @param deviceUnique
     */
    private void deleteLink(String orderId, String deviceUnique) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("orderId", orderId));
        params.add(new BasicNameValuePair("deviceUnique", deviceUnique));
        String sign = ByteUtils.JoiningTogether(params);
        params.add(new BasicNameValuePair("sign",
                ByteUtils.MD5(ByteUtils.MD5(sign) + BaseApplication.getInstance().getCommand())));
        String mUrl = IpAddress.DeleteBindLink + "?deviceUnique=" + deviceUnique + "&orderId=" + orderId + "&sign="
                + ByteUtils.MD5(ByteUtils.MD5(sign) + BaseApplication.getInstance().getCommand());
        Log.i("qianbao", "murl:" + mUrl);
        ConnServer cs = new ConnServer(mUrl, myHandler, "DeleteBindLink");
        new Thread(cs).start();
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
