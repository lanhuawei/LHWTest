package baifu.www.lhwtest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import baifu.www.lhwtest.R;
import baifu.www.lhwtest.entity.HomeModel;
import baifu.www.lhwtest.view.image.SmartImageView;

/**
 * Created by Ivan.L on 2017/7/28.
 * 主页面布局适配器
 */

public class HomeGridTempAdapter extends BaseAdapter {
    private LayoutInflater li_mInflater;
    private List<HomeModel> ls_homemodel;
    private Context context;
    private int line_number;

    public HomeGridTempAdapter(Context ctx, List<HomeModel> ls_homemodle) {
        super();
        this.li_mInflater = LayoutInflater.from(context);
        this.ls_homemodel = ls_homemodle;
        context = ctx;
        line_number = ls_homemodle.size() % 3;
    }

    @Override
    public int getCount() {
        return ls_homemodel.size();
    }

    @Override
    public Object getItem(int position) {
        return ls_homemodel.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HomeModel rb = ls_homemodel.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = li_mInflater.inflate(R.layout.activity_home_modle_item, null);
            // 把布局文件中所有组件的对象封装至ViewHolder对象中
            viewHolder.iv_Home = (SmartImageView) convertView.findViewById(R.id.iv_home);
            viewHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.v_line=(View) convertView.findViewById(R.id.v_line);
            // 把ViewHolder对象封装至View对象中
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(ls_homemodel.size()-position-1<line_number){
            viewHolder.v_line.setVisibility(View.GONE);
        }
        viewHolder.tv_name.setText(rb.getName());
        viewHolder.iv_Home.setImageResource(rb.getAddress());
        return convertView;
    }

    final static class ViewHolder {
        // 条目的布局文件中有什么组件，这里就定义什么属性
        SmartImageView iv_Home;
        TextView tv_name;
        View v_line;
    }
}
