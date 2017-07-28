package baifu.www.lhwtest.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ivan.L on 2017/7/27.
 */

public class GsonTools {
    public GsonTools() {
    }

    /**
     * 使用Gson进行解析Person
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T getBean(String jsonString, Class<T> cls) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 使用Gson进行解析 List<Person>
     * @param jsonString
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> List<T> getBeans(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            Gson gson = new Gson();
            list = gson.fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());
        } catch (Exception e) {
        }
        return list;
    }
}
