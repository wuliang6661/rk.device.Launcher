package rk.device.launcher.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by wuliang on 2017/11/23.
 * <p>
 * 设备配置bean
 */

public class DeviceInfoBean implements Serializable {


    /**
     * mobile : 123333333
     * tlist : [{"id":1,"name":"蓝牙智能锁"},{"id":2,"name":"普通门禁"},{"id":3,"name":"身份证门禁"},{"id":4,"name":"电梯"}]
     */

    private String mobile;
    private List<TlistBean> tlist;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<TlistBean> getTlist() {
        return tlist;
    }

    public void setTlist(List<TlistBean> tlist) {
        this.tlist = tlist;
    }

    public static class TlistBean implements Serializable {
        /**
         * id : 1
         * name : 蓝牙智能锁
         */

        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
