package baifu.www.lhwtest.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import baifu.www.lhwtest.R;
import baifu.www.lhwtest.activity.RepaymentMoneyActivity;
import baifu.www.lhwtest.entity.BankCardInfo;

/**
 * Created by Ivan.L on 2017/7/28.
 * 银行卡布局页面设配器
 */

public class BankImageAdapter extends BaseAdapter implements View.OnClickListener{

    //	private ArrayList<Map<String, String>> mData;
    private Context context;
    private ImageView bank_head_iv;
    private Button repayment_btn;
    private TextView bank_name_tv, bank_type_tv, bank_num_tv;
    private List<BankCardInfo> bankCardInfo;
//	private Map<String, String> map;

    private MyClickListener mListener;

    // 自定义接口，用于回调按钮点击事件到Activity
    public interface MyClickListener {
        public void clickListener(View v);
    }

    public BankImageAdapter() {
    }

    public BankImageAdapter(Context context, List<BankCardInfo> bankCardInfo, MyClickListener mListener) {
        this.context = context;
        this.bankCardInfo = bankCardInfo;
        this.mListener = mListener;
    }

    @Override
    public int getCount() {
        return bankCardInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return bankCardInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.bank_card_view, null);
        }
        bank_head_iv = (ImageView) convertView.findViewById(R.id.bank_head_iv);
        bank_name_tv = (TextView) convertView.findViewById(R.id.bank_name_tv);
        bank_type_tv = (TextView) convertView.findViewById(R.id.bank_type_tv);
        bank_num_tv = (TextView) convertView.findViewById(R.id.bank_num_tv);
        repayment_btn = (Button) convertView.findViewById(R.id.repayment_btn);
        LinearLayout ll_item = (LinearLayout) convertView.findViewById(R.id.ll_item);
        LinearLayout ll_line = (LinearLayout) convertView.findViewById(R.id.line_ll);
//		TextView is_quickpay_tv = (TextView) view.findViewById(R.id.is_quickpay_tv);
//		Button is_quickpay_btn = (Button) view.findViewById(R.id.is_quickpay_btn);
        //ImageView kjbz_iv=(ImageView) view.findViewById(R.id.kjbz_iv);
        TextView jsk_tv=(TextView) convertView.findViewById(R.id.jsk_tv);
        final BankCardInfo bi = bankCardInfo.get(position);

        //0表示没有快捷支付，1有
        if ("0".equals(bi.getIs_quickpay())) {
//			is_quickpay_tv.setVisibility(View.VISIBLE);
//			is_quickpay_btn.setVisibility(View.VISIBLE);
            //kjbz_iv.setVisibility(View.INVISIBLE);
//			is_quickpay_btn.setOnClickListener(this);
//			is_quickpay_btn.setTag(arg0);
        }else if("1".equals(bi.getIs_quickpay())){
//			is_quickpay_tv.setVisibility(View.INVISIBLE);
//			is_quickpay_btn.setVisibility(View.INVISIBLE);
            //kjbz_iv.setVisibility(View.VISIBLE);
        }
        //0不是入账卡，1是
        if("0".equals(bi.getIf_record())){
            jsk_tv.setVisibility(View.INVISIBLE);
        }else if("1".equals(bi.getIf_record())){
            jsk_tv.setVisibility(View.VISIBLE);
        }
        bank_name_tv.setText(bi.getBankName());
        if (bi.getCardTp().equals("0")) {
            bank_type_tv.setText("借记卡");
            ll_item.setBackgroundResource(R.drawable.bank_debitcard_circular);
            ll_line.setBackgroundResource(R.color.line_ll);;
        } else {
            bank_type_tv.setText("信用卡");
            ll_item.setBackgroundResource(R.drawable.bank_creditcard_circular);
        }
        String k = bi.getCardId();
        String x = "**** **** **** **** ";
        bank_num_tv.setText(x + k.substring(k.length() - 4, k.length()));
        setHead(bi.getBankName());
        repayment_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg1) {
                Intent intent = new Intent(context, RepaymentMoneyActivity.class);
                intent.putExtra("bank_name_tv", bi.getBankName());
                intent.putExtra("bank_type_tv", bi.getCardTp());
                intent.putExtra("bank_num_tv", bi.getCardId());
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    /** 设置头像 */
    private void setHead(String bankName) {
        System.out.println(bankName);
        if (bankName.equals("招商银行")) {
            bank_head_iv.setImageResource(R.mipmap.zhaoshang);
        } else if (bankName.equals("工商银行")) {
            bank_head_iv.setImageResource(R.mipmap.gongshang);
        } else if (bankName.equals("建设银行")) {
            bank_head_iv.setImageResource(R.mipmap.jianshe);
        } else if (bankName.equals("广发银行")) {
            bank_head_iv.setImageResource(R.mipmap.guangfa);
        } else if (bankName.equals("华夏银行")) {
            bank_head_iv.setImageResource(R.mipmap.huaxia);
        } else if (bankName.equals("农业银行")) {
            bank_head_iv.setImageResource(R.mipmap.nongye);
        } else if (bankName.equals("兴业银行")) {
            bank_head_iv.setImageResource(R.mipmap.xingye);
        } else if (bankName.equals("光大银行")) {
            bank_head_iv.setImageResource(R.mipmap.guangda);
        } else if (bankName.equals("交通银行")) {
            bank_head_iv.setImageResource(R.mipmap.jiaotong);
        } else if (bankName.equals("民生银行")) {
            bank_head_iv.setImageResource(R.mipmap.minsheng);
        } else if (bankName.equals("邮政储蓄银行")) {
            bank_head_iv.setImageResource(R.mipmap.youzheng);
        } else if (bankName.equals("中信银行")) {
            bank_head_iv.setImageResource(R.mipmap.zhongxin);
        } else if (bankName.equals("人民银行")) {
            bank_head_iv.setImageResource(R.mipmap.renmin);
        } else {
            bank_head_iv.setImageResource(R.mipmap.yinlian);
        }
    }

    @Override
    public void onClick(View view) {
        mListener.clickListener(view);

    }
}
