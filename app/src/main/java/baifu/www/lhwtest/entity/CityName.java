package baifu.www.lhwtest.entity;

import java.util.List;

/**
 * Created by Ivan.L on 2017/7/28.
 * 城市名
 */

public class CityName {
    String orgCode = null;
    String orgName = null;
    List<CityName2> city = null;

    public CityName() {
        super();
    }

    public CityName(String orgCode, String orgName, List<CityName2> city) {
        this.orgCode = orgCode;
        this.orgName = orgName;
        this.city = city;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public List<CityName2> getCity() {
        return city;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public void setCity(List<CityName2> city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "CityName{" + "orgCode='" + orgCode + '\'' + ", orgName='" + orgName + '\'' +
                ", city=" + city + '}';
    }
}
