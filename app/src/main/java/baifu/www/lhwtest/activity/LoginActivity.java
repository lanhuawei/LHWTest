package baifu.www.lhwtest.activity;

import android.os.Bundle;
import android.view.View;

import baifu.www.lhwtest.R;

/**
 * 登录界面
 */


public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //    点击登录按钮
    public void loginBtnOnClick(View view) {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_login_activity;
    }
}
