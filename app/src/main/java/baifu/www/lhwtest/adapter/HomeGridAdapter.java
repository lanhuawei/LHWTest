package baifu.www.lhwtest.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import baifu.www.lhwtest.R;
import baifu.www.lhwtest.entity.HomePage;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.view.image.SmartImageView;

/**
 * Created by Ivan.L on 2017/7/28.
 * 获取并显示远程图片的适配器
 * 使用Picasso框架
 */

public class HomeGridAdapter extends BaseAdapter {
    private LayoutInflater li_mInflater;
    private List<HomePage.ResultBean> ls_homemodle;
    private Context context;
    private int line_number;

    public HomeGridAdapter(Context ctx, List<HomePage.ResultBean> ls_homemodle) {
        this.li_mInflater = LayoutInflater.from(ctx);
        this.ls_homemodle = ls_homemodle;
        this.context = ctx;
        line_number = ls_homemodle.size() % 3;
    }

    @Override
    public int getCount() {
        return ls_homemodle.size();
    }

    @Override
    public Object getItem(int position) {
        return ls_homemodle.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HomePage.ResultBean resultBean = ls_homemodle.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = li_mInflater.inflate(R.layout.activity_home_modle_item, null);
            // 把布局文件中所有组件的对象封装至ViewHolder对象中
            viewHolder.iv_Home = (SmartImageView) convertView.findViewById(R.id.iv_home);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.v_line = (View) convertView.findViewById(R.id.v_line);
            // 把ViewHolder对象封装至View对象中
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (ls_homemodle.size() - position - 1 < line_number) {
            viewHolder.v_line.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(resultBean.getName()) && !TextUtils.isEmpty(resultBean.getImg())) {
            viewHolder.tv_name.setText(resultBean.getName());
            //viewHolder.iv_Home.setImageBitmap(MyApplication.getInstance().getHmBitmap().get(rb.getType()));
//			Picasso.with(context).load(IpAddress.baufydata+rb.getImg()).into(viewHolder.iv_Home);
            Picasso.with(context).load(IpAddress.baufydata+resultBean.getImg()).placeholder(R.mipmap.home_01).into(viewHolder.iv_Home);
            //viewHolder.iv_Home.setImageUrl(IpAddress.baufydatas+rb.getImg());
        }
        return convertView;
    }

    final static class ViewHolder {
        // 条目的布局文件中有什么组件，这里就定义什么属性
        SmartImageView iv_Home;
        TextView tv_name;
        View v_line;
    }
}
