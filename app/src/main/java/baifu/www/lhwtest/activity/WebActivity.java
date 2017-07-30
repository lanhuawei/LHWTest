package baifu.www.lhwtest.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import baifu.www.lhwtest.R;
import baifu.www.lhwtest.utils.showDialogUtil;

/**
 * Created by Ivan on 2017/7/29.
 * 网页web的activity
 */

public class WebActivity extends BaseActivity {

    private WebView web_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_web_activity;
    }

    private void initView() {
        String html = getIntent().getExtras().getString("url");
        String status = getIntent().getExtras().getString("status");
        TextView title_web_iv = (TextView) findViewById(R.id.title_web_iv);
        title_web_iv.setText(status);
        showDialogUtil.startProgressDialog("", WebActivity.this);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ImageView back_web_iv = (ImageView) findViewById(R.id.back_web_iv);
        back_web_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        web_view = (WebView) findViewById(R.id.web_view);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setDomStorageEnabled(true);
        web_view.loadUrl(html);
        web_view.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        web_view.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    showDialogUtil.stopProgressDialog();
                    // 网页加载完成
                } else {
                    // 加载中
                }
            }
        });
        web_view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && web_view.canGoBack()) {  //表示按返回键时的操作
                        web_view.goBack();   //后退
                        return true;    //已处理
                    }
                }
                return false;
            }
        });
        findViewById(R.id.back_web_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web_view.goBack();//后退
            }
        });
        findViewById(R.id.forward_web_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web_view.goForward();//前进
            }
        });
        findViewById(R.id.refresh_web_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web_view.reload();//刷新
                showDialogUtil.startProgressDialog("", WebActivity.this);
            }
        });
        findViewById(R.id.stop_web_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                web_view.stopLoading();
            }
        });
//		web_view.getSettings().setBuiltInZoomControls(true);//缩放按钮
        web_view.getSettings().setUseWideViewPort(true);//等比缩放
//		web_view.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        web_view.getSettings().setLoadWithOverviewMode(true);//按照本地大小适配
        //优先使用缓存
        web_view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }
}
