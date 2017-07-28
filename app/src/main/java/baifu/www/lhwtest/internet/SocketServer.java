package baifu.www.lhwtest.internet;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by Ivan.L on 2017/7/27.
 */

public class SocketServer implements Runnable {

    private Handler myHandler;
    private String ip;
    private int port;
    private byte[] message;
    private String temp;

    public SocketServer() {
    }

    public SocketServer(Handler myHandler, byte[] message, String temp) {
        super();
        this.ip = IpAddress.ip;
        this.port = IpAddress.port;
        this.myHandler = myHandler;
        this.message = message;
        this.temp = temp;
    }

//    将String转换成ASCII码
    public static String stringToAscii(String value){
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            sbu.append((int)chars[i]);
        }
        return sbu.toString();
    }

    @Override
    public void run() {
        Socket socket = null;
        InputStream dis = null;
        OutputStream dos = null;

        try {
            socket = new Socket(ip,port);
            socket.setKeepAlive(false);
            socket.setSoTimeout(20 * 1000);

            dos = socket.getOutputStream();
            dos.write(message);
            dos.flush();
            dis = socket.getInputStream();
            int count = 0;
            while (count == 0) {
                count = dis.available();
            }
            byte[] b = new byte[count];
            dis.read(b);
            String strKey = "";
            for (int i = 0; i < b.length; i++)
                strKey = strKey + String.format("%02X",b[i]);
            message(strKey);
        }catch(ConnectTimeoutException c){
            message("Timeout");
        }catch(SocketException c){
            Message msg = new Message();
            msg.obj = temp;
            Bundle bundleData = new Bundle();
            bundleData.putString("data","SocketException");
            bundleData.putString("info",c.getMessage());
            String str="";
            for (int i = 0; i < message.length; i++)
                str = str + String.format("%02X",message[i]);
            bundleData.putString("message",str);
            msg.setData(bundleData);
            myHandler.sendMessage(msg);
//			message("SocketException");
        }catch(SocketTimeoutException s){
            message("Timeout");
        }catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(dos!=null){
                    dos.close();
                }if(dis!=null){
                    dis.close();
                }if(socket!=null){
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void message(String msgStr) {
        Message msg = new Message();
        msg.obj = temp;
        Bundle bundleData = new Bundle();
        bundleData.putString("data",msgStr);
        msg.setData(bundleData);
        myHandler.sendMessage(msg);
    }
}
