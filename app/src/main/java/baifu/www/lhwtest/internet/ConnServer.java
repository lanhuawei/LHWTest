package baifu.www.lhwtest.internet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Ivan.L on 2017/7/26.
 * 服务器连接工具
 */

public class ConnServer implements Runnable {

    private String url;
    private Handler myHandler;
    private String temp;

    public ConnServer(String url, Handler myHandler, String temp) {
        super();
        this.url = url;
        this.myHandler = myHandler;
        this.temp = temp;
    }
    @Override
    public void run() {
        try {
            HttpGet httpRequest = new HttpGet(url);
            HttpClient hc = SSLSocketFactoryEx.getNewHttpClient();
            hc.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10*1000);
            hc.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 0);
            HttpResponse httpResponse = hc.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                String strResult = EntityUtils.toString(httpResponse.getEntity());
                System.out.println(strResult);
                result(strResult);
            }
        }catch(UnknownHostException u){
            result("Timeout");
//			Message msg = new Message();
//			msg.obj = "Timeout";
//			Bundle bundleData = new Bundle();
//			bundleData.putString("data","Timeout");
//			msg.setData(bundleData);
//			myHandler.sendMessage(msg);
        } catch (ParseException e) {
            System.out.println("1");
            e.printStackTrace();
        }catch(ConnectTimeoutException c){
            result("Timeout");
        }catch(HttpHostConnectException h){
            result("Timeout");
        }catch(SocketTimeoutException s){
            result("Timeout");
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
