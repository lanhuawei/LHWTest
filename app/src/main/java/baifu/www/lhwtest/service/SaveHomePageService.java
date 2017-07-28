package baifu.www.lhwtest.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.xutils.common.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import baifu.www.lhwtest.BaseApplication;
import baifu.www.lhwtest.entity.HomePage;
import baifu.www.lhwtest.internet.BaifuHttpRequest;
import baifu.www.lhwtest.internet.IpAddress;
import baifu.www.lhwtest.utils.GsonTools;

/**
 * Created by Ivan.L on 2017/7/27.
 * 保存主页面服务
 */

public class SaveHomePageService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("tag", "打开服务");
        initDate();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i("tag", "停止服务");
        super.onDestroy();
    }

    /**
     * 初始化主页面数据
     */
    private HomePage mHomePage = null;
    private List<HomePage.ResultBean> list_resule = new ArrayList<HomePage.ResultBean>();


    // 加载数据
    private HomePage.ResultBean rb = null;
    private List<HomePage.ResultBean> list_image = new ArrayList<HomePage.ResultBean>();
    private List<HomePage.ResultBean> mlist_lunbo = new ArrayList<HomePage.ResultBean>();// 主界面轮播图list
    private Bitmap[] bm;

    public void initDate() {

        BaifuHttpRequest.changeImage(new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                mHomePage = GsonTools.getBean(s, HomePage.class);
                Log.i("tag", "主界面:" + mHomePage.toString());
                if ("1000".equals(mHomePage.getCode())) {
                    list_resule = mHomePage.getResult();
                    // 移除移动支付
                    for (int i = list_resule.size() - 1; i >= 0; i--) {
                        rb = list_resule.get(i);
                        if (!TextUtils.isEmpty(rb.getSort())&&!isNumeric(rb.getSort())) {
                            list_resule.remove(i);
                            continue;
                        }
                        if (!TextUtils.isEmpty(rb.getImg()) && !TextUtils.isEmpty(rb.getType())
                                && !TextUtils.isEmpty(rb.getSort()) && (Integer.parseInt(rb.getSort()) > 9)) {
                            mlist_lunbo.add(rb);
                        }
                        if (TextUtils.isEmpty(rb.getImg()) || TextUtils.isEmpty(rb.getName())
                                || TextUtils.isEmpty(rb.getType()) || TextUtils.isEmpty(rb.getSort())) {
                            list_resule.remove(i);
                        }
                    }
                    // list排序
                    Collections.sort(list_resule, new Comparator<HomePage.ResultBean>() {

                        @Override
                        public int compare(HomePage.ResultBean arg0, HomePage.ResultBean arg1) {
                            if (Integer.parseInt(arg0.getSort()) > Integer.parseInt(arg1.getSort())) {
                                return 1;
                            } else if (Integer.parseInt(arg0.getSort()) == Integer.parseInt(arg1.getSort())) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    });
                    if (null != mlist_lunbo || mlist_lunbo.size() > 0) {
                        Collections.sort(mlist_lunbo, new Comparator<HomePage.ResultBean>() {

                            @Override
                            public int compare(HomePage.ResultBean arg0, HomePage.ResultBean arg1) {
                                if (Integer.parseInt(arg0.getSort()) > Integer.parseInt(arg1.getSort())) {
                                    return 1;
                                } else if (Integer.parseInt(arg0.getSort()) == Integer.parseInt(arg1.getSort())) {
                                    return 0;
                                } else {
                                    return -1;
                                }
                            }
                        });
                    }

                    BaseApplication.getInstance().setList_resule(list_resule);
                    BaseApplication.getInstance().setList_lunbo(mlist_lunbo);
                    // 下载图片
                    // new Thread(new Runnable() {
                    //
                    // @Override
                    // public void run() {
                    // try {
                    // if (getBitmap(IpAddress.baufydatas, list_image) != null)
                    // {
                    // bm = getBitmap(IpAddress.baufydatas, list_image);
                    // MyApplication.getInstance().setHmBitmap(hmBitmap);
                    // Log.i("tag", "下载成功");
                    // Log.i("tag","bm_len:"+bm.length+"hmBitmap.size:"+hmBitmap.size()+
                    // "smrz"+hmBitmap.get("smrz")+"zhbd"+hmBitmap.get("zhbd"));
                    // } else {
                    // Log.i("tag", "下载失败");
                    // }
                    // } catch (IOException e) {
                    // e.printStackTrace();
                    // }
                    // }
                    // }).start();
                } else {
                    // Toast.makeText(HomeNewActivity.this, "请连接网络",
                    // Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.i("tag", "throwable" + throwable.getMessage());
            }

            @Override
            public void onCancelled(CancelledException e) {
                Log.i("tag", "CancelledException" + e.toString());
            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 判断是否为数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }


    public static HashMap<String, Bitmap> hmBitmap = new HashMap<String, Bitmap>();

    /**
     * 获取网络上需要下载的图片
     * @param path
     *              固定的路径
     * @param list_image
     * @picName
     *          提前获取的图片名的数组
     * @return
     * @throws IOException
     */
    public static Bitmap[] getBitmap(String path, List<HomePage.ResultBean> list_image) throws IOException {
        Bitmap[] b = new Bitmap[list_image.size()];
        // 根据图片数组长度来，循环获取我们需要的图片数组
        for (int i = 0; i < list_image.size(); i++) {
            URL url = new URL(path + list_image.get(i).getImg());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // 这里可以调节延迟时间
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() == 200) {
                InputStream inputStream = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                b[i] = bitmap;
                hmBitmap.put(list_image.get(i).getType(), bitmap);
            }
        }
        return b;
    }

    private void ManyPic(final String[] picName) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Message ms = handler.obtainMessage();
                try {
                    if (getBitmap(IpAddress.baufydatas, list_image) != null) {
                        bm = getBitmap(IpAddress.baufydatas, list_image);
                        // ms.what = BITMAP_SUCCESS;
                        // ms.obj = bm;
                        // ms.sendToTarget();
                    } else {
                        Log.i("tag", "下载失败");
                        // ms.what = BITMAP_ERROR;
                        // ms.obj = "下载失败";
                        // ms.sendToTarget();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
