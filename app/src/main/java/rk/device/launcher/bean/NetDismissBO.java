package rk.device.launcher.bean;

import java.io.Serializable;

/**
 * Created by wuliang on 2017/11/24.
 * <p>
 * 判断网络是否连接bean
 */

public class NetDismissBO implements Serializable {

    boolean isContect = true;


    public NetDismissBO(boolean isContect) {
        this.isContect = isContect;
    }


    public boolean isConnect() {
        return isContect;
    }

    public void setContect(boolean contect) {
        isContect = contect;
    }
}
