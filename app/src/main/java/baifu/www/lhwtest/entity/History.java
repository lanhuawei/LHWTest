package baifu.www.lhwtest.entity;

/**
 * Created by Ivan.L on 2017/7/27.
 * 历史记录
 */

public class History {
    private String code;
    private String info;
    private HistorySubclass result;
    private String currentTime;

    public History() {
        super();
    }

    public History(String code, String info, HistorySubclass result, String currentTime) {
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

    public void setResult(HistorySubclass result) {
        this.result = result;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public HistorySubclass getResult() {
        return result;
    }

    public String getCurrentTime() {
        return currentTime;
    }
    @Override
    public String toString() {
        return "History [code=" + code + ",info=" + info + ",result=" + result +
                ",currentTime=" + currentTime + "]";
    }
}
