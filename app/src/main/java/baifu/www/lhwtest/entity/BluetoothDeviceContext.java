package baifu.www.lhwtest.entity;

/**
 * Created by Ivan.L on 2017/7/25.
 * 存放广播名称地址实体类
 */

public class BluetoothDeviceContext {
    public String name = "";
    public String address = "";
    public BluetoothDeviceContext(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
