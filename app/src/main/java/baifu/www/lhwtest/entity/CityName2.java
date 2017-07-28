package baifu.www.lhwtest.entity;

/**
 * Created by Ivan.L on 2017/7/28.
 */

public class CityName2 {
    String orgCode;
    String orgName;

    public CityName2() {
        super();
    }

    public CityName2(String orgCode, String orgName) {
        super();
        this.orgCode = orgCode;
        this.orgName = orgName;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    @Override
    public String toString() {
        return "CityName2{" + "orgCode='" + orgCode + '\'' + ", orgName='" + orgName + '\'' + '}';
    }
}
