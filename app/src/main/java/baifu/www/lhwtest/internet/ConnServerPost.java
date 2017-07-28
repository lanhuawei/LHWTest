package baifu.www.lhwtest.internet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

/**
 * Created by Ivan.L on 2017/7/27.
 * 服务器连接的post请求
 */

public class ConnServerPost implements Runnable {

    private String url;
    private Handler myHandler;
    private String temp;
    List<NameValuePair> params;
    public static String PHPSESSID = null;

    public ConnServerPost() {
    }

    public ConnServerPost(String url, Handler myHandler, String temp) {
        this.url = url;
        this.myHandler = myHandler;
        this.temp = temp;
    }

    public ConnServerPost(String url, Handler myHandler, String temp, List<NameValuePair> params) {
        this(url, myHandler, temp);
        this.params = params;
    }

    private Map<String, ContentBody> contentBody;

    /**
     * map方式
     * @param url 路径
     * @param myHandler 数据返回监听handler
     * @param temp 返回参数
     * @param contentBody contentBody类型有(FileBody,ByteArrayBody,StringBody)
     */
    public ConnServerPost(String url, Handler myHandler, String temp, Map<String, ContentBody> contentBody) {
        this.url = url;
        this.myHandler = myHandler;
        this.temp = temp;
        this.contentBody = contentBody;
    }

    @Override
    public void run() {
        try {
            DefaultHttpClient httpClient = SSLSocketFactoryEx.getNewHttpClient();
            HttpPost httpPostRequest = new HttpPost(url);
            MultipartEntity m = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            if(contentBody!=null){
                for (Map.Entry<String, ContentBody> entry : contentBody.entrySet()) {
                    m.addPart(entry.getKey(),entry.getValue());
                }
                httpPostRequest.setEntity(m);
            }else if(params!=null){
				/* 添加请求参数到请求对象*/
                httpPostRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            }if(null != PHPSESSID){
                httpPostRequest.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
            }

			/*发送请求并等待响应*/
            HttpResponse httpPostResponse = httpClient.execute(httpPostRequest);
			/*若状态码为200 ok*/
            if(httpPostResponse.getStatusLine().getStatusCode() == 200){
                CookieStore mCookieStore = httpClient.getCookieStore();
                List<Cookie> cookies = mCookieStore.getCookies();
                for (int i = 0; i < cookies.size(); i++) {
                    //这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
                    if ("PHPSESSID".equals(cookies.get(i).getName())) {
                        PHPSESSID = cookies.get(i).getValue();
                        break;
                    }
                }

				/*读返回数据*/
                String strResult = EntityUtils.toString(httpPostResponse.getEntity());
                System.out.println(strResult);
                result(strResult);
            }
        } catch(ConnectTimeoutException c){
            result("Timeout");
        } catch(HttpHostConnectException h){
            result("Timeout");
        } catch(SocketTimeoutException s){
            result("Timeout");
        } catch(UnknownHostException s){
            result("Timeout");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 结果
     * @param strResult
     */
    private void result(String strResult) {
        Message msg = new Message();
        msg.obj = temp;
        Bundle bundleData = new Bundle();
        bundleData.putString("data",strResult);
        msg.setData(bundleData);
        myHandler.sendMessage(msg);
    }
}
