package baifu.www.lhwtest.internet;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by Ivan.L on 2017/7/27.
 * 百付Http请求
 */

public class BaifuHttpRequest {
    /**
     * 获取图片资源
     * @param httpCommonCallback
     */
    public static void changeImage(Callback.CommonCallback<String> httpCommonCallback) {
        RequestParams requestParams = new RequestParams(IpAddress.CHANGE_IMAGE);
        x.http().post(requestParams, httpCommonCallback);
    }
}
