package rk.device.launcher.event;

import java.io.Serializable;

/**
 * Created by wuliang on 2017/12/9.
 * <p>
 * ip地址已动态更改，通知首页重新获取数据
 */

public class IpHostEvent implements Serializable {

    private boolean isHost;

    public IpHostEvent(boolean isHost) {
        this.isHost = isHost;
    }


    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
