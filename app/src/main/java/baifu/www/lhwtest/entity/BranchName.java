package baifu.www.lhwtest.entity;

import java.util.Map;

/**
 * Created by Ivan.L on 2017/7/28.
 * 支行名字实体类
 */

public class BranchName {
    private String code;
    private String info;
    private Map<String,String> result;
    private int currentTime;

    public BranchName() {
        super();
    }

    public BranchName(String code, String info, Map<String, String> result, int currentTime) {
        this.code = code;
        this.info = info;
        this.result = result;
        this.currentTime = currentTime;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        return "BranchName{" + "code='" + code + '\'' + ", info='" + info + '\'' + ", result=" + result +
                ", currentTime=" + currentTime + '}';
    }
}
