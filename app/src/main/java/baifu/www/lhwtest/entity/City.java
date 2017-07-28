package baifu.www.lhwtest.entity;

import java.util.List;

/**
 * Created by Ivan.L on 2017/7/28.
 * 城市类
 */

public class City {
    private List<CityName> result;
    private String code;
    private String info;
    private long currentTime;

    public City() {
        super();
    }

    public City(List<CityName> result, String code, String info, long currentTime) {
        this.result = result;
        this.code = code;
        this.info = info;
        this.currentTime = currentTime;
    }

    public void setResult(List<CityName> result) {
        this.result = result;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public List<CityName> getResult() {
        return result;
    }

    public String getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    @Override
    public String toString() {
        return "City{" + "result=" + result + ", code='" + code + '\'' + ", info='" + info + '\'' +
                ", currentTime=" + currentTime + '}';
    }
}
