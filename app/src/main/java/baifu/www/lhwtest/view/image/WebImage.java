package baifu.www.lhwtest.view.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ivan.L on 2017/7/28.
 * 网页图片
 */

public class WebImage implements SmartImage {

    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 10000;

    private static WebImageCache webImageCache;

    private String url;

    public WebImage(String url) {
        this.url = url;
    }

    @Override
    public Bitmap getBitmap(Context context) {
        // Don't leak context
        if(webImageCache == null) {
            webImageCache = new WebImageCache(context);
        }

        // Try getting bitmap from cache first
        Bitmap bitmap = null;
        if(url != null) {
            bitmap = webImageCache.get(url);
            if(bitmap == null) {
                bitmap = getBitmapFromUrl(url);
                if(bitmap != null){
                    webImageCache.put(url, bitmap);
                }
            }
        }

        return bitmap;
    }
//    通过路径获得获得bitmap
    private Bitmap getBitmapFromUrl(String url) {
        Bitmap bitmap = null;

        try {
            URLConnection conn = new URL(url).openConnection();
            conn.setConnectTimeout(CONNECT_TIMEOUT);
            conn.setReadTimeout(READ_TIMEOUT);
            bitmap = BitmapFactory.decodeStream((InputStream) conn.getContent());
        } catch(Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * 从网页图片缓存移除
     * @param url
     */
    public static void removeFromCache(String url) {
        if(webImageCache != null) {
            webImageCache.remove(url);
        }
    }
}
