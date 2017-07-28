package baifu.www.lhwtest.entity;

import java.util.List;

/**
 * Created by Ivan.L on 2017/7/28.
 * 银行卡视图
 */

public class BankCardView {
    private String code;
    private String info;
    private List<BankCardInfo> result;
    private int currentTime;

    public BankCardView() {super();
    }

    public BankCardView(String code, String info, List<BankCardInfo> result, int currentTime) {
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

    public void setResult(List<BankCardInfo> result) {
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

    public List<BankCardInfo> getResult() {
        return result;
    }

    public int getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        return "BankCardView{" + "code='" + code + '\'' + ", info='" + info + '\'' + ", result=" + result +
                ", currentTime=" + currentTime + '}';
    }
}
