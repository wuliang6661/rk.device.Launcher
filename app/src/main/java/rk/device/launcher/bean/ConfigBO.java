package rk.device.launcher.bean;

import java.io.Serializable;

/**
 * Created by wuliang on 2018/3/20.
 * <p>
 * 获取配置的类
 */

public class ConfigBO implements Serializable {


    /**
     * currentTime : 1521559114
     * timeUpdate : 0
     * heartbeatInterval : 300
     * managerIpAddr :
     * managerPort : 0
     * uuid : null
     * access_token : null
     */

    private int currentTime;
    private int timeUpdate;
    private int heartbeatInterval;
    private String managerIpAddr;
    private int managerPort;
    private String uuid;
    private String access_token;

    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }

    public int getTimeUpdate() {
        return timeUpdate;
    }

    public void setTimeUpdate(int timeUpdate) {
        this.timeUpdate = timeUpdate;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }

    public String getManagerIpAddr() {
        return managerIpAddr;
    }

    public void setManagerIpAddr(String managerIpAddr) {
        this.managerIpAddr = managerIpAddr;
    }

    public int getManagerPort() {
        return managerPort;
    }

    public void setManagerPort(int managerPort) {
        this.managerPort = managerPort;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
