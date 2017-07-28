package baifu.www.lhwtest.utils;

import org.apache.http.NameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Ivan.L on 2017/7/26.
 * 字节工具类
 */

public class ByteUtils {

    /**
     * 十六进制 转换byte[]
     * @param hexStr
     * @return
     */

    public static byte[] hexString2ByteArray(String hexStr) {
        if (hexStr == null)
            return null;
        if (hexStr.length() % 2 != 0) {
            return null;
        }
        byte[] data = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            char hc = hexStr.charAt(2 * i);
            char lc = hexStr.charAt(2 * i + 1);
            byte hb = hexChar2Byte(hc);
            byte lb = hexChar2Byte(lc);
            if (hb < 0 || lb < 0) {
                return null;
            }
            int n = hb << 4;
            data[i] = (byte) (n + lb);
        }
        return data;
    }

    /**
     * 图片到byte数组
     * @param path
     * @return
     */

    public static byte[] image2byte(String path){
        byte[] data = null;
        FileInputStream input = null;
        try {
            input = new FileInputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int numBytesRead = 0;
            while ((numBytesRead = input.read(buf)) != -1) {
                output.write(buf, 0, numBytesRead);
            }
            data = output.toByteArray();
            output.close();
            input.close();
        }
        catch (FileNotFoundException ex1) {
            ex1.printStackTrace();
        }
        catch (IOException ex1) {
            ex1.printStackTrace();
        }
        return data;
    }

    /**
     * MD5算法
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 随机生成字母数字
     * @param length
     * @return
     */

    public static String getCharAndNumr(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val += (char) (choice + random.nextInt(26));
            } else if ("num".equalsIgnoreCase(charOrNum)) { // 数字
                val += String.valueOf(random.nextInt(10));
            }
        }
        return val;
    }

    /**
     * 整理数据并拼接
     * @param nv
     * @return
     */
    public static final String JoiningTogether(List<NameValuePair> nv) {
        List<String> ager = new ArrayList<String>();
        for (NameValuePair par : nv) {
            ager.add(par.getName() + "=" + par.getValue());
        }
        Collections.sort(ager);
        String a = "";
        for (int i = 0; i < ager.size(); i++) {
            if (i == 0) a += ager.get(i);
            else a = a + "&" + ager.get(i);
        }
        return a;
    }


    /**
     * 十六进制中用到
     * @param c
     * @return
     */
    public static byte hexChar2Byte(char c) {
        if (c >= '0' && c <= '9')
            return (byte) (c - '0');
        if (c >= 'a' && c <= 'f')
            return (byte) (c - 'a' + 10);
        if (c >= 'A' && c <= 'F')
            return (byte) (c - 'A' + 10);
        return -1;
    }

    /**
     * 把map转换成： name1=value1&name2=value2 的形式，用于地址参数的请求
     * @param map
     * @return
     */
    public static String mapToString(Map<String,Object> map){
        StringBuffer stringBuffer = new StringBuffer("");
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            @SuppressWarnings("rawtypes")
            Map.Entry entry = (Map.Entry)it.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (null != value) {
                stringBuffer = stringBuffer.append(key.toString() + "=").append(
                        value.toString() + "&");
            } else {
                stringBuffer = stringBuffer.append(key.toString() + "=").append("&");
            }
        }
        stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        return stringBuffer.toString();
    }

    /**
     * byte[] 16进制字符
     * @param arr
     * @return
     */
    public static String byteArray2HexString(byte[] arr) {
        StringBuilder sbd = new StringBuilder();
        for (byte b : arr) {
            String tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() < 2)
                tmp = "0" + tmp;
            sbd.append(tmp);
        }
        return sbd.toString();
    }

    public static String byteArray2HexStringWithSpace(byte[] arr) {
        StringBuilder sbd = new StringBuilder();
        for (byte b : arr) {
            String tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() < 2)
                tmp = "0" + tmp;
            sbd.append(tmp);
            sbd.append(" ");
        }
        return sbd.toString();
    }

    static public String getBCDString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return ByteUtils.byteArray2HexString(t);
    }

    static public String getHexString(byte[] data, int start, int end) {
        byte[] t = new byte[end - start + 1];
        System.arraycopy(data, start, t, 0, t.length);
        return ByteUtils.byteArray2HexStringWithSpace(t);
    }


    static public String byte2string(byte[] data){
        StringBuffer result = new StringBuffer();
        for (byte b : data) {
            String s = String.format("%,", b);
            result.append(s);
        }
        return result.toString();
    }

    public static byte[] process(byte[] pin, byte[] accno) {
//	    byte arrAccno[] = getHAccno(accno);
//	    byte arrPin[] = getHPin(pin);
        byte arrRet[] = new byte[8];
        //PIN BLOCK 格式等于 PIN 按位异或 主帐�?;
        for (int i = 0; i < 8; i++) {
            arrRet[i] = (byte) (pin[i] ^ accno[i]);
        }
        return arrRet;
    }

    /**
     * 对密码进行转
     * PIN格式
     * BYTE 1 PIN的长
     * BYTE 2 �? BYTE 3/4/5/6/7   4--12个PIN(每个PIN�?4个BIT)
     * BYTE 4/5/6/7/8 �? BYTE 8   FILLER “F�? (每个“F“占4个BIT)
     * @param pin
     * @return
     */

    public static byte[] getHPin(String pin) {
        byte arrPin[] = pin.getBytes();
        byte encode[] = new byte[8];
        encode[0] = (byte) 0x06;
        encode[1] = (byte) uniteBytes(arrPin[0], arrPin[1]);
        encode[2] = (byte) uniteBytes(arrPin[2], arrPin[3]);
        encode[3] = (byte) uniteBytes(arrPin[4], arrPin[5]);
        encode[4] = (byte) 0xFF;
        encode[5] = (byte) 0xFF;
        encode[6] = (byte) 0xFF;
        encode[7] = (byte) 0xFF;
        return encode;
    }

    /**
     * PinBlock
     * @param pin
     * @return
     */
    public static byte[] getPinBlock(String pin) {
        String block0 = "000000000000";
        String blockF = "FFFFFFFFFFFF";
        int pinLen = pin.length();
        byte[] pinInfo = new byte[8];
        byte[] subInfo;

        if (pinLen < 4) {
            pin = block0.substring(0, 4-pinLen) + pin;
        } else if (pinLen > 12) {
            pin = pin.substring(0, 12);
        } else if (pinLen%2 != 0) {
            pin = pin + "F";
        }
        pinLen = pin.length();
        pinInfo[0] = (byte)pinLen;
        subInfo = hex2Bin(pin + blockF.substring(0, 14-pinLen));
        System.arraycopy(subInfo, 0, pinInfo, 1, 7);
        return pinInfo;
    }

    public static byte[] hex2Bin(String hex) {
        byte[] hexbin = hex.getBytes();
        byte[] data = new byte[(hexbin.length + 1) / 2];

        for (int i = 0; i < hexbin.length; i++) {
            int v = Math.abs(hexbin[i]);
            if (v >= '0' && v <= '9')
                v = v - '0';
            else if (v >= 'A' && v <= 'F')
                v = v - 'A' + 10;
            else if (v >= 'a' && v <= 'f')
                v = v - 'a' + 10;
            else
                v = 0;
            v &= 0x0f;
            if (i % 2 == 0) {
                v <<= 4;
            }
            data[i / 2] |= v;
        }
        return data;
    }
    public static byte[] getPanInfo(String pan ) {
        String block0 = "0000000000000";
        int panLen = pan.length();
        byte[] panInfo = new byte[8];
        byte[] subInfo;
        panInfo[0] = 0x00;
        panInfo[1] = 0x00;
        if (panLen < 13) {
            pan = block0.substring(0, 13-panLen) + pan;
            subInfo = hex2Bin(pan.substring(0, panLen-1));
        } else {
            subInfo = hex2Bin(pan.substring(panLen-13, panLen-1));
        }
        System.arraycopy(subInfo, 0, panInfo, 2, 6);
        return panInfo;
    }

    /**
     * 将指定字符串src，以每两个字符分割转换为16进制形式 如："2B44EFD9" –> byte[]{0x2B, 0×44, 0xEF,0xD9}
     * @param src
     * @return
     */

    public static byte[] HexString2Bytes(String src) {
        if (null == src || 0 == src.length()) {
            return null;
        }
        byte[] ret = new byte[src.length() / 2];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < (tmp.length / 2); i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    /**
     * 将两个ASCII字符合成一个字节； 如："EF"–> 0xEF
     * @param src0
     * @param src1
     * @return
     */
    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[] { src0 }))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[] { src1 }))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public static String convertStringToHex(String str) {
        char[] chars = str.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString();
    }

}
