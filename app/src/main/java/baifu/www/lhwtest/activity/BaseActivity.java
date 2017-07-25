package baifu.www.lhwtest.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import baifu.www.lhwtest.R;

/**
 * Created by Administrator on 2017/7/24.
 * Ivan lan
 */

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
    }

    //    获得布局文件的id
    protected abstract int getContentViewId();

    //    设置Activity的标题
    public void setTitle(int resourceId) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_top);
        TextView textView = (TextView) linearLayout.findViewById(R.id.activity_title);
        textView.setText(resourceId);
    }

    //    设置点击监听事件
    public void setOnclickListener(View.OnClickListener onclickListener) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_top);
        LinearLayout optionButton = (LinearLayout) linearLayout.findViewById(R.id.activity_setting_up);
        optionButton.setOnClickListener(onclickListener);
    }

    //    不显示设置或者其他按钮
    public void setOptionButtonInVisible() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_top);
        LinearLayout optionButton = (LinearLayout) findViewById(R.id.activity_setting_up);
        optionButton.setVisibility(View.VISIBLE);
    }

    //    按下返回按钮处理
    public void onBack(View view) {
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
