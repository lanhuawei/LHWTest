package baifu.www.lhwtest.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import baifu.www.lhwtest.R;
import baifu.www.lhwtest.wheel.ArrayWheelAdapter;
import baifu.www.lhwtest.wheel.OnWheelChangedListener;
import baifu.www.lhwtest.wheel.WheelView;

/**
 * Created by Ivan.L on 2017/7/25.
 */

public class showDialogUtil {
    public static Dialog pd;//ProgressDialog
    private static Dialog showDialogAd = null;
    private static Dialog showDialogHandlerAd = null;
    private static TextView ProgressDialogContent = null;
    private static Dialog selectDialog = null;

    public static final void showDialog(String content, final Context context) {
        showDialog(content, context, false);
    }

    /**
     * dialog提示
     */
    public static final void showDialog(String content, final Context context, final boolean b) {
        if (showDialogAd != null && showDialogAd.isShowing()) {
        } else {
            View showDialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_prompt_one, null);
            showDialogAd = new Dialog(context, R.style.dialog1);
            TextView prompt_content_tv = (TextView) showDialogView.findViewById(R.id.prompt_content_tv);
            prompt_content_tv.setText(content);
            Button ok_btn = (Button) showDialogView.findViewById(R.id.ok_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        showDialogAd.dismiss();
                    } catch (IllegalArgumentException i) {
                        showDialogAd = null;
                    }
                    if (b)
                        ((Activity) context).finish();
                }
            });
            showDialogAd.setContentView(showDialogView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = showDialogAd.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            showDialogAd.onWindowAttributesChanged(wl);
            try {
                showDialogAd.show();
            } catch (IllegalArgumentException i) {
                showDialogAd = null;
            }
        }
    }

    public static final void showDialog(String content, Context context, final Handler handler) {
        if (showDialogHandlerAd != null && showDialogHandlerAd.isShowing()) {
        } else {
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_prompt_one, null);
            showDialogHandlerAd = new Dialog(context, R.style.dialog1);
            showDialogHandlerAd.setCanceledOnTouchOutside(false);
            showDialogHandlerAd.setCancelable(false);
            TextView prompt_content_tv = (TextView) view.findViewById(R.id.prompt_content_tv);
            prompt_content_tv.setText(content);
            Button ok_btn = (Button) view.findViewById(R.id.ok_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    handler.sendMessage(msg);
                    try {
                        showDialogHandlerAd.dismiss();
                    } catch (IllegalArgumentException i) {
                        showDialogHandlerAd = null;
                    }
                }
            });
            showDialogHandlerAd.setContentView(view,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = showDialogHandlerAd.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            showDialogHandlerAd.onWindowAttributesChanged(wl);
            try {
                showDialogHandlerAd.show();
            } catch (IllegalArgumentException i) {
                showDialogHandlerAd = null;
            }
        }
    }

    /**
     * 三个可点击的对话框
     */
    private static Dialog selectThreeDialog = null;

    public static final void selectThreeDialog(String content, Context context, final String str,
                                               final Handler handler) {
        if (selectThreeDialog != null && selectThreeDialog.isShowing()) {
        } else {
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_three_select, null);
            selectThreeDialog = new Dialog(context, R.style.dialog1);
            selectThreeDialog.setCanceledOnTouchOutside(false);
            TextView prompt_content = (TextView) view.findViewById(R.id.prompt_content_three_tv);
            prompt_content.setText(content);
            Button organizing_data_btn = (Button) view.findViewById(R.id.organizing_data_btn);
            Button binding_device_three_btn = (Button) view.findViewById(R.id.binding_device_three_btn);
            Button cancel_btn = (Button) view.findViewById(R.id.cancel_three_btn);
            organizing_data_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.obj = str;
                    msg.what = 1;// 取消
                    handler.sendMessage(msg);
                    try {
                        selectThreeDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectThreeDialog = null;
                    }
                }
            });
            binding_device_three_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.obj = str;
                    msg.what = 2;// 取消
                    handler.sendMessage(msg);
                    try {
                        selectThreeDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectThreeDialog = null;
                    }
                }
            });
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.obj = str;
                    msg.what = 3;// 取消
                    handler.sendMessage(msg);
                    try {
                        selectThreeDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectThreeDialog = null;
                    }
                }
            });
            selectThreeDialog.setContentView(view,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = selectThreeDialog.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            selectThreeDialog.onWindowAttributesChanged(wl);
            try {
                selectThreeDialog.show();
            } catch (IllegalArgumentException i) {
                selectThreeDialog = null;
            }
        }
    }

    public static final void selectDialog(String content, Context context, final Handler handler, boolean b) {
        if (selectDialog != null && selectDialog.isShowing()) {
        } else {
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_select, null);
            selectDialog = new Dialog(context, R.style.dialog1);
            selectDialog.setCanceledOnTouchOutside(false);
            TextView prompt_content_tv = (TextView) view.findViewById(R.id.prompt_content_tv);
            prompt_content_tv.setText(content);
            Button ok_btn = (Button) view.findViewById(R.id.ok_btn);
            Button cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.what = 1;// 确定
                    handler.sendMessage(msg);
                    try {
                        selectDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectDialog = null;
                    }
                }
            });
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.what = 2;// 取消
                    handler.sendMessage(msg);
                    try {
                        selectDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectDialog = null;
                    }
                }
            });
            selectDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = selectDialog.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            selectDialog.onWindowAttributesChanged(wl);
            try {
                selectDialog.show();
            } catch (IllegalArgumentException i) {
                selectDialog = null;
            }
        }
    }

    /**
     * dialog选择
     *
     * @param content
     *          内容
     * @param context
     *          上下文
     * @param handler
     *          handler
     */

    public static final void selectDialog(String content, Context context, final Handler handler) {
        selectDialog(content, context, handler, "true");
    }

    public static final void selectDialog(String content, Context context, Handler handler, String string) {
        selectDialog(content, context, handler, "true", "确定", "取消");
    }

    public static final void selectDialog(String content, Context context, Handler handler, String ok, String cancel) {
        selectDialog(content, context, handler, "true", ok, cancel);
    }

    /**
     * dialog选择
     * @param content
     *            内容
     * @param context
     *            上下文
     * @param handler
     *            handler
     */
    public static final void selectDialog(String content, Context context, final Handler handler, final String str,
                                          String ok, String cancel) {
        if (selectDialog != null && selectDialog.isShowing()) {
        } else {
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_select, null);
            selectDialog = new Dialog(context, R.style.dialog1);
            selectDialog.setCanceledOnTouchOutside(false);
            TextView prompt_content_tv = (TextView) view.findViewById(R.id.prompt_content_tv);
            prompt_content_tv.setText(content);
            Button ok_btn = (Button) view.findViewById(R.id.ok_btn);
            if (ok != null) {
                ok_btn.setText(ok);
            }
            Button cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
            if (cancel != null) {
                cancel_btn.setText(cancel);
            }
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.obj = str;
                    handler.sendMessage(msg);
                    try {
                        selectDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectDialog = null;
                    }
                }
            });
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        selectDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectDialog = null;
                    }
                }
            });
            selectDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = selectDialog.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            selectDialog.onWindowAttributesChanged(wl);
            try {
                selectDialog.show();
            } catch (IllegalArgumentException i) {
                selectDialog = null;
            }
        }
    }

    //    可输入的弹窗
    public static final void inputDialog(String content, Context context, final Handler handler) {
        if (selectDialog != null && selectDialog.isShowing()) {
        } else {
            View view = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_input_select, null);
            selectDialog = new Dialog(context, R.style.dialog1);
            selectDialog.setCanceledOnTouchOutside(false);
            final EditText prompt_content_et = (EditText) view.findViewById(R.id.prompt_content_et);
            prompt_content_et.setHint(content);
            Button ok_btn = (Button) view.findViewById(R.id.ok_btn);
            Button cancel_btn = (Button) view.findViewById(R.id.cancel_btn);
            ok_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.what = 1;// 确定
                    msg.obj=prompt_content_et.getText().toString();
                    handler.sendMessage(msg);
                    try {
                        selectDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectDialog = null;
                    }
                }
            });
            cancel_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    Message msg = new Message();
                    msg.what = 2;// 取消
                    handler.sendMessage(msg);
                    try {
                        selectDialog.dismiss();
                    } catch (IllegalArgumentException i) {
                        selectDialog = null;
                    }
                }
            });
            selectDialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Window window = selectDialog.getWindow();
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            selectDialog.onWindowAttributesChanged(wl);
            try {
                selectDialog.show();
            } catch (IllegalArgumentException i) {
                selectDialog = null;
            }
        }
    }

    /**
     * 启动ProgressDialog
     */
    public static final void startProgressDialog(String msg, Context context) {
        if (pd != null && pd.isShowing()) {
            changeProgressDialogMessage(msg);
        } else {
            View loadView = ((Activity) context).getLayoutInflater().inflate(R.layout.load_view, null);
            ProgressDialogContent = (TextView) loadView.findViewById(R.id.content_tv);
            ProgressDialogContent.setText(msg);
            pd = new Dialog(context, R.style.dialog1);
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            pd.setContentView(loadView);
            try {
                pd.show();
            } catch (IllegalArgumentException i) {
                pd = null;
            }
        }
    }

    /**
     * 更改ProgressDialog
     */
    public static final void changeProgressDialogMessage(String msg) {
        if (pd != null && pd.isShowing()) {
            ProgressDialogContent.setText(msg);
        }
    }
    /**
     * 停止ProgressDialog
     */
    public static final void stopProgressDialog() {
        if (pd != null && pd.isShowing()) {
            try {
                pd.dismiss();
            } catch (IllegalArgumentException e) {
                pd = null;
            }
        }
    }

    //    toast
    @SuppressLint("ShowToast")
    public static final void errorMessage(String s, Context context) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastView = (LinearLayout) toast.getView();
        ImageView iv = new ImageView(context);
        iv.setImageResource(R.mipmap.error);
        toastView.addView(iv, 0);
        toast.show();
    }
    @SuppressWarnings("deprecation")
    public static void cityDialog(Context context, final Handler handler, final List<String> items1,
                                  final List<List<String>> items2) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle("选择城市：");
        // 创建布局
        final LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL); // 设置布局方式：水平
        final WheelView category1 = new WheelView(context);
        category1.setVisibleItems(5); // category1.setCyclic(true);
        category1.setAdapter(new ArrayWheelAdapter(items1));// 大项
        final WheelView category2 = new WheelView(context);
        category2.setVisibleItems(5);
        // category2.setCyclic(true);
        category2.setAdapter(new ArrayWheelAdapter(items2.get(0)));// 子项初始化
        // 创建参数
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp1.gravity = Gravity.LEFT;
        lp1.weight = (float) 0.4;
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp2.weight = (float) 0.6;
        lp2.gravity = Gravity.RIGHT;
        ll.addView(category1, lp1);
        ll.addView(category2, lp2);
        // 为category1添加监听
        category1.addChangingListener(new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                category2.setAdapter(new ArrayWheelAdapter(items2.get(newValue)));
                category2.setCurrentItem(0);
            }
        });
        // 为会话创建确定按钮
        dialog.setButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("category1", category1.getCurrentItem());
                bundle.putInt("category2", category2.getCurrentItem());
                msg.setData(bundle);
                handler.dispatchMessage(msg);
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





}
