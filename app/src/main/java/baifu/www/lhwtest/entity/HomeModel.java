package baifu.www.lhwtest.entity;

/**
 * Created by Ivan.L on 2017/7/28.
 * 主页面实例
 */

public class HomeModel {
    private int address;
    private String name;

    public HomeModel() {
    }

    public HomeModel(int address, String name) {
        this.address = address;
        this.name = name;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }
}
