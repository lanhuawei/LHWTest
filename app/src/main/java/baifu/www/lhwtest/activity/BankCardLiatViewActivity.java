package baifu.www.lhwtest.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.MyService;
import baifu.www.lhwtest.R;
import baifu.www.lhwtest.entity.BankCard;
import baifu.www.lhwtest.entity.BranchName;
import baifu.www.lhwtest.internet.ConnServer;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.utils.ByteUtils;

/**
 * Created by Ivan.L on 2017/7/28.
 * 添加银行卡时，银行卡列表
 */

public class BankCardLiatViewActivity extends BaseActivity {

    private Context context = BankCardLiatViewActivity.this;
    private Map<String,String> bankCode;
    private Handler myHandler;
    private List<String> mBankCard, mBranchName;
    private Map<String,String>mBranchNum;
    private int type,CardType;
    private ListView bankcard_lv;
    private TextView add_bank_tv;
    private String city, bankName, deviceUnique;
    private SearchView search_bank_sv;
    private MyBroadcast myService;
    private Dialog ad;
    ArrayAdapter<String> stuAdapter;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_bank_card_listview;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadDialog("加载中...");
        initView();
        onBroadcast();
        getBankCode();//获取银行代码
        Handler();
        getBankData();
        myListener();
    }

    /**
     * 初始化
     */
    private void initView() {
        bankcard_lv = (ListView) findViewById(R.id.bankcard_lv);
        bankcard_lv.setTextFilterEnabled(true);
        search_bank_sv = (SearchView) findViewById(R.id.search_bank_sv);
        type = getIntent().getExtras().getInt("type");
        CardType = getIntent().getExtras().getInt("CardType");
        add_bank_tv = (TextView) findViewById(R.id.add_bank_tv);
        if (type == 1) {
            add_bank_tv.setVisibility(View.VISIBLE);
            bankName = getIntent().getExtras().get("bankName").toString();
            city = getIntent().getExtras().get("city").toString();
            System.out.println(city);
            deviceUnique = BaseApplication.getInstance().getSN();// "Q8NL03214200";//
            add_bank_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(
                            BankCardLiatViewActivity.this,CustomBankActivity.class);
                    startActivityForResult(intent,0);
                }
            });
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            if(data.getStringExtra("BranchName").equals("")){
                finish();
            }else{
                String strBankCard=data.getStringExtra("BranchName").toString();
                String strBankNum=data.getStringExtra("BranchNum").toString();
                Intent intent = new Intent(BankCardLiatViewActivity.this,
                        CertificationActivity.class);
                intent.putExtra("BranchName",strBankCard);
                intent.putExtra("BranchNum",strBankNum);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 加载中 dialog
     */
    private void loadDialog(String s) {
        View loadView = getLayoutInflater().inflate(R.layout.load_view, null);
        TextView content_tv = (TextView) loadView.findViewById(R.id.content_tv);
        content_tv.setText("");
        ad = new Dialog(BankCardLiatViewActivity.this,R.style.dialog1);
        ad.setCanceledOnTouchOutside(false);
        ad.setContentView(loadView, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        ad.show();
    }

    /**
     * 未找到支行，手动添加
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("该城市未找到支行，是否手动添加？");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); //
                dialog.cancel();
                Intent intent = new Intent(
                        BankCardLiatViewActivity.this,CustomBankActivity.class);
                startActivityForResult(intent,0);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
                finish();
            }
        });
        builder.show();
    }

    /**
     * 从文件中获取获取银行代码
     */
    private void getBankCode() {
        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(getAssets().open("bank_code.txt"));
            byte[] buffer = new byte[1024];
            int d = -1;
            String chunk = "";
            while ((d = bis.read(buffer)) != -1) {
                chunk += new String(buffer, 0, d);
            }
            bankCode = new Gson().fromJson(chunk,new TypeToken<Map<String,String>>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送请求,获取银行信息
     */
    private void getBankData() {
        String temp;
        String url;
        if (type == 0) {
            url = IpAddress.BankList;
            temp = "getBank";
        } else {
            String sign = ByteUtils.MD5(ByteUtils.MD5("bankName=" + bankName
                    + "&city=" + city+ "&deviceUnique=" + deviceUnique)
                    + BaseApplication.getInstance().getCommand());
            url = IpAddress.BankBranch + "sign=" + sign + "&bankName="
                    + bankName + "&city=" + city + "&deviceUnique="
                    + deviceUnique;
            temp = "getBranch";
        }
        ConnServer conn = new ConnServer(url, myHandler, temp);
        new Thread(conn).start();
    }

    /**
     * 接受返回数据
     */
    private void Handler() {
        myHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.obj.equals("getBank")) {
                    String bank = msg.getData().get("data").toString();
                    if(bank.equals("Timeout")){
                        Toast.makeText(context, "初始化超时，请检查网络重试", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        BankCard bc = new Gson().fromJson(bank, BankCard.class);
                        mBankCard = bc.getResult();
                        mBankCard.add(0,"请选择");
                        stuAdapter = new ArrayAdapter<String>(
                                BankCardLiatViewActivity.this,
                                android.R.layout.simple_list_item_1, mBankCard);
                        bankcard_lv.setAdapter(stuAdapter);
                        ad.dismiss();
                    }
                }if (msg.obj.equals("getBranch")) {
                    String branch = msg.getData().get("data").toString();
                    if(branch.equals("Timeout")){
                        Toast.makeText(context, "获取支行超时，请重试", Toast.LENGTH_SHORT).show();
                    }else{
                        BranchName branchName = null;
                        BankCard bankCard = null;
                        try{
                            branchName = new Gson().fromJson(branch,BranchName.class);
                            mBranchName = traverse(branchName.getResult());
                            mBranchName.add(0,"请选择");
                            mData=mBranchName;
                            mBackData = mData;
//							stuAdapter = new ArrayAdapter<String>(
//									BankCardLiatViewActivity.this,
//									android.R.layout.simple_list_item_1, mBranchName);
//							bankcard_lv.setAdapter(stuAdapter);
                            mAdapter = new MyAdapter();
                            bankcard_lv.setAdapter(mAdapter);
                            ad.dismiss();
                        }catch(JsonParseException e){
                            bankCard = new Gson().fromJson(branch,BankCard.class);
                            if(bankCard.getResult().size()==0){
                                ad.dismiss();
                                showDialog();
                            }
                        }
                    }
                }
            }
        };
    }


    /**
     * 事件监听
     */
    private void myListener() {
//		search_bank_sv.setOnQueryTextListener(new OnQueryTextListener() {
//			@Override
//			public boolean onQueryTextSubmit(String query) {
//				return false;
//			}
//
//			@Override
//			public boolean onQueryTextChange(String newText) {
//				if(TextUtils.isEmpty(newText)){
//					stuAdapter.getFilter().filter("");
//					bankcard_lv.clearTextFilter();
//				}else{
//					stuAdapter.getFilter().filter(newText.toString());
//				}
//				return true;
//			}
//		});
        search_bank_sv.setOnQueryTextListener(new QueryListener());
        bankcard_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(BankCardLiatViewActivity.this,
                        CertificationActivity.class);
                String s = bankcard_lv.getItemAtPosition(arg2).toString();
                if (type == 0) {
                    if(CardType==0){
                        if(s.equals("请选择")){
                            intent.putExtra("BankCard","");
                        }else{
                            if(bankCode.get(s)!=null){
                                intent.putExtra("BankCode", bankCode.get(s));
                            }
                            intent.putExtra("BankCard",s);
                        }
                    }else{
                        if(s.equals("请选择")){
                            intent.putExtra("creditBank","");
                        }else{
                            if(bankCode.get(s)!=null){
                                intent.putExtra("creditCode", bankCode.get(s));
                                System.out.println(bankCode.get(s));
                            }
                            intent.putExtra("creditBank",s);
                        }
                    }
                } else {
                    if(s.equals("请选择")){
                        intent.putExtra("BranchName","");
                        intent.putExtra("BranchNum","");
                    }else{
                        intent.putExtra("BranchName",s);
                        intent.putExtra("BranchNum",mBranchNum.get(s));
                        System.out.println(mBranchNum.get(s));
                    }
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    // 搜索文本监听器
    private class QueryListener implements SearchView.OnQueryTextListener {
        // 当内容被提交时执行
        @Override
        public boolean onQueryTextSubmit(String query) {
            return true;
        }

        // 当搜索框内内容发生改变时执行
        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText)) {
                // mListView.clearTextFilter(); // 清楚ListView的过滤
                mAdapter.getFilter().filter("");
            } else {
                // mListView.setFilterText(newText); // 设置ListView的过滤关键词
                mAdapter.getFilter().filter(newText.toString());
            }
            return true;
        }
    }


    /**
     * 遍历map key value
     * 获取支行 号 和 名
     * @param map
     * @return
     */
    private List<String> traverse(Map<String, String> map) {
        List<String> list = new ArrayList<String>();
        mBranchNum = new HashMap<String,String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            list.add(entry.getValue());
            mBranchNum.put(entry.getValue(), entry.getKey());
//			mBranchNum.add(entry.getKey());
        }
        return list;
    }

    //模糊查询
    private List<String> mData = new ArrayList<String>(); // 这个数据会改变
    private List<String> mBackData; // 这是原始的数据

    private MyAdapter mAdapter;
    // 必须实现Filterable接口
    private class MyAdapter extends BaseAdapter implements Filterable {
        private MyFilter mFilter;

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(BankCardLiatViewActivity.this, R.layout.branch_item, null);
            }
            TextView show = (TextView) convertView.findViewById(R.id.zh);
            show.setText(mData.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (null == mFilter) {
                mFilter = new MyFilter();
            }
            return mFilter;
        }

        // 自定义Filter类
        class MyFilter extends Filter {
            @Override
            // 该方法在子线程中执行
            // 自定义过滤规则
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                List<String> newValues = new ArrayList<String>();
                String filterString = constraint.toString().trim().toLowerCase();

                // 如果搜索框内容为空，就恢复原始数据
                if (TextUtils.isEmpty(filterString)) {
                    newValues = mBackData;
                } else {
                    // 过滤出新数据
                    for (String str : mBackData) {
                        if (-1 != str.toLowerCase().indexOf(filterString)) {
                            newValues.add(str);
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mData = (List<String>) results.values;

                if (results.count > 0) {
                    mAdapter.notifyDataSetChanged(); // 通知数据发生了改变
                } else {
                    mAdapter.notifyDataSetInvalidated(); // 通知数据失效
                }
            }
        }
    }


    /**
     * 点击事件监听
     * @param v
     */
    public void BankCardLiatViewOnClick(View v) {
        switch (v.getId()) {
            case R.id.back_iv:
                finish();
                break;
        }
    }

    /**
     * 开启接受广播
     */
    private void onBroadcast() {
        myService = new MyBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("org.great.activity.BankCardLiatViewActivity");
        filter.addAction("org.great.activity.All");
        registerReceiver(myService, filter);
        Intent intent = new Intent(BankCardLiatViewActivity.this,MyService.class);
        startService(intent);
    }

    /**
     * 广播接收
     */
    public class MyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String info = intent.getStringExtra("value");
            if("exceptionalDisconnect".equals(info)||"initiativeDisconnect".equals(info)){
                finish();//设备断开关闭界面
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myService != null) {
            unregisterReceiver(myService);
        }
    }
}
