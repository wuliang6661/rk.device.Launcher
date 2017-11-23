package rk.device.launcher.bean;

import java.io.Serializable;

/**
 * Created by wuliang on 2017/11/22.
 * <p>
 * 设备关联类型bean
 */

public class DeviceCorrelateBean implements Serializable {

    /**
     * 跟后台对应的类型编号
     */
    private int key;

    /**
     * 类型名称
     */
    private String name;


    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
