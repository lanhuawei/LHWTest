package baifu.www.lhwtest.view.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;

/**
 * Created by Ivan.L on 2017/7/28.
 * 图片加载线程栈
 */

public class SmartImageTask implements Runnable {

    private static final int BITMAP_READY = 0;

    private boolean cancelled = false;
    private OnCompleteHandler onCompleteHandler;
    private SmartImage image;
    private Context context;

    public static class OnCompleteHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bitmap bitmap = (Bitmap)msg.obj;
            onComplete(bitmap);
        }
        public void onComplete(Bitmap bitmap){};
    }

    public abstract static class OnCompleteListener {
        public abstract void onComplete();
    }

    public SmartImageTask(SmartImage image, Context context) {
        this.image = image;
        this.context = context;
    }

    public void setOnCompleteHandler(OnCompleteHandler completeHandler) {
        this.onCompleteHandler = completeHandler;
    }

    @Override
    public void run() {
        if (image != null) {
            complete(image.getBitmap(context));
            context = null;
        }
    }

    public void cancel() {
        cancelled = true;
    }

    public void complete(Bitmap bitmap){
        if(onCompleteHandler != null && !cancelled) {
            onCompleteHandler.sendMessage(onCompleteHandler.obtainMessage(BITMAP_READY, bitmap));
        }
    }
}
