package baifu.www.lhwtest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import net.sf.json.JSON;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.http.NameValuePair;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import baifu.www.lhwtest.encryption.CRC1021;


/**
 * Created by Ivan.L on 2017/7/26.
 * 各项工具
 */

public class Utility {
    private static final int PHOTO_REQUEST_CAMERA = 1;// 拍照
    private static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    private static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";

    public static JSONObject jsonObjectData(Context context, Object data) {
        return jsonObjectData(context, data, true);
    }
    public static JSONObject jsonObjectData(Context context, Object data, boolean a) {
        try {
            return JSONObject.fromObject(data);
        }catch(JSONException j) {//JSONException j出问题
            String err = "{\"code\"=\"99999999\",\"info\"=\"\u6750\u636e\u5f20\u5e38\"}";
            return JSONObject.fromObject(err);
        }
    }

    /**
     * 签名验证:md5(md5(string)+tmk)
     * @param nameValuePairs
     * @param tmk
     * @return
     */

    public static String signCalculation(List<NameValuePair> nameValuePairs, String tmk) {
        return ByteUtils.MD5(ByteUtils.JoiningTogether(nameValuePairs) + tmk);
    }

    /**
     * 判断中文
     * @param s1
     * @return
     */
    public static String chinese(String s1) {
        String a = "[\u4e00-\u9fa5]";
        String s = "";
        for (int i = 0; i < s1.length(); i++)
            s += a;
        return s;
    }

