package baifu.www.lhwtest.entity;

import java.util.List;

/**
 * Created by Ivan.L on 2017/7/27.
 * 银行列表
 */

public class BankCard {
    private String code;
    private String info;
    private List<String> result;
    private int currentTime;

    public BankCard(String code, String info, List<String> result, int currentTime) {
        super();
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

    public void setResult(List<String> result) {
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

    public List<String> getResult() {
        return result;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        return "BankCard [code=" + code + ", info=" + info + ", result="
                + result + ", currentTime=" + currentTime + "]";
    }
}
