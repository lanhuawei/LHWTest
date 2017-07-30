package baifu.www.lhwtest.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.adapter.BankImageAdapter;
import baifu.www.lhwtest.entity.BankCardInfo;
import baifu.www.lhwtest.entity.BankCardView;
import baifu.www.lhwtest.internet.ConnServer;
import baifu.www.lhwtest.internet.ConnServerPost;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.utils.ByteUtils;
import baifu.www.lhwtest.utils.Utility;
import baifu.www.lhwtest.utils.showDialogUtil;

/**
 * Created by Ivan.L on 2017/7/28.
 * 选择欲还款的银行卡
 * 连接设备后可选择
 */

public class SelectBankActivity extends BaseActivity implements BankImageAdapter.MyClickListener{

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
        initView();
        Handler();
        onBroadcast();
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

    private BankCardInfo bi;
    private int selection;

    /**
     * 接收返回信息
     */
    @SuppressLint("HandlerLeak")
    private void Handler() {
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj.equals("QuickPayCardBind")) {
                    String strResult = msg.getData().get("data").toString();
                    Log.i("qianbao", "快捷绑定：" + strResult);
                    if (strResult.equals("Timeout")) {
                        showDialogUtil.stopProgressDialog();
                        Toast.makeText(context, "连接超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jo = Utility.jsonObjectData(context, strResult);
                        String code = jo.getString("code");
                        String info = jo.getString("info");
                        if (code.equals("1000")) {
                            bankCardInfo.get(selection).setIs_quickpay("1");
                            bia.notifyDataSetChanged();
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog(info, context);
                        } else {
                            showDialogUtil.stopProgressDialog();
                            showDialogUtil.showDialog(info, context);
                        }
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
                                    Thread.sleep(1000);
                                    // 删除缓存链接接口
                                    deleteLink(temporderId, tempdeviceUnique);

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
                } else if (msg.obj.equals("getBank")) {
                    showDialogUtil.stopProgressDialog();
                    String message = msg.getData().get("data").toString();
                    Log.i("qianbao", "银行卡：" + message);
                    if (message.equals("Timeout")) {// 超时
                        Toast.makeText(context, "初始化数据超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    } else {
                        BankCardView bc = new Gson().fromJson(message, BankCardView.class);
                        // bankCardInfo = bc.getResult();
                        addBankCard(bc.getResult());
                        // MyApplication.getInstance().setBankCardInfo(bc.getResult());
                    }
                }  else if (msg.obj.equals("getBank2")) {
                    showDialogUtil.stopProgressDialog();
                    String message = msg.getData().get("data").toString();
                    Log.i("qianbao", "银行卡：" + message);
                    if (message.equals("Timeout")) {// 超时
                        Toast.makeText(context, "初始化数据超时，请检查网络后重试", Toast.LENGTH_SHORT).show();
                    } else {
                        BankCardView bc = new Gson().fromJson(message, BankCardView.class);
                        // bankCardInfo = bc.getResult();
                        addBankCard2(bc.getResult());
                        // MyApplication.getInstance().setBankCardInfo(bc.getResult());
                    }
                }else if (msg.obj.equals("DeleteBindLink")) {
                    showDialogUtil.stopProgressDialog();
                    String message = msg.getData().get("data").toString();
                    Log.i("qianbao", "删除接口：" + message);
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
     * 添加银行卡1
     * @param bankCard
     */

    private void addBankCard(List<BankCardInfo> bankCard) {
        // bankCardInfo = MyApplication.getInstance().getBankCardInfo();
        bankCardInfo = bankCard;
        if (null == bankCardInfo || bankCardInfo.size() == 0) {
            return;
        }
        listSort(bankCardInfo);
        bia = new BankImageAdapter(this, bankCardInfo, this);
        bank_lv.setAdapter(bia);
        bia.notifyDataSetChanged();
    }

    /**
     * 添加银行卡2
     * @param bankCard
     */
    private void addBankCard2(List<BankCardInfo> bankCard) {
        // bankCardInfo = MyApplication.getInstance().getBankCardInfo();
        bankCardInfo = bankCard;
        if (null == bankCardInfo || bankCardInfo.size() == 0) {
            return;
        }
        listSort(bankCardInfo);
        bia = new BankImageAdapter(this, bankCardInfo, this);
        bank_lv.setAdapter(bia);
        bia.notifyDataSetChanged();
    }

    /**
     * 获取银行卡
     *
     */
    private void getBankCard() {
        String url = IpAddress.BankCardBindingQuery + "deviceUnique=" + deviceUnique;
        ConnServer conn = new ConnServer(url, myHandler, "getBank");
        new Thread(conn).start();
    }

    /**
     * 获取银行卡
     */
    private void getBankCard2() {
        String url = IpAddress.BankCardBindingQuery + "deviceUnique=" + deviceUnique;
        ConnServer conn = new ConnServer(url, myHandler, "getBank2");
        new Thread(conn).start();
    }

    /**
     * 对银行卡进行排序
     * @param bankCardInfo
     */
    private void listSort(List<BankCardInfo> bankCardInfo) {
        // list排序
        Collections.sort(bankCardInfo, new Comparator<BankCardInfo>() {

            @Override
            public int compare(BankCardInfo arg0, BankCardInfo arg1) {
                if (Integer.parseInt(arg1.getIf_record()) > Integer.parseInt(arg0.getIf_record())) {
                    return 1;
                } else if (Integer.parseInt(arg1.getIf_record()) == Integer.parseInt(arg0.getIf_record())) {
                    if (Integer.parseInt(arg1.getCardTp()) < Integer.parseInt(arg0.getCardTp())) {
                        return 1;
                    } else if (Integer.parseInt(arg1.getCardTp()) == Integer.parseInt(arg0.getCardTp())) {
                        return 0;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    /**
     * 开启接受广播
     */
    private void onBroadcast() {
        myService = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.great.activity.SelectBankActivity");
        filter.addAction("org.great.activity.All");
        registerReceiver(myService, filter);
        Intent intent = new Intent(SelectBankActivity.this, MyService.class);
        startService(intent);
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

    /**
     * 按钮点击事件处理
     * @param v
     */

    public void SelectBankOnClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
            case R.id.add_iv:
                Intent certificationIntent = new Intent(SelectBankActivity.this, CertificationActivity.class);
                startActivity(certificationIntent);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //        获得银行卡
        showDialogUtil.startProgressDialog("", context);
        getBankCard();
    }

    /**
     * 模拟向服务器请求数据
     */
    private void requestDataFromServer(final boolean isLoadingMore) {
        new Thread() {
            public void run() {
                //SystemClock.sleep(3000);// 模拟请求服务器的一个时间长度

                if (isLoadingMore) {
                } else {
                    // list.add(0, "下拉刷新的数据");
                    getBankCard2();
                }
                // 在UI线程更新UI
                // Message msg = new Message();
                // msg.obj = "";
                // myHandler.sendMessage(msg);
            };
        }.start();
    }




    @Override
    protected void onRestart() {
        super.onRestart();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myService != null) {
            unregisterReceiver(myService);
        }
    }



    /**
     * 绑定快捷支付回调
     * @param v
     */
    @Override
    public void clickListener(View v) {
        // 点击之后的操作在这里写
        selection = (Integer) v.getTag();
        bi = bankCardInfo.get(selection);
        Log.i("qianbao", "BI:" + bi.toString());
        if ("0".equals(bi.getPhone()) || TextUtils.isEmpty(bi.getPhone())) {
            // 无手机号
            showDialogUtil.inputDialog("请输入绑定银行卡的手机号", context, new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 1:
                            String phone = (String) msg.obj;
                            Log.i("qianbao", "确定：" + phone);
                            if (TextUtils.isEmpty(phone)) {
                                showDialogUtil.showDialog("请输入手机号", context);
                            } else if (!phone.matches("^1[3|5|7|8|][0-9]{9}$")) {
                                showDialogUtil.showDialog("手机号格式错误", context);
                            } else {
                                // bindQuickpay(bi.getCardId(), bi.getCardTp(),
                                // bi.getBankName(), phone, deviceUnique);
                                showDialogUtil.startProgressDialog("", context);
                                bindQuickpay(bi.getCardId(), deviceUnique);
                            }
                            break;
                    }
                }
            });
        } else {
            showDialogUtil.startProgressDialog("", context);
            bindQuickpay(bi.getCardId(), deviceUnique);
            // bindQuickpay(bi.getCardId(), bi.getCardTp(), bi.getBankName(),
            // bi.getPhone(), deviceUnique);
        }
    }

    /**
     * 快捷支付绑卡接口
     */
    // private void bindQuickpay(String cardId, String cardTp, String bankName,
    // String cardPhone, String deviceUnique) {
    // List<NameValuePair> params = new ArrayList<NameValuePair>();
    // params.add(new BasicNameValuePair("cardId", cardId));
    // params.add(new BasicNameValuePair("cardTp", cardTp));
    // params.add(new BasicNameValuePair("bankName", bankName));
    // params.add(new BasicNameValuePair("cardPhone", cardPhone));
    // params.add(new BasicNameValuePair("deviceUnique", deviceUnique));
    // List<NameValuePair> params1 = new ArrayList<NameValuePair>();
    // params1.add(new BasicNameValuePair("cardId", cardId));
    // params1.add(new BasicNameValuePair("cardPhone", cardPhone));
    // params1.add(new BasicNameValuePair("deviceUnique", deviceUnique));
    // String sign = ByteUtils.JoiningTogether(params1);
    // params.add(new BasicNameValuePair("sign",
    // ByteUtils.MD5(ByteUtils.MD5(sign) +
    // MyApplication.getInstance().getCommand())));
    // String url = IpAddress.QuickPayCardBind;// 快捷支付绑卡接口
    // ConnServerPost csp = new ConnServerPost(url, myHandler,
    // "QuickPayCardBind", params);
    // new Thread(csp).start();
    // }

    private void bindQuickpay(String cardId, String deviceUnique) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("cardId", cardId));
        params.add(new BasicNameValuePair("deviceUnique", deviceUnique));
        String sign = ByteUtils.JoiningTogether(params);
        params.add(new BasicNameValuePair("sign",
                ByteUtils.MD5(ByteUtils.MD5(sign) + BaseApplication.getInstance().getCommand())));
        // url = IpAddress.ApiBindBankCard + "?cardId=" + cardId +
        // "&deviceUnique=" + deviceUnique + "&sign="
        // + ByteUtils.MD5(ByteUtils.MD5(sign) +
        // MyApplication.getInstance().getCommand());// 快捷支付绑卡接口
        String mUrl = IpAddress.ApiBindBankCard;
        ConnServerPost csp = new ConnServerPost(mUrl, myHandler, "ApiBindBankCard", params);
        new Thread(csp).start();
    }

}
