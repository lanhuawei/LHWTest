package baifu.www.lhwtest.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.TextView;

import java.util.List;

import baifu.www.lhwtest.R;

/**
 * Created by Ivan.L on 2017/7/26.
 * 获取验证码的倒计时操作
 * 后面尖括号内分别是参数（线程休息时间），进度(publishProgress用到)，返回值 类型
 */

public class CountdownAsyncTask extends AsyncTask<Integer,Integer,String> {

    private TextView mTextView = null;
    private Context context;

    public CountdownAsyncTask(TextView mTextView, Context context) {
        this.mTextView = mTextView;
        this.context = context;
    }

//    初始化,按钮点击后
    @Override
    protected void onPreExecute() {
        mTextView.setEnabled(false);
        mTextView.setBackgroundResource(R.drawable.gray_btn);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        mTextView.setEnabled(true);
        mTextView.setText(s);
        mTextView.setBackgroundResource(R.drawable.blue_btn);
        super.onPostExecute(s);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mTextView.setText(values[0] + "s秒重新获取");
        super.onProgressUpdate(values);
    }
//    耗时操作
    @Override
    protected String doInBackground(Integer... params) {
        for (int i = 59; i >= 0 && isTopActivity(context); i--) {
            try {
                Thread.sleep(params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            publishProgress(i);
        }
        return " 获取验证码 ";
    }

    private boolean isTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //        SDK 21之后弃用了
        //        ComponentName componentName = activityManager.getRunningTasks(1).get(0).topActivity;
//        String className = null;
        boolean isInBackground = true;
        if (Build.VERSION.SDK_INT > 21) {
//            android5.0之后版本
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfos) {
//                前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activityProcess : processInfo.pkgList) {
                        if (activityProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
//            android 5.0之前
//            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//            return context.toString().contains(cn.getClassName());
            List<ActivityManager.RunningTaskInfo> taskInfos = activityManager.getRunningTasks(1);
            ComponentName componentName = taskInfos.get(0).topActivity;
            if (componentName.getPackageName().equals(context.getPackageName())) {
                isInBackground = true;
            }
        }
        return isInBackground;
//        return context.toString().contains(componentName.getClassName());
    }
}