    /**
     * 16进制转字符串
     * @param s
     * @return
     */
    public static String toStringHex(String s) {
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");// UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    /**
     * 整理数据排序拼接
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
            if (i == 0) {
                a += ager.get(i);
            } else {
                a = a + "&" + ager.get(i);
            }
        }
        return a;
    }

    public static final String JoiningTogether(Map<String,String> map) {
        List<String> ager = new ArrayList<String>();
        for(Map.Entry<String,String> entry:map.entrySet()){//遍历map
            ager.add(entry.getKey() + "=" + entry.getValue());
        }
        Collections.sort(ager);
        String a = "";
        for (int i = 0; i < ager.size(); i++) {
            if (i == 0) {
                a += ager.get(i);
            } else {
                a = a + "&" + ager.get(i);
            }
        }
        return a;
    }

    /**
     * StringBody utf-8
     * @param str
     * @return
     * @throws JSONException
     */

    public static StringBody stringBody(String str) {
        try {
            return new StringBody(str, Charset.forName("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 剪切图片
     * @param fragment
     * @param uri
     */
    public static void crop(Fragment fragment, Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        fragment.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * 剪切图片
     * @param fragment
     * @param uri
     */
    public static void crop(Activity fragment, Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        fragment.startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /**
     * MD5算法
     * @param s
     * @return
     */
    public final static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f' };
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

    public final static String sha1(String data) {
        MessageDigest md;
        StringBuffer buf = null;
        try {
            md = MessageDigest.getInstance("SHA1");
            md.update(data.getBytes());
            buf = new StringBuffer();
            byte[] bits = md.digest();
            for (int i = 0; i < bits.length; i++) {
                int a = bits[i];
                if (a < 0) a += 256;
                if (a < 16) buf.append("0");
                buf.append(Integer.toHexString(a));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return buf.toString();
    }

    /**
     * 从相册获取图片
     * @param fragment
     */
    public static void gallery(Fragment fragment) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /**
     * 从相册获取图片
     * @param fragment
     */
    public static void gallery(Activity fragment) {
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 从相机获取图片
     * @param fragment
     */
    public static void camera(Fragment fragment) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        fragment.startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    /**
     * 从相机获取图片
     * @param fragment
     */
    public static void camera(Activity fragment) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        // 判断存储卡是否可以用，可用进行存储
        if (hasSdcard()) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(Environment
                            .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
        }
        fragment.startActivityForResult(intent, PHOTO_REQUEST_CAMERA);
    }

    /**
     * 将图片变为圆角
     * @param bitmap
     *          原Bitmap图片
     * @param pixels
     *          图片圆角的弧度(单位:像素(px))
     * @return  带有圆角的图片(Bitmap 类型)
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * 设置listview高度
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 设置gridview高度
     * @param gridView
     * @param w
     */
    public static void setGridViewHeightBasedOnChildren(GridView gridView, double w) {
        // 获取对应的adapter
        ListAdapter listAdapter = gridView.getAdapter();
        Class<GridView> tempGridView = GridView.class; // 获得gridview这个类的class
        int column = -1;
        try {
            Field field = tempGridView.getDeclaredField("mRequestedNumColumns"); // 获得申明的字段
            field.setAccessible(true); // 设置访问权限
            column = Integer.valueOf(field.get(gridView).toString()); // 获取字段的值
        } catch (Exception e1) {
        }
        if (column == -1)
            return;
        if (listAdapter == null)
            return;

        int totalHeight = 0;

        int len = listAdapter.getCount();
        int lineNum = len / (column+1) + 1;// 求行数
        if (len > column && len % column == 1) {
            View listItem = listAdapter.getView(0, null, gridView);
            listItem.measure(0, 0); // 计算子项View 的宽高
            totalHeight = listItem.getMeasuredHeight() * lineNum;
        }
        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + 10 * 2;
        if(len==0){
            params.height = 0;
        }else{
            params.height = (int) w*lineNum;
        }
        gridView.setLayoutParams(params);
    }

    /**
     * 将拍下来的照片存放在SD卡中
     * @param data
     * @param path
     * @return
     * @throws IOException
     */
    public static String saveToSDCard(byte[] data, String path)
            throws IOException {
        File jpgFile = new File(path);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
        return jpgFile.toString();
    }

    /**
     * 分解报文
     * @param msg 报文
     * @return 返回map形式的各个域
     */
    synchronized public static Map<String, String> disassembleMessage(String msg) {
        Map<String, String> map = new HashMap<String, String>();
        String key;//键
        String value;//值
        int length;//各个域长度
        String allLength;//报文总长度
        int i = 6;//起始截取位置
        String crcCode = msg.substring(msg.length() - 4,msg.length());
        String crcData = msg.substring(4, msg.length() - 4);
        String f = CRC1021.getCRC1021(ByteUtils.HexString2Bytes(crcData),ByteUtils.HexString2Bytes(crcData).length);
        if (crcCode.equalsIgnoreCase(f)) {
            String hex = Integer.toHexString((msg.length() - 4) / 2);
            if (hex.length() > 2) {
                String a = msg.substring(0, 2);
                String c = msg.substring(2, 4);
                allLength = Integer.valueOf(c) * 100 + Integer.valueOf(a) + "";
            } else
                allLength = msg.substring(0, hex.length());
            if (hex.equalsIgnoreCase(allLength)) {
                while (i < msg.length()) {
                    key = msg.substring(i, i = i + 2);
                    length = Integer.parseInt(msg.substring(i, i = i + 2), 16);
                    value = msg.substring(i, i = i + length * 2);
                    map.put(key, value);
                }
                return map;
            }
        }
        return null;
    }

    synchronized public static String getImagePath() {
        File f = new File(Environment.getExternalStorageDirectory(), "/docotr/");
        if (!f.exists()) {
            f.mkdir();
        } // 如果目录不存在，则创建一个名为"finger"的目录
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
        String imageCameraPath = format.format(date) + ".jpg";
        File file = new File(f, imageCameraPath);
        return file.getPath();
    }

    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){return "";}
        if(format == null || format.isEmpty()) format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }

    /**
     * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
     * @param timeStamp
     * @return
     */
    public static String convertTimeToFormat(long timeStamp) {
        long curTime =System.currentTimeMillis() / (long) 1000 ;
        long time = curTime - timeStamp;
        if (time < 60 && time >= 0) {
            return "刚刚";
        } else if (time >= 60 && time < 3600) {
            return time / 60 + "分钟前";
        } else if (time >= 3600 && time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time >= 3600 * 24 && time < 3600 * 24 * 30) {
            return time / 3600 / 24 + "天前";
        } else if (time >= 3600 * 24 * 30 && time < 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 + "个月前";
        } else if (time >= 3600 * 24 * 30 * 12) {
            return time / 3600 / 24 / 30 / 12 + "年前";
        } else {
            return "刚刚";
        }
    }
}
